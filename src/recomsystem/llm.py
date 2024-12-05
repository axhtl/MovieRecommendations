from flask import Flask, request, jsonify

import Moviepick_LLM_Module as MLM
import pandas as pd

from transformers import AutoModelForCausalLM, AutoTokenizer
import torch
import pandas as pd
import Hybrid_Recom_Module as HRM
import json
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # 모든 도메인에서 접근 허용


# Spring Boot의 /api/ai/predict/llm 와 동일한 경로로 수정
@app.route('/api/ai/predict/llm', methods=['POST'])
def ai_recommendation():
    # 클라이언트로부터 요청 데이터를 받아옴
    data = request.get_json()

    # 요청에서 user_prompt (사용자 프롬포트 입력 데이터)
    user_prompt = data.get('user_prompt')

    if not user_prompt:
        return jsonify({"error": "필수 데이터가 누락되었습니다. user_prompt 가 필요합니다."}), 400

    # 추천 시스템 실행
    try:
        # 추천 함수 호출: movie_name을 타겟 영화로 사용하여 추천 시스템 실행
        llm_response = MLM.do_response(
            user_prompt=user_prompt
        )

        return jsonify(llm_response)

    except Exception as e:
        # 예외 처리: 추천 시스템 실행 중 오류 발생 시
        print(f"Error: {e}")
        return jsonify({"error": "추천 시스템 실행 중 오류가 발생했습니다."}), 500

# Flask 서버는 여기서 직접 실행하지 않음. Spring Boot에서 실행됨.


  """
"트랜스포머같은 영화를 추천해 주세요."
  """


   """
   연동 구조 요약:
    - Flask는 Python 코드에서 API를 제공하고, Spring Boot는 Flask API를 호출하여 데이터를 받아옵니다.
    - **AIModelService.java**에서 Flask 서버를 실행하지 않고, 외부 프로세스로 Python 스크립트를 호출합니다.
    - Spring Boot는 Flask로부터 JSON 응답을 받아서 처리하고, **AIController**에서 이를 클라이언트에 전달합니다.
    - Flask 서버는 Spring Boot 내에서 외부 프로세스로 실행되며, 포트 충돌을 피하기 위해 Spring Boot가 모든 HTTP 요청을 처리합니다.
   """
