import Hybrid_Recom_Module as HRM
import pandas as pd


# 사용자 데이터 및 행동 데이터
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


num_pages = 1 # TMDB에서 가져올 영화의 페이지 수 (1페이지당 20개)
genre_id = 35 # TMDB 영화 장르 아이디
target_movie = "인터스텔라" # 유사한 영화의 타겟 영화


# TMDB 데이터 준비
df_movies = HRM.tmdb_prepare(n_pages=num_pages, genre_id=genre_id)
# 임베딩 생성 및 유사도 계산
similarity_matrix = HRM.embeddingsNsimilarityCal(df_movies=df_movies)


# 추천
hybrid_recommendations, similar_movies = HRM.do_recommendation(user_data=user_data, target_movie=target_movie,
                                                               df_movies=df_movies, similarity_matrix=similarity_matrix,
                                                               user_behavior_data=user_behavior_data)

print(f"Hybrid 추천 결과:\n{hybrid_recommendations}")
print(f"'{target_movie}'와 유사한 영화 추천 리스트: {similar_movies}")
