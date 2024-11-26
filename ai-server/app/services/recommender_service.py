from app.models.recommender import MovieRecommender
from app.utils.data_processing import DataPreprocessing

class RecommenderService:
    def __init__(self):
        # MovieRecommender 클래스의 인스턴스를 생성하고, 모델을 로드
        self.model = MovieRecommender()
        self.model.load_model('path_to_trained_model.pth')  # 모델 경로를 지정
        self.data_preprocessor = DataPreprocessing()  # 데이터 전처리 클래스

    def get_recommendations(self, gender, age, preferred_genres, preferred_actors, movie_details):
        # 사용자 데이터를 전처리
        preprocessed_data = self.data_preprocessor.preprocess_user_data(
            gender, age, preferred_genres, preferred_actors
        )
        
        # 전처리된 데이터를 모델에 입력하여 추천을 받음
        recommended_movies = self.model.recommend(preprocessed_data)
        
        return recommended_movies
