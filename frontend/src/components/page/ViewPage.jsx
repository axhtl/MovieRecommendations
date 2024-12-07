import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom'; // navigate 사용을 위한 import
import Navbar from '../ui/Navbar';
import { tmdbApiClient } from '../../api/tmdb'; // Axios 인스턴스 가져오기
import MovieList from '../list/MovieList'; // MovieList 컴포넌트 가져오기
import '../styles/ViewPage.css'; // 스타일 파일 추가

const ViewPage = () => {
  const [movies, setMovies] = useState([]);
  const navigate = useNavigate(); // navigate 사용 준비

  useEffect(() => {
    // 최신 영화 데이터를 불러오는 함수
    const fetchMovies = async () => {
      try {
        const response = await tmdbApiClient.get('/movie/popular', {
          params: {
            language: 'ko-KR', // 응답 언어 설정 (한국어)
          },
        });
        setMovies(response.data.results); // 영화 목록 저장
      } catch (error) {
        console.error('영화 데이터 불러오기 오류:', error);
      }
    };

    fetchMovies();
  }, []);

  // 영화 클릭 시 영화 상세 페이지로 이동하는 함수
  const handleMovieClick = (movieId) => {
    console.log('Navigating to movie details with ID:', movieId); // 로그 추가
    navigate(`/api/movies/detail/${movieId}?language=ko`);
  };

  return (
    <div className="view-page">
      <Navbar />
      
      <div className="view-content">
        <h2>최신 영화 목록</h2>
        <MovieList movies={movies} onMovieClick={handleMovieClick} /> {/* MovieList 컴포넌트 사용 */}
      </div>
    </div>
  );
};

export default ViewPage;
