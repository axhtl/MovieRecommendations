from flask import Flask, request, jsonify
import pandas as pd
from .model import Hybrid_Recom_Module
#from Hybrid_Recom_Module import do_recommendation, tmdb_prepare, embeddingsNsimilarityCal
import json

app = Flask(__name__)

# 영화 데이터 준비 (예시로 5페이지의 영화 데이터를 불러옴)
df_movies = tmdb_prepare(n_pages=5)
similarity_matrix = embeddingsNsimilarityCal(df_movies)

@app.route('/api/ai-recommendation', methods=['POST'])
def ai_recommendation():
    # 클라이언트로부터 요청 데이터를 받아옴
    data = request.get_json()

    # 요청에서 user_input (사용자 선호 데이터)과 user_behavior_data (사용자 행동 데이터) 받기
    user_input = data.get('user_input')  # 사용자 선호 정보 (예: {'preferredGenres': ['Action', 'Comedy'], 'preferredActors': ['Actor1']})
    user_behavior_data = data.get('user_behavior_data')  # 사용자 행동 데이터 (예: [{'liked_movies': ['Movie1', 'Movie2']}, ...])
    movie_name = data.get('movie_name')  # 추가된 부분: 검색된 영화 이름을 받음

    if not user_input or not user_behavior_data or not movie_name:
        return jsonify({"error": "필수 데이터가 누락되었습니다. user_input, user_behavior_data, movie_name이 필요합니다."}), 400

    # 추천 시스템 실행
    try:
        # 추천 함수 호출: movie_name을 타겟 영화로 사용하여 추천 시스템 실행
        hybrid_recommendations, similar_movies = do_recommendation(
            user_data=user_input,  # 사용자 선호 데이터
            target_movie=movie_name,  # 전달받은 영화 이름을 타겟 영화로 설정
            df_movies=df_movies,  # 영화 데이터
            similarity_matrix=similarity_matrix,  # 영화 유사도 매트릭스
            user_behavior_data=user_behavior_data  # 사용자 행동 데이터 (협업 필터링에 사용)
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

if __name__ == '__main__':
    app.run(debug=True)
