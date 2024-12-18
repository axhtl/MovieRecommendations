from fastapi import FastAPI, HTTPException, Request, Body
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, validator
from typing import List, Optional
import pandas as pd
import Hybrid_Recom_Module as HRM
import Moviepick_LLM_Module as MLM
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
import logging
import json
import pickle

app = FastAPI()

# CORS 미들웨어 추가
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 모든 도메인 허용 (배포 시 특정 도메인으로 제한 필요)
    allow_credentials=True,
    allow_methods=["*"],  # 모든 HTTP 메서드 허용
    allow_headers=["*"],  # 모든 HTTP 헤더 허용
)

logging.basicConfig(level=logging.ERROR)
logger = logging.getLogger(__name__)

# 요청 검증 에러 핸들러 추가
@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError):
    # 상세 에러 메시지 추출
    errors = exc.errors()
    detailed_errors = []
    for error in errors:
        detailed_errors.append({
            "loc": error.get("loc"),  # 오류 위치 (e.g., body, query)
            "msg": error.get("msg"),  # 오류 메시지
            "type": error.get("type")  # 오류 타입
        })

    return JSONResponse(
        status_code=422,
        content={
            "message": "Validation error occurred",
            "details": detailed_errors,
        },
    )

@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    logger.error(f"Unhandled exception: {exc}", exc_info=True)
    return JSONResponse(
        status_code=500,
        content={"message": "Internal server error", "details": str(exc)},
    )


# Pydantic 모델 정의
class Member(BaseModel):
    memberId: Optional[int]
    membername: Optional[str]
    password: Optional[str]
    nickname: Optional[str]
    role: Optional[str]
    memberStatus: Optional[str]
    refreshToken: Optional[str] = None

class Survey(BaseModel):
    surveyId: Optional[int]
    memberId: Optional[int]
    gender: Optional[str]
    age: Optional[str]

class Review(BaseModel):
    reviewId: Optional[int]
    movieId: Optional[int]
    ranked: Optional[str]

class MovieInfo(BaseModel):
    movieInfoId: Optional[int]
    reviewId: Optional[int]
    movieId: Optional[int]
    title: Optional[str]
    originalTitle: Optional[str]
    runtime: Optional[str]
    releaseDate: Optional[str]
    originCountry: Optional[str]

class ReviewInfo(BaseModel):
    reviewId: Optional[int]
    movieId: Optional[int]
    movieInfo: MovieInfo
    actors: List[str]
    directors: List[str]
    genres: List[str]

class Data(BaseModel):
    member: Member
    survey: Survey
    preferredGenres: List[str]
    preferredActors: List[str]
    reviews: List[Review]
    reviewInfos: List[ReviewInfo]


@app.post("/test")
async def receive_data(data: Data):
    print(data)

@app.post("/recom-hybrid")
async def receive_data(data: Data):
    try:

        # 요청 데이터 확인
        print("1. Received data:\n", data)


        # JSON 데이터를 DataFrame으로 변환
        member_df = pd.DataFrame([data.member.dict()])
        print("2. Member DataFrame:\n", member_df)

        survey_df = pd.DataFrame([data.survey.dict()])
        preferred_genres_df = pd.DataFrame(data.preferredGenres, columns=["preferredGenres"])
        preferred_actors_df = pd.DataFrame(data.preferredActors, columns=["preferredActors"])
        reviews_df = pd.DataFrame([review.dict() for review in data.reviews])
        review_infos_df = pd.DataFrame([
            {
                **review_info.dict(),
                "movieInfo": review_info.movieInfo.dict()
            }
            for review_info in data.reviewInfos
        ])

        user_data = {
            "gender": survey_df.loc[0, "gender"],
            "age": survey_df.loc[0, "age"],
            "preferredGenres": preferred_genres_df["preferredGenres"].tolist(),
            "preferredActors": preferred_actors_df["preferredActors"].tolist()
        }

        user_behavior_data = [
        ]
        
        num_pages = 1
        genre_id = 35

        last_review = data.reviews[-1]  # 객체 속성 접근 방식으로 수정
        last_movie_id = last_review.movieId
        target_movie = HRM.get_movie_title(last_movie_id)
        print("---TARGET MOVIE---")
        print(target_movie)

        # df_movies = HRM.tmdb_prepare(n_pages=num_pages, genre_id=genre_id)
        df_movies = pd.read_pickle("/home/t24326/svr/AI/recomsystem/data/tmdb_all_movies.pkl")
        print("3. Movies DataFrame:\n", df_movies)

        # similarity_matrix = HRM.embeddingsNsimilarityCal(df_movies=df_movies)
        with open('/home/t24326/isfolder/tmdb_prepare/similarity_matrix.pkl', 'rb') as f:
            similarity_matrix = pickle.load(f)
        print("4. Similarity Matrix:\n", similarity_matrix)

        # user_behavior OK
        # hybrid_recommendations, similar_movies = HRM.do_recommendation(user_data=user_data, target_movie=target_movie,
        #                                                        df_movies=df_movies, similarity_matrix=similarity_matrix,
        #                                                        user_behavior_data=user_behavior_data)
        
        # user_behavior No OK
        hybrid_recommendations, similar_movies = HRM.do_recommendation(user_data=user_data, target_movie=target_movie,
                                                               df_movies=df_movies, similarity_matrix=similarity_matrix,
                                                               user_behavior_data=user_behavior_data)

        
        hybrid_result = hybrid_recommendations[["movieNm", "movieCd"]].values.tolist()
        h_result = [{"movieNm": movie[0], "movieCd": movie[1]} for movie in hybrid_result]
        
        print("5. Compelete:\n")
        print(h_result)

        # String 형태로 변환
        h_result_string = json.dumps(h_result, ensure_ascii=False)

        return h_result
    
    except Exception as e:
        print(f"Error occurred: {e}")
        raise HTTPException(status_code=400, detail=str(e))


@app.post("/llm")
async def llm_activate(text: str = Body(..., embed=True)):

    user_prompt = text
    llm_response = MLM.chat(
        user_prompt=user_prompt
    )

    return {"llm_response": llm_response}

@app.post("/llm-reason")
async def llm_reason(text: str = Body(..., embed=True)):

    try:
        # 문자열을 정수로 변환
        movie_id = int(text)
    except ValueError:
        # 숫자로 변환할 수 없을 때 예외 처리
        return {"error": f"Invalid input: '{text}' is not a valid number."}

    # HRM 모듈의 함수 호출
    movie_title = HRM.get_movie_title(movie_id)

    llm_response = MLM.explain_response(movie_title)
    
    return {"llm_response": llm_response}



if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=8080)
