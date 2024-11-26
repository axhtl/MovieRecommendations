from flask import Flask
from .routes import ai_routes  # AI 알고리즘을 처리하는 라우트 파일

def create_app():
    app = Flask(__name__)
    app.register_blueprint(ai_routes)  # AI 관련 라우트를 블루프린트로 등록
    return app