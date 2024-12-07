import axios from 'axios';

const tmdbApiToken = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI2MjIzNWYyOWJhYjEzZGRjOTVlNzRmZGFlMDFlZDg1MCIsIm5iZiI6MTczMjk2NDExMC4yNzcsInN1YiI6IjY3NGFlZjBlYTEyMzE5ZTVjZTBjZmM3YyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.A_KVw6zVQ_no9vKL_mi_WtESpSJajqjwcgAtwWSj4Ns"; // TMDB API 토큰

// Axios 인스턴스 생성
export const tmdbApiClient = axios.create({
  baseURL: 'https://api.themoviedb.org/3',
  headers: {
    Authorization: `Bearer ${tmdbApiToken}`,
  },
});

// 영화 검색 함수
export const searchMovies = async (query) => {
  try {
    const response = await tmdbApiClient.get('/search/movie', {
      params: {
        query: query,
        language: "ko-KR", // 응답을 한국어로 설정
      },
    });
    return response.data.results;
  } catch (error) {
    console.error('TMDB API 요청 오류:', error);
    throw error;
  }
};
