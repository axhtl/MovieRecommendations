# Hybrid_Recom_Module.py
from transformers import AutoTokenizer, AutoModel
import torch
from sklearn.metrics.pairwise import cosine_similarity
import pandas as pd
import requests

HUGGINGFACE_TOKEN = "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"
TMDB_API_KEY = "62235f29bab13ddc95e74fdae01ed850"

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

def hybrid_filtering(user_input, df_movies, user_behavior_data):
    df_movies["genre_score"] = df_movies["genres"].apply(
        lambda genres: len(set(genres) & set(user_input["preferredGenres"]))
    )
    df_movies["actor_score"] = df_movies["actors"].apply(
        lambda actors: len(set(actors) & set(user_input["preferredActors"]))
    )
    df_movies["content_score"] = df_movies["genre_score"] * 0.6 + df_movies["actor_score"] * 0.4
    user_behavior_score = []
    for movie in df_movies["movieNm"]:
        score = 0
        for behavior in user_behavior_data:
            if movie in behavior["liked_movies"]:
                score += 1
        user_behavior_score.append(score)
    df_movies["collaborative_score"] = user_behavior_score
    df_movies["hybrid_score"] = df_movies["content_score"] * 0.5 + df_movies["collaborative_score"] * 0.5
    return df_movies.sort_values("hybrid_score", ascending=False)

def generate_embeddings(text_list, tokenizer, model):
    inputs = tokenizer(text_list, padding=True, truncation=True, return_tensors="pt")
    with torch.no_grad():
        outputs = model(**inputs)
        embeddings = outputs.last_hidden_state.mean(dim=1)
    return embeddings

def recommend_movies(user_input, target_movie, df_movies, similarity_matrix, user_behavior_data, top_n=5):
    if target_movie not in df_movies["movieNm"].values:
        search_results = search_movie_by_name(target_movie)
        if search_results:
            genres = fetch_genres()
            processed_movies = process_tmdb_data(search_results, genres)
            df_movies = pd.concat([df_movies, pd.DataFrame(processed_movies)], ignore_index=True)
            df_movies = update_movie_details(df_movies)
        else:
            raise ValueError(f"'{target_movie}'를 찾을 수 없습니다. 다른 타겟 영화를 선택하세요.")
        
        df_movies["meta_text"] = df_movies.apply(
            lambda row: f"{row['movieNm']} {row['genres']} {row['directors']} {row['actors']}", axis=1
        )
        text_list = df_movies["meta_text"].tolist()
        embeddings = generate_embeddings(text_list, tokenizer, model)
        similarity_matrix = cosine_similarity(embeddings)
    
    df_movies = hybrid_filtering(user_input, df_movies, user_behavior_data)
    target_index = df_movies[df_movies["movieNm"] == target_movie].index[0]
    similarity_scores = list(enumerate(similarity_matrix[target_index]))
    similarity_scores = sorted(similarity_scores, key=lambda x: x[1], reverse=True)
    similar_movies = [df_movies.iloc[i[0]]["movieNm"] for i in similarity_scores[1:top_n + 1]]
    
    return df_movies[["movieNm", "hybrid_score"]].head(top_n), similar_movies

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

def do_recommendation(user_data, target_movie, df_movies, similarity_matrix, user_behavior_data):
    hybrid_recommendations, similar_movies = recommend_movies(
    user_input=user_data,
    target_movie=target_movie,
    df_movies=df_movies,
    similarity_matrix=similarity_matrix,
    user_behavior_data=user_behavior_data
    )

    return hybrid_recommendations, similar_movies