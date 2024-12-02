# Hybrid_Recom_Module.py
from transformers import AutoTokenizer, AutoModel
import torch
from sklearn.metrics.pairwise import cosine_similarity
import pandas as pd
import requests
import os
from dotenv import load_dotenv

# 환경 변수 로드
load_dotenv()

HUGGINGFACE_TOKEN = os.getenv("HUGGINGFACE_TOKEN")  # Huggingface 인증 토큰
TMDB_API_KEY = os.getenv("TMDB_API_KEY")  # TMDB API 키

# 사전 학습된 모델과 토크나이저 로드
model_name = "meta-llama/Llama-3.2-3B-Instruct"
tokenizer = AutoTokenizer.from_pretrained(model_name, use_auth_token=HUGGINGFACE_TOKEN)
model = AutoModel.from_pretrained(model_name, use_auth_token=HUGGINGFACE_TOKEN)

# 토크나이저에 패딩 토큰이 없으면 추가
if tokenizer.pad_token is None:
    tokenizer.add_special_tokens({'pad_token': '[PAD]'})
    model.resize_token_embeddings(len(tokenizer))

# 영화 장르 정보를 가져오는 함수
def fetch_genres():
    url = "https://api.themoviedb.org/3/genre/movie/list"
    params = {"api_key": TMDB_API_KEY, "language": "ko-KR"}
    response = requests.get(url, params=params)  # TMDB API 호출
    data = response.json()
    # 장르 ID와 이름을 매핑하여 반환
    return {genre["id"]: genre["name"] for genre in data.get("genres", [])}

# 특정 장르에 속하는 영화들을 가져오는 함수
def fetch_movies_by_genre(genre_id, n_pages=5):
    url = "https://api.themoviedb.org/3/discover/movie"
    all_movies = []
    # 여러 페이지에 걸쳐 영화 데이터를 가져옴
    for page in range(1, n_pages + 1):
        params = {"api_key": TMDB_API_KEY, "with_genres": genre_id, "language": "ko-KR", "page": page}
        response = requests.get(url, params=params)
        data = response.json()
        all_movies.extend(data.get("results", []))  # 영화 데이터를 리스트에 추가
    return all_movies

# TMDB에서 가져온 영화 데이터를 원하는 형식으로 처리하는 함수
def process_tmdb_data(tmdb_movies, genres):
    processed_movies = []
    for movie in tmdb_movies:
        # 영화 정보 처리 후 딕셔너리로 변환
        processed_movies.append({
            "movieCd": movie.get("id"),
            "movieNm": movie.get("title"),
            "movieEn": movie.get("original_title"),
            "showTm": movie.get("runtime", 0),
            "openDt": movie.get("release_date"),
            "typeNm": "장편",
            "nations": ["한국" if movie.get("original_language") == "ko" else "해외"],
            "genres": [genres.get(genre_id, "") for genre_id in movie.get("genre_ids", [])],
            "directors": [],
            "actors": [],
            "audits": "관람가"
        })
    return processed_movies

# 영화의 상세 정보를 가져오는 함수
def fetch_movie_details(movie_id):
    url = f"https://api.themoviedb.org/3/movie/{movie_id}"
    params = {"api_key": TMDB_API_KEY, "language": "ko-KR", "append_to_response": "credits"}
    response = requests.get(url, params=params)  # 영화 상세 정보와 크레딧(감독, 배우 등)을 가져옴
    return response.json()

# 영화 목록에 대한 추가 정보(감독, 배우 등)를 업데이트하는 함수
def update_movie_details(df_movies):
    for idx, movie in df_movies.iterrows():
        movie_details = fetch_movie_details(movie["movieCd"])
        directors = [crew["name"] for crew in movie_details.get("credits", {}).get("crew", []) if crew["job"] == "Director"]
        df_movies.at[idx, "directors"] = directors
        actors = [cast["name"] for cast in movie_details.get("credits", {}).get("cast", [])][:10]
        df_movies.at[idx, "actors"] = actors
    return df_movies

# 영화 이름으로 영화를 검색하는 함수
def search_movie_by_name(movie_name):
    url = "https://api.themoviedb.org/3/search/movie"
    params = {"api_key": TMDB_API_KEY, "query": movie_name, "language": "ko-KR"}
    response = requests.get(url, params=params)  # 영화 이름으로 TMDB에서 검색
    return response.json().get("results", [])

# 하이브리드 필터링을 통해 영화 추천을 위한 점수를 계산하는 함수
def hybrid_filtering(user_input, df_movies, user_behavior_data):
    # 사용자 선호 장르와 영화 장르의 일치 여부를 기반으로 점수 계산
    df_movies["genre_score"] = df_movies["genres"].apply(
        lambda genres: len(set(genres) & set(user_input["preferredGenres"]))
    )
    # 사용자 선호 배우와 영화 배우의 일치 여부를 기반으로 점수 계산
    df_movies["actor_score"] = df_movies["actors"].apply(
        lambda actors: len(set(actors) & set(user_input["preferredActors"]))
    )
    # 콘텐츠 기반 점수 계산 (장르 점수 * 0.6 + 배우 점수 * 0.4)
    df_movies["content_score"] = df_movies["genre_score"] * 0.6 + df_movies["actor_score"] * 0.4

    # 협업 필터링을 위한 점수 계산
    user_behavior_score = []
    for movie in df_movies["movieNm"]:
        score = 0
        # 사용자가 좋아한 영화에 따라 점수 추가
        for behavior in user_behavior_data:
            if movie in behavior["liked_movies"]:
                score += 1
        user_behavior_score.append(score)
    df_movies["collaborative_score"] = user_behavior_score
    # 하이브리드 점수 계산 (콘텐츠 점수 * 0.5 + 협업 점수 * 0.5)
    df_movies["hybrid_score"] = df_movies["content_score"] * 0.5 + df_movies["collaborative_score"] * 0.5
    # 점수가 높은 순으로 영화 정렬
    return df_movies.sort_values("hybrid_score", ascending=False)

# 텍스트 데이터를 임베딩 벡터로 변환하는 함수
def generate_embeddings(text_list, tokenizer, model):
    inputs = tokenizer(text_list, padding=True, truncation=True, return_tensors="pt")
    with torch.no_grad():
        outputs = model(**inputs)
        embeddings = outputs.last_hidden_state.mean(dim=1)  # 평균 풀링을 통해 텍스트 임베딩 생성
    return embeddings

# 영화 추천 함수 (하이브리드 추천 시스템)
def recommend_movies(user_input, target_movie, df_movies, similarity_matrix, user_behavior_data, top_n=5):
    # 타겟 영화가 데이터베이스에 없으면 TMDB에서 검색하여 추가
    if target_movie not in df_movies["movieNm"].values:
        search_results = search_movie_by_name(target_movie)
        if search_results:
            genres = fetch_genres()
            processed_movies = process_tmdb_data(search_results, genres)
            df_movies = pd.concat([df_movies, pd.DataFrame(processed_movies)], ignore_index=True)
            df_movies = update_movie_details(df_movies)
        else:
            raise ValueError(f"'{target_movie}'를 찾을 수 없습니다. 다른 타겟 영화를 선택하세요.")

    # 영화 메타텍스트로부터 임베딩 생성
    df_movies["meta_text"] = df_movies.apply(
        lambda row: f"{row['movieNm']} {row['genres']} {row['directors']} {row['actors']}", axis=1
    )
    text_list = df_movies["meta_text"].tolist()
    embeddings = generate_embeddings(text_list, tokenizer, model)
    similarity_matrix = cosine_similarity(embeddings)  # 영화들 간의 유사도 계산

    # 하이브리드 필터링 후 추천
    df_movies = hybrid_filtering(user_input, df_movies, user_behavior_data)
    target_index = df_movies[df_movies["movieNm"] == target_movie].index[0]
    similarity_scores = list(enumerate(similarity_matrix[target_index]))
    similarity_scores = sorted(similarity_scores, key=lambda x: x[1], reverse=True)
    similar_movies = [df_movies.iloc[i[0]]["movieNm"] for i in similarity_scores[1:top_n + 1]]

    return df_movies[["movieNm", "hybrid_score"]].head(top_n), similar_movies

# TMDB에서 영화 데이터를 가져와 처리하는 함수
def tmdb_prepare(n_pages=None, genre_id=None):
    genres = fetch_genres()
    movies = fetch_movies_by_genre(genre_id=genre_id, n_pages=n_pages)
    processed_movies = process_tmdb_data(movies, genres)
    df_movies = pd.DataFrame(processed_movies)
    df_movies = update_movie_details(df_movies)

    return df_movies

# 임베딩과 유사도를 계산하는 함수
def embeddingsNsimilarityCal(df_movies=None):
    df_movies["meta_text"] = df_movies.apply(
    lambda row: f"{row['movieNm']} {row['genres']} {row['directors']} {row['actors']}", axis=1
    )
    text_list = df_movies["meta_text"].tolist()
    embeddings = generate_embeddings(text_list, tokenizer, model)
    similarity_matrix = cosine_similarity(embeddings)

    return similarity_matrix

# 추천 시스템을 실행하는 최종 함수
def do_recommendation(user_data, target_movie, df_movies, similarity_matrix, user_behavior_data):
    hybrid_recommendations, similar_movies = recommend_movies(
    user_input=user_data,
    target_movie=target_movie,
    df_movies=df_movies,
    similarity_matrix=similarity_matrix,
    user_behavior_data=user_behavior_data
    )

    return hybrid_recommendations, similar_movies
