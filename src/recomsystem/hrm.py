from flask import Flask, request, jsonify
import pandas as pd
import Hybrid_Recom_Module as HRM
from transformers import AutoModelForCausalLM, AutoTokenizer
import json
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # 모든 도메인에서 접근 허용

# 영화 데이터 준비 (예시로 5페이지의 영화 데이터를 불러옴)
df_movies = tmdb_prepare(n_pages=5)
similarity_matrix = embeddingsNsimilarityCal(df_movies)

# Spring Boot의 /api/ai/predict와 동일한 경로로 수정
@app.route('/api/ai/predict', methods=['POST'])
def ai_recommendation():
    # 클라이언트로부터 요청 데이터를 받아옴
    data = request.get_json()

    # 요청에서 필요한 데이터 추출
    survey_id = data.get('surveyId')  # 설문 ID (필요에 따라 사용)
    member_id = data.get('memberId')  # 회원 ID
    gender = data.get('gender')  # 성별
    age = data.get('age')  # 나이
    preferred_genres = data.get('preferredGenres')  # 선호 장르
    preferred_actors = data.get('preferredActors')  # 선호 배우
    target_movie = data.get('target_movie')  # 추천할 영화 이름

    # user_input 구성
    user_input = {
        "gender": gender,
        "age": age,
        "preferredGenres": preferred_genres,
        "preferredActors": preferred_actors
    }

    # user_behavior_data는 null로 설정
    user_behavior_data = None

    # 필수 데이터가 없는 경우 예외 처리
    if not user_input or not target_movie:
        return jsonify({"error": "필수 데이터가 누락되었습니다. user_input, target_movie이 필요합니다."}), 400

    # 추천 시스템 실행
    try:
        # 추천 함수 호출: movie_name을 타겟 영화로 사용하여 추천 시스템 실행
        hybrid_recommendations, similar_movies = do_recommendation(
            user_data=user_input,  # 사용자 선호 데이터
            target_movie=target_movie,  # 전달받은 영화 이름을 타겟 영화로 설정
            df_movies=df_movies,  # 영화 데이터
            similarity_matrix=similarity_matrix,  # 영화 유사도 매트릭스
            user_behavior_data=user_behavior_data  # 사용자 행동 데이터 (null로 설정)
        )

        # 추천된 영화 목록을 포함한 응답 반환
        response = {
            "hybrid_recommendations": hybrid_recommendations.to_dict(orient="records"),
            "similar_movies": similar_movies
        }
        return jsonify(response)

    except Exception as e:
        # 예외 처리: 추천 시스템 실행 중 오류 발생 시
        print(f"Error: {e}")
        return jsonify({"error": "추천 시스템 실행 중 오류가 발생했습니다."}), 500

# Flask 서버는 여기서 직접 실행하지 않음. Spring Boot에서 실행됨.


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
   연동 구조 요약:
    - Flask는 Python 코드에서 API를 제공하고, Spring Boot는 Flask API를 호출하여 데이터를 받아옵니다.
    - **AIModelService.java**에서 Flask 서버를 실행하지 않고, 외부 프로세스로 Python 스크립트를 호출합니다.
    - Spring Boot는 Flask로부터 JSON 응답을 받아서 처리하고, **AIController**에서 이를 클라이언트에 전달합니다.
    - Flask 서버는 Spring Boot 내에서 외부 프로세스로 실행되며, 포트 충돌을 피하기 위해 Spring Boot가 모든 HTTP 요청을 처리합니다.
   """