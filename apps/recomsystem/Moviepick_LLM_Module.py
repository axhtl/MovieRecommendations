from transformers import AutoModelForCausalLM, AutoTokenizer
from sentence_transformers import SentenceTransformer
import faiss
import pickle
import numpy as np
import torch
import requests
import os
from dotenv import load_dotenv

# 환경 변수 로드
load_dotenv()

HUGGINGFACE_TOKEN = os.getenv("HUGGINGFACE_TOKEN")
TMDB_API_KEY = os.getenv("TMDB_API_KEY")



# Load tokenizer and model
model_path = "z8086486/Moviepick-Llama-3-8B"
tokenizer = AutoTokenizer.from_pretrained(model_path)
model = AutoModelForCausalLM.from_pretrained(
    model_path,
    torch_dtype=torch.float16,
    device_map="auto",
    load_in_8bit=True,
)

if tokenizer.pad_token is None:
    tokenizer.add_special_tokens({'pad_token': '[PAD]'})
    model.resize_token_embeddings(len(tokenizer))

# Load Embeddings and FAISS Index
def load_embeddings_and_index(embedding_file, index_file):
    # 저장된 임베딩과 영화 데이터 로드
    with open(embedding_file, "rb") as f:
        movies, embeddings = pickle.load(f)
    
    # 저장된 FAISS 인덱스 로드
    index = faiss.read_index(index_file)
    print(f"Embeddings and index loaded from {embedding_file} and {index_file}.")
    return movies, embeddings, index

# 불러오기
embedding_file = "/home/t24326/svr/AI/recomsystem/data/embeddings.pkl"
index_file = "/home/t24326/svr/AI/recomsystem/data/faiss_index.bin"
movies, embeddings, index = load_embeddings_and_index(embedding_file, index_file)

# Retrieval Using Precomputed Embeddings
def retrieve_context(query, model, index, movies, k=5):
    # 입력 쿼리의 임베딩 생성
    query_embedding = model.encode([query], convert_to_tensor=False)
    # FAISS 인덱스를 통해 유사한 결과 검색
    distances, indices = index.search(np.array(query_embedding), k)
    return [movies[i] for i in indices[0]]

def generate_prompt(instruction: str, context: str = None) -> str:
    if context:
        return f"""### Instruction:
{instruction}

### Context:
{context}

### Response:
"""
    else:
        return f"""### Instruction:
{instruction}

### Response:
"""

    
# Generate Response
def generate_response(instruction, context):
    # 프롬프트 생성 및 모델 입력
    prompt = generate_prompt(instruction, context)
    inputs = tokenizer(prompt, return_tensors="pt").to(model.device)
    outputs = model.generate(
        **inputs,
        max_length=512,
        temperature=0.7,
        top_p=0.9,
        repetition_penalty=1.2,
        do_sample=True
    )
    response = tokenizer.decode(outputs[0], skip_special_tokens=True).strip()

    # 빈 응답 처리
    if not response or "### Response:" in response and not response.split("### Response:")[1].strip():
        fallback_prompt = generate_prompt("이전에 요청한 내용을 다시 한 번 작성해 주세요.", context)
        fallback_inputs = tokenizer(fallback_prompt, return_tensors="pt").to(model.device)
        outputs = model.generate(
            **fallback_inputs,
            max_length=512,
            temperature=0.7,
            top_p=0.9,
            repetition_penalty=1.2,
            do_sample=True
        )
        response = tokenizer.decode(outputs[0], skip_special_tokens=True).strip()

    return response



def transform_query_to_instruction(query, format_type="similar_movie"):
    if format_type == "similar_movie":
        return f"다음 질문에 답변해주세요: {query}와 비슷한 영화 추천"
    elif format_type == "genre_features":
        return f"다음 질문에 답변해주세요: 장르와 특징 기반으로 {query}에 해당하는 영화 추천"
    elif format_type == "specific_conditions":
        return f"다음 질문에 답변해주세요: {query} 조건에 맞는 영화 추천"
    else:
        return f"다음 질문에 답변해주세요: {query}"
    

def fetch_movie_details_from_tmdb(movie_name, api_key, language="ko-KR"):
    search_url = f"https://api.themoviedb.org/3/search/movie"
    search_params = {"api_key": api_key, "query": movie_name, "language": language}
    search_response = requests.get(search_url, params=search_params).json()
    if not search_response["results"]:
        raise ValueError(f"'{movie_name}'에 대한 정보를 찾을 수 없습니다.")
    
    movie_id = search_response["results"][0]["id"]
    
    details_url = f"https://api.themoviedb.org/3/movie/{movie_id}"
    details_params = {"api_key": api_key, "language": language, "append_to_response": "credits"}
    details_response = requests.get(details_url, params=details_params).json()
    
    return details_response

def generate_movie_analysis_text(movie_details):
    title = movie_details["title"]
    genres = ", ".join([genre["name"] for genre in movie_details.get("genres", [])])
    overview = movie_details.get("overview", "설명이 제공되지 않았습니다.")
    cast = [member["name"] for member in movie_details.get("credits", {}).get("cast", [])[:5]]
    cast_text = ", ".join(cast) if cast else "정보 없음"
    
    text = (
        f"{title}는 {genres} 장르의 영화로, 주요 줄거리는 다음과 같습니다: {overview}\n"
        f"이 영화에는 {cast_text}와 같은 배우들이 출연하며, 복수와 스릴러라는 테마가 돋보입니다."
    )
    return text

# # 예제 호출
# movie_details = fetch_movie_details_from_tmdb("올드보이", TMDB_API_KEY)

# analysis_text = generate_movie_analysis_text(movie_details)
# # print(analysis_text)


def retrieve_similar_movies_from_analysis(analysis_text, query_embedding_model, index, movies, k=5):
    # 텍스트 임베딩 생성
    query_embedding = query_embedding_model.encode([analysis_text], convert_to_tensor=False)
    
    # FAISS 인덱스에서 유사한 항목 검색
    distances, indices = index.search(np.array(query_embedding), k)
    
    # 검색된 영화 데이터 반환
    retrieved_results = [
        movies[i] if isinstance(movies[i], dict) else {"title": movies[i], "overview": "정보 없음"}
        for i in indices[0]
    ]
    return retrieved_results

# # 예제 호출
# query_embedding_model = SentenceTransformer("sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2")
# retrieved_movies = retrieve_similar_movies_from_analysis(analysis_text, query_embedding_model, index, movies)



def generate_response_from_retrieved_movies(instruction, analysis_text, retrieved_movies):
    # 데이터가 문자열일 경우 기본값으로 변환
    retrieved_context = "\n".join(
        [
            f"Title: {movie['title']}\nOverview: {movie['overview']}"
            if isinstance(movie, dict) else f"Title: {movie}\nOverview: 정보 없음"
            for movie in retrieved_movies
        ]
    )
    
    # 프롬프트 생성 및 모델 입력
    prompt = generate_prompt(instruction, analysis_text + "\n" + retrieved_context)
    inputs = tokenizer(prompt, return_tensors="pt").to(model.device)
    outputs = model.generate(
        **inputs,
        max_length=512,
        temperature=0.7,
        top_p=0.9,
        repetition_penalty=1.2,
        do_sample=True
    )
    response = tokenizer.decode(outputs[0], skip_special_tokens=True)
    return response


def generate_response_with_recommendation(instruction, analysis_text, retrieved_movies, ott_platforms=None):
    """
    추천 응답을 생성하는 함수.
    :param instruction: 사용자 요청
    :param analysis_text: 분석된 영화 텍스트
    :param retrieved_movies: 검색된 영화 리스트 (딕셔너리 형태 예상)
    :param ott_platforms: OTT 플랫폼 정보 (딕셔너리, 선택적)
    :return: 최종 응답 문자열
    """
    # OTT 플랫폼 기본값 설정
    if ott_platforms is None:
        ott_platforms = {}

    # 추천 결과 텍스트 생성
    recommendations = []
    for movie in retrieved_movies:
        title = movie.get("title", "제목 없음")
        overview = movie.get("overview", "줄거리 정보가 없습니다.")
        # 추천 이유 간단히 생성
        recommendation_reason = (
            f"'{title}'는 {analysis_text.split(' ', 1)[0]}와 비슷한 분위기와 테마를 가지고 있습니다."
        )
        # OTT 플랫폼 정보 가져오기
        ott_info = ott_platforms.get(title, "해당 정보가 없습니다.")

        # 추천 텍스트 형식화
        recommendations.append(
            f"[{title}]\n\n"
            f"줄거리: {overview}\n\n"
            f"추천 이유: {recommendation_reason}\n\n"
            f"OTT 플랫폼: {ott_info}\n"
        )

    # 최종 결과를 하나의 텍스트로 연결
    final_response = "\n".join(recommendations)
    return final_response


# ott_platforms = {
#     "올드보이": "Netflix, Watcha",
#     "기생충": "Amazon Prime, Watcha",
#     "살인의 추억": "Netflix",
# }

# # 영화 분석 텍스트
# analysis_text = generate_movie_analysis_text(movie_details)

# # 영화 검색
# retrieved_movies = retrieve_similar_movies_from_analysis(
#     analysis_text, query_embedding_model, index, movies
# )


def do_response(user_prompt=None):

    target_moive = "트랜스포머, Transformer"

    movie_details = fetch_movie_details_from_tmdb(target_moive, TMDB_API_KEY)
    analysis_text = generate_movie_analysis_text(movie_details)

    query_embedding_model = SentenceTransformer("sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2")
    retrieved_movies = retrieve_similar_movies_from_analysis(analysis_text, query_embedding_model, index, movies)

    ott_platforms = {
    "올드보이": "Netflix, Watcha",
    "기생충": "Amazon Prime, Watcha",
    "살인의 추억": "Netflix",
    }

    response = generate_response_with_recommendation(
        instruction=user_prompt,
        analysis_text=analysis_text,
        retrieved_movies=retrieved_movies,
        ott_platforms=ott_platforms
    )

    return response

def chat(user_prompt=None):
    """
    사용자 프롬프트를 받아 모델 응답을 생성하는 함수.
    :param user_prompt: 사용자 입력
    :return: LLM의 Response 부분만 반환
    """
    if not user_prompt:
        raise ValueError("사용자 프롬프트가 제공되지 않았습니다.")

    # LLM 프롬프트와 컨텍스트 생성
    instruction = "사용자의 요청에 응답해주세요."
    context = user_prompt

    # LLM 응답 생성
    llm_full_response = generate_response(instruction, context)

    # Response 부분만 추출
    response_start = llm_full_response.find("### Response:") + len("### Response:")
    final_response = llm_full_response[response_start:].strip()

    # 빈 응답 처리
    if not final_response:
        # LLM 응답이 비어 있을 경우 기본 프롬프트로 재생성
        fallback_instruction = "사용자의 요청을 다시 작성해주세요."
        fallback_context = user_prompt
        llm_full_response = generate_response(fallback_instruction, fallback_context)
        response_start = llm_full_response.find("### Response:") + len("### Response:")
        final_response = llm_full_response[response_start:].strip()

    return final_response


def explain_response(basemovie=None):
    """
    기준 영화(basemovie)에 대해 왜 추천했는지 설명을 생성하는 함수.
    :param basemovie: 추천된 영화 (영화 제목)
    :return: 추천 이유가 포함된 응답 문자열
    """

    if not basemovie:
        raise ValueError("기준 영화(basemovie)가 제공되지 않았습니다.")

    # 영화 세부 정보를 TMDB에서 가져오기
    try:
        movie_details = fetch_movie_details_from_tmdb(basemovie, TMDB_API_KEY)
    except Exception as e:
        return f"'{basemovie}'에 대한 정보를 가져오는 중 오류가 발생했습니다: {e}"

    # 영화 분석 텍스트 생성
    analysis_text = generate_movie_analysis_text(movie_details)

    # 추천 이유 생성
    recommendation_reason = (
        f"'{basemovie}'는 다음과 같은 이유로 추천됩니다:\n"
        f"- 장르: {', '.join([genre['name'] for genre in movie_details.get('genres', [])])}\n"
        f"- 줄거리: {movie_details.get('overview', '줄거리 정보가 없습니다.')}")

    # LLM 프롬프트 생성
    instruction = f"다음 영화에 대해 왜 추천했는지 자세히 설명해주세요: {basemovie}"
    context = (
        f"[영화 제목: {basemovie}]\n\n"
        f"{recommendation_reason}\n\n"
        f"이 영화는 독특한 테마와 매력적인 이야기를 제공하여 추천드립니다."
    )

    # LLM을 활용한 응답 생성
    llm_full_response = generate_response(instruction, context)

    # Response 부분만 추출
    response_start = llm_full_response.find("### Response:") + len("### Response:")
    final_response = llm_full_response[response_start:].strip()

    # 빈 응답 처리
    if not final_response:
        # LLM 응답이 비어 있을 경우 기본 프롬프트로 재생성
        fallback_instruction = f"'{basemovie}'를 추천하는 이유를 다시 작성해 주세요."
        fallback_context = (
            f"[영화 제목: {basemovie}]\n\n"
            f"{recommendation_reason}\n\n"
            f"이 영화는 독특한 테마와 매력적인 이야기를 제공하여 추천드립니다."
        )
        llm_full_response = generate_response(fallback_instruction, fallback_context)
        response_start = llm_full_response.find("### Response:") + len("### Response:")
        final_response = llm_full_response[response_start:].strip()

    return final_response


