#src\recomsystem\model.py
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Dict, Optional
import pandas as pd
import Hybrid_Recom_Module as HRM
import json
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()

# CORS 허용 설정
origins = [
    "*",  # 모든 도메인 허용
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,  # 허용할 도메인 설정
    allow_credentials=True,
    allow_methods=["*"],  # 모든 HTTP 메서드 허용
    allow_headers=["*"],  # 모든 헤더 허용
)

# 영화 데이터 준비 (예시로 5페이지의 영화 데이터를 불러옴)
df_movies = tmdb_prepare(n_pages=5)
similarity_matrix = embeddingsNsimilarityCal(df_movies)

# Pydantic 모델 정의: 요청 데이터를 검증할 수 있도록
class RecommendationRequest(BaseModel):
    user_input: Dict  # 사용자 선호 정보 (예: {'preferredGenres': [...], 'preferredActors': [...]})
    user_behavior_data: List[Dict]  # 사용자 행동 데이터 (예: [{'liked_movies': [...]}])
    movie_name: str  # 검색된 영화 이름

class LLMRequest(BaseModel):
    user_prompt: str  # 사용자 프롬프트 입력 데이터

# 영화 추천 API
@app.post("/api/ai/predict")
async def hrm_ai_recommendation(request: RecommendationRequest):
    # 요청에서 데이터 받아오기
    # 필요한 값들 추출
    gender = request.get("gender")
    age = request.get("age")
    preferred_genres = request.get("preferredGenres")
    preferred_actors = request.get("preferredActors")
    movie_name = request.get("target_movie")

    # 새 JSON 형식으로 묶기
    result = {
        "gender": gender,
        "age": age,
        "preferredGenres": preferred_genres,
        "preferredActors": preferred_actors
    }

    # JSON 형식으로 변환 (optional, 출력)
    user_input = json.dumps(result, ensure_ascii=False, indent=4)

    user_behavior_data = None

    if not user_input  or not movie_name:
        raise HTTPException(status_code=400, detail="필수 데이터가 누락되었습니다. user_input, user_behavior_data, movie_name이 필요합니다.")

    try:
        # 추천 함수 호출
        hybrid_recommendations, similar_movies = HRM.do_recommendation(
            user_data=user_input,  # 사용자 선호 데이터
            target_movie=movie_name,  # 전달받은 영화 이름을 타겟 영화로 설정
            df_movies=df_movies,  # 영화 데이터
            similarity_matrix=similarity_matrix,  # 영화 유사도 매트릭스
            user_behavior_data=user_behavior_data  # 사용자 행동 데이터 (협업 필터링에 사용)
        )

        response = {
            "hybrid_recommendations": hybrid_recommendations.to_dict(orient="records"),
            "similar_movies": similar_movies
        }
        return response

    except Exception as e:
        print(f"Error: {e}")
        raise HTTPException(status_code=500, detail="추천 시스템 실행 중 오류가 발생했습니다.")

# LLM 추천 API
@app.post("/api/ai/predict/llm")
async def llm_ai_recommendation(request: LLMRequest):
    # 요청에서 user_prompt 받아오기
    user_prompt = request.user_prompt

    if not user_prompt:
        raise HTTPException(status_code=400, detail="필수 데이터가 누락되었습니다. user_prompt 가 필요합니다.")

    try:
        # 추천 함수 호출
        llm_response = HRM.do_response(user_prompt=user_prompt)
        return {"response": llm_response}

    except Exception as e:
        print(f"Error: {e}")
        raise HTTPException(status_code=500, detail="추천 시스템 실행 중 오류가 발생했습니다.")

# 서버를 8080 포트에서 실행하도록 설정
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=8080)


"""
    # 사용자 데이터 및 행동 데이터
    data = {
             "surveyId": 1,
            "memberId" : 1,
            "gender": "M",
            "age": "30",
            "preferredGenres": ["코미디"],
            "preferredActors": ["황정민"],
            "target_movie" : "inception"
        }
    user_data = {
        "gender": "M",
        "age": "30",
        "preferredGenres": ["코미디"],
        "preferredActors": ["황정민"]
    }
    user_behavior_data = [
        {"user_id": 1, "liked_movies": ["명량", "베테랑"]},
        {"user_id": 2, "liked_movies": ["명량"]},
        {"user_id": 3, "liked_movies": ["베테랑"]},
    ]
    """

      """
    "트랜스포머같은 영화를 추천해 주세요."
      """



   """
  연동 구조 요약:
   - FastAPI는 Python 코드에서 API를 제공하고, Spring Boot는 FastAPI API를 호출하여 데이터를 받아옵니다.
   - **AIModelService.java**에서는 FastAPI 서버를 실행하지 않고, 외부 프로세스로 Python 스크립트를 호출합니다.
   - Spring Boot는 FastAPI로부터 JSON 응답을 받아 처리하고, **AIController**에서 이를 클라이언트에 전달합니다.
   - FastAPI 서버는 Spring Boot 내에서 외부 프로세스로 실행되며, 포트 충돌을 피하기 위해 Spring Boot가 모든 HTTP 요청을 처리합니다.
   """
