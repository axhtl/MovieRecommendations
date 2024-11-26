from flask import Blueprint, request, jsonify
from .models import recommend_movies  # 추천 알고리즘 함수

ai_routes = Blueprint('ai_routes', __name__)

# 영화 추천 API 엔드포인트
@ai_routes.route('/recommend', methods=['POST'])
def recommend():
    data = request.get_json()  # POST 요청으로 전달된 JSON 데이터 받기
    user_preferences = data.get('preferences', {})  # 사용자 취향 데이터 받기
    
    # 추천 알고리즘 호출
    recommendations = recommend_movies(user_preferences)
    
    return jsonify({'recommendations': recommendations})  # 추천 결과를 JSON 형식으로 반환