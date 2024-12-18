# Hybrid_Recom_Module.py
from transformers import AutoTokenizer, AutoModel
import torch
from sklearn.metrics.pairwise import cosine_similarity
import pandas as pd
import requests
import os
from dotenv import load_dotenv
import pickle

# 환경 변수 로드
load_dotenv()

HUGGINGFACE_TOKEN = os.getenv("HUGGINGFACE_TOKEN")
TMDB_API_KEY = os.getenv("TMDB_API_KEY")

model_name = "meta-llama/Llama-3.2-3B-Instruct"
tokenizer = AutoTokenizer.from_pretrained(model_name, use_auth_token=HUGGINGFACE_TOKEN)
model = AutoModel.from_pretrained(model_name, use_auth_token=HUGGINGFACE_TOKEN)

if tokenizer.pad_token is None:
    tokenizer.add_special_tokens({'pad_token': '[PAD]'})
    model.resize_token_embeddings(len(tokenizer))

def fetch_genres():
    url = "https://api.themoviedb.org/3/genre/movie/list"
    params = {"api_key": TMDB_API_KEY, "language": "ko-KR"}
    response = requests.get(url, params=params)
    data = response.json()
    return {genre["id"]: genre["name"] for genre in data.get("genres", [])}

def fetch_movies_by_genre(genre_id, n_pages=5):
    url = "https://api.themoviedb.org/3/discover/movie"
    all_movies = []
    for page in range(1, n_pages + 1):
        params = {"api_key": TMDB_API_KEY, "with_genres": genre_id, "language": "ko-KR", "page": page}
        response = requests.get(url, params=params)
        data = response.json()
        all_movies.extend(data.get("results", []))
    return all_movies

def process_tmdb_data(tmdb_movies, genres):
    processed_movies = []
    for movie in tmdb_movies:
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

def fetch_movie_details(movie_id):
    url = f"https://api.themoviedb.org/3/movie/{movie_id}"
    params = {"api_key": TMDB_API_KEY, "language": "ko-KR", "append_to_response": "credits"}
    response = requests.get(url, params=params)
    return response.json()

def update_movie_details(df_movies):
    for idx, movie in df_movies.iterrows():
        movie_details = fetch_movie_details(movie["movieCd"])
        directors = [crew["name"] for crew in movie_details.get("credits", {}).get("crew", []) if crew["job"] == "Director"]
        df_movies.at[idx, "directors"] = directors
        actors = [cast["name"] for cast in movie_details.get("credits", {}).get("cast", [])][:10]
        df_movies.at[idx, "actors"] = actors
    return df_movies

def search_movie_by_name(movie_name):
    url = "https://api.themoviedb.org/3/search/movie"
    params = {"api_key": TMDB_API_KEY, "query": movie_name, "language": "ko-KR"}
    response = requests.get(url, params=params)
    return response.json().get("results", [])

# def hybrid_filtering(user_input, df_movies, user_behavior_data=None):
#     df_movies["genre_score"] = df_movies["genres"].apply(
#         lambda genres: len(set(genres) & set(user_input["preferredGenres"]))
#     )
#     df_movies["actor_score"] = df_movies["actors"].apply(
#         lambda actors: len(set(actors) & set(user_input["preferredActors"]))
#     )
#     df_movies["content_score"] = df_movies["genre_score"] * 0.6 + df_movies["actor_score"] * 0.4
#     user_behavior_score = []
#     for movie in df_movies["movieNm"]:
#         score = 0
#         for behavior in user_behavior_data:
#             if movie in behavior["liked_movies"]:
#                 score += 1
#         user_behavior_score.append(score)
#     df_movies["collaborative_score"] = user_behavior_score
#     df_movies["hybrid_score"] = df_movies["content_score"] * 0.5 + df_movies["collaborative_score"] * 0.5
#     return df_movies.sort_values("hybrid_score", ascending=False)

def hybrid_filtering(user_input, df_movies, user_behavior_data=None):
    df_movies["genre_score"] = df_movies["genres"].apply(
        lambda genres: len(set(genres) & set(user_input["preferredGenres"]))
    )
    df_movies["actor_score"] = df_movies["actors"].apply(
        lambda actors: len(set(actors) & set(user_input["preferredActors"]))
    )
    df_movies["content_score"] = df_movies["genre_score"] * 0.6 + df_movies["actor_score"] * 0.4

    # 하이브리드 점수 = 콘텐츠 점수만 활용
    df_movies["hybrid_score"] = df_movies["content_score"]
    
    return df_movies.sort_values("hybrid_score", ascending=False)

def generate_embeddings(text_list, tokenizer, model):
    inputs = tokenizer(text_list, padding=True, truncation=True, return_tensors="pt")
    with torch.no_grad():
        outputs = model(**inputs)
        embeddings = outputs.last_hidden_state.mean(dim=1)
    return embeddings

# def recommend_movies(user_input, target_movie, df_movies, similarity_matrix, user_behavior_data=None, top_n=5):
#     print("---RECOM MOVIE FUNC---")
#     if target_movie not in df_movies["movieNm"].values:
#         # search_results = search_movie_by_name(target_movie)
#         # if search_results:
#         #     genres = fetch_genres()
#         #     processed_movies = process_tmdb_data(search_results, genres)
#         #     df_movies = pd.concat([df_movies, pd.DataFrame(processed_movies)], ignore_index=True)
#         #     df_movies = update_movie_details(df_movies)
#         # else:
#         #     raise ValueError(f"'{target_movie}'를 찾을 수 없습니다. 다른 타겟 영화를 선택하세요.")
        
#         # df_movies["meta_text"] = df_movies.apply(
#         #     lambda row: f"{row['movieNm']} {row['genres']} {row['directors']} {row['actors']}", axis=1
#         # )
#         # text_list = df_movies["meta_text"].tolist()
#         # embeddings = generate_embeddings(text_list, tokenizer, model)

#         print("---LOAD EMB PKL---")
#         save_path = "/home/t24326/isfolder/tmdb_prepare/recom_movie_emb.pkl"
#         with open(save_path, "rb") as f:
#             embeddings = pickle.load(f)

#         similarity_matrix = cosine_similarity(embeddings)
    
#     df_movies = hybrid_filtering(user_input, df_movies, user_behavior_data)
#     target_index = df_movies[df_movies["movieNm"] == target_movie].index[0]
#     similarity_scores = list(enumerate(similarity_matrix[target_index]))
#     similarity_scores = sorted(similarity_scores, key=lambda x: x[1], reverse=True)

#     # 추천 영화의 이름과 영화 ID를 함께 저장
#     similar_movies = [
#         {"movieNm": df_movies.iloc[i[0]]["movieNm"], "movieCd": df_movies.iloc[i[0]]["movieCd"]}
#         for i in similarity_scores[1:top_n + 1]
#     ]
    
#     # 하이브리드 추천 결과에 영화 이름과 영화 ID 포함
#     hybrid_recommendations = df_movies[["movieNm", "movieCd", "hybrid_score"]].head(top_n)
    
#     return hybrid_recommendations, similar_movies

def recommend_movies(user_input, target_movie, df_movies, similarity_matrix, user_behavior_data=None, top_n=5):
    print("---RECOM MOVIE FUNC---")
    
    # df_movies가 비어 있는지 확인
    if df_movies.empty:
        raise ValueError("영화 데이터프레임이 비어 있습니다. 데이터를 확인하세요.")
    
    # target_movie가 존재하지 않을 경우 처리
    if target_movie not in df_movies["movieNm"].values:
        print(f"'{target_movie}'를 찾을 수 없습니다. 기본 동작을 실행합니다.")
        # 타겟 영화가 없는 경우 임의의 타겟 영화 선택 (첫 번째 영화로 설정)
        target_movie = df_movies.iloc[0]["movieNm"]
        print(f"타겟 영화를 '{target_movie}'로 대체합니다.")
    
    # 임베딩 로드 및 코사인 유사도 계산
    print("---LOAD EMB PKL---")
    save_path = "/home/t24326/isfolder/tmdb_prepare/recom_movie_emb.pkl"
    with open(save_path, "rb") as f:
        embeddings = pickle.load(f)
    
    if embeddings.size == 0:
        raise ValueError("임베딩 데이터가 비어 있습니다. 임베딩 생성 과정을 확인하세요.")
    
    similarity_matrix = cosine_similarity(embeddings)

    # 하이브리드 필터링
    df_movies = hybrid_filtering(user_input, df_movies, user_behavior_data)

    # target_movie의 인덱스 찾기
    target_index = df_movies[df_movies["movieNm"] == target_movie].index[0]

    # 유사도 점수 계산
    similarity_scores = list(enumerate(similarity_matrix[target_index]))
    similarity_scores = sorted(similarity_scores, key=lambda x: x[1], reverse=True)

    # 추천 영화 추출
    similar_movies = [
        {"movieNm": df_movies.iloc[i[0]]["movieNm"], "movieCd": df_movies.iloc[i[0]]["movieCd"]}
        for i in similarity_scores[1:top_n + 1]
    ]
    
    # 하이브리드 추천 결과
    hybrid_recommendations = df_movies[["movieNm", "movieCd", "hybrid_score"]].head(top_n)
    
    return hybrid_recommendations, similar_movies




def tmdb_prepare_from_file(file_path="/home/ccl/Desktop/isfolder/P_project/llm_test_tmp/tmdb_data/all_movies.txt"):
    """
    미리 저장된 all_movies.txt 파일에서 데이터를 읽어와 DataFrame을 반환하는 함수.
    :param file_path: 영화 데이터를 저장한 파일 경로
    :return: 영화 데이터 DataFrame
    """
    import pandas as pd
    
    # 파일 읽기
    with open(file_path, "r", encoding="utf-8") as file:
        data = file.read()

    # 영화 데이터를 파싱
    movies = []
    for movie_block in data.split("---"):
        lines = movie_block.strip().split("\n")
        movie = {}
        for line in lines:
            if line.startswith("Title:"):
                movie["movieNm"] = line.split("Title:")[1].strip()
            elif line.startswith("Overview:"):
                movie["overview"] = line.split("Overview:")[1].strip() or "정보 없음"
            elif line.startswith("Release Date:"):
                movie["release_date"] = line.split("Release Date:")[1].strip()
            elif line.startswith("Vote Average:"):
                movie["vote_average"] = float(line.split("Vote Average:")[1].strip())
            elif line.startswith("Genres:"):
                movie["genres"] = line.split("Genres:")[1].strip().split(", ")
        
        # 추가된 기본 값 (적절히 수정 가능)
        movie["movieCd"] = hash(movie.get("movieNm", ""))
        movie["directors"] = []
        movie["actors"] = []
        movie["showTm"] = 0
        movie["nations"] = ["해외"]  # 한국 영화인지 여부는 파일에 따라 수정 가능
        movie["audits"] = "관람가"
        movie["typeNm"] = "장편"
        
        movies.append(movie)

    # DataFrame 생성
    df_movies = pd.DataFrame(movies)

    return df_movies

def tmdb_prepare2(file_path="/home/ccl/Desktop/isfolder/P_project/llm_test_tmp/tmdb_data/all_movies.txt"):
    """
    미리 저장된 영화 데이터 파일에서 데이터를 읽어와 DataFrame으로 변환.
    """
    return tmdb_prepare_from_file(file_path=file_path)

def tmdb_prepare(n_pages=None, genre_id=None):
    genres = fetch_genres()
    movies = fetch_movies_by_genre(genre_id=genre_id, n_pages=n_pages)
    processed_movies = process_tmdb_data(movies, genres)
    df_movies = pd.DataFrame(processed_movies)
    df_movies = update_movie_details(df_movies)

    return df_movies

def embeddingsNsimilarityCal(df_movies=None,):
    df_movies["meta_text"] = df_movies.apply(
    lambda row: f"{row['movieNm']} {row['genres']} {row['directors']} {row['actors']}", axis=1
    )
    text_list = df_movies["meta_text"].tolist()
    embeddings = generate_embeddings(text_list, tokenizer, model)
    similarity_matrix = cosine_similarity(embeddings)

    return similarity_matrix

def do_recommendation(user_data, target_movie, df_movies, similarity_matrix, user_behavior_data=None):
    print("---DO RECOM FUNC---")
    hybrid_recommendations, similar_movies = recommend_movies(
    user_input=user_data,
    target_movie=target_movie,
    df_movies=df_movies,
    similarity_matrix=similarity_matrix,
    user_behavior_data=user_behavior_data
    )

    return hybrid_recommendations, similar_movies

def get_movie_title(movie_id):
    url = f"https://api.themoviedb.org/3/movie/{movie_id}"
    params = {
        "api_key": TMDB_API_KEY,
        "language": "ko-KR"  # 한국어로 응답을 받기 위해 설정
    }
    try:
        response = requests.get(url, params=params)
        response.raise_for_status()  # HTTP 에러가 발생하면 예외 처리
        data = response.json()
        return data.get("title", "제목 없음")
    except requests.exceptions.RequestException as e:
        print(f"API 요청 중 에러 발생: {e}")
        return None
