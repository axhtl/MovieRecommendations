import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import StarRating from './StarRating'; // 별점 컴포넌트 import
import Navbar from '../ui/Navbar'; // Navbar 컴포넌트 import
import { tmdbApiClient } from '../../api/tmdb'; // Axios 인스턴스를 가져옴
import '../styles/MovieDetails.css';

const MovieDetails = () => {
  const { movieCd } = useParams(); // URL의 파라미터에서 movieCd 추출
  const [movieDetails, setMovieDetails] = useState(null);

  useEffect(() => {
    // 영화 세부 정보를 불러오는 함수
    const fetchMovieDetails = async () => {
      try {
        const response = await tmdbApiClient.get(`/movie/${movieCd}`, {
          params: {
            language: 'ko-KR', // 응답 언어를 한국어로 설정
          },
        });
        setMovieDetails(response.data); // 영화 세부 정보를 state에 저장
      } catch (error) {
        console.error('영화 세부 정보 불러오기 오류:', error);
      }
    };

    fetchMovieDetails();
  }, [movieCd]);

  if (!movieDetails) {
    return <p>Loading...</p>; // 세부 정보를 불러오는 동안 로딩 메시지 표시
  }

  return (
    <div className="movie-details">
      {/* Navbar 추가 */}
      <Navbar />
      <div className="movie-details-container">
        <div className="movie-image">
          <img
            src={movieDetails.poster_path ? `https://image.tmdb.org/t/p/w500${movieDetails.poster_path}` : '/icons/default-image-url.jpg'}
            alt={movieDetails.title || '영화 이미지'}
          />
        </div>
        <div className="movie-info">
          <h2>{movieDetails.title || movieDetails.original_title || '제목 없음'}</h2>
          <p><strong>개봉일:</strong> {movieDetails.release_date || '정보 없음'}</p>
          <p><strong>장르:</strong> {movieDetails.genres?.map(genre => genre.name).join(', ') || '정보 없음'}</p>
          <p><strong>줄거리:</strong> {movieDetails.overview || '정보 없음'}</p>
          <div className="star-rating">
            <p><strong>별점:</strong></p>
            <StarRating /> {/* 별점 컴포넌트 사용 */}
          </div>
          <div className="register-button-container">
            <button className="register-button">등록</button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MovieDetails;
