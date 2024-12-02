import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import StarRating from './StarRating'; // 별점 컴포넌트 import
import Navbar from '../ui/Navbar'; // Navbar 컴포넌트 import
import { tmdbApiClient } from '../../api/tmdb'; // Axios 인스턴스를 가져옴
import axios from 'axios'; // 서버와 통신을 위한 axios import
import '../styles/MovieDetails.css';

const MovieDetails = () => {
  const { movieCd } = useParams(); // URL의 파라미터에서 movieCd 추출
  const [movieDetails, setMovieDetails] = useState(null);
  const [rating, setRating] = useState(0); // 별점 저장

  useEffect(() => {
    // 영화 세부 정보를 불러오는 함수
    const fetchMovieDetails = async () => {
      if (!movieCd) {
        console.error('영화 코드가 없습니다.');
        return;
      }

      try {
        console.log('Fetching movie details for:', movieCd); // movieCd 값 확인
        const response = await tmdbApiClient.get(`/movie/${movieCd}`, {
          params: {
            language: 'ko-KR', // 응답 언어를 한국어로 설정
          },
        });
        console.log('API Response:', response.data); // API 응답 확인
        setMovieDetails(response.data); // 영화 세부 정보를 state에 저장
      } catch (error) {
        console.error('영화 세부 정보 불러오기 오류:', error);
      }
    };

    fetchMovieDetails();
  }, [movieCd]);

  const handleRegister = async () => {
    try {
      // 영화 등록 요청을 서버에 전송
      await axios.post('/api/user/movies', {
        movieId: movieDetails.id,
        title: movieDetails.title,
        posterUrl: movieDetails.poster_path ? `https://image.tmdb.org/t/p/w500${movieDetails.poster_path}` : null,
        rating: rating,
      });

      alert('영화가 등록되었습니다.');
    } catch (error) {
      console.error('영화 등록 중 오류 발생:', error);
      alert('영화 등록에 실패했습니다. 다시 시도해 주세요.');
    }
  };

  if (!movieDetails) {
    return (
      <div className="movie-details">
        <Navbar />
        <p>Loading...</p> {/* 세부 정보를 불러오는 동안 로딩 메시지 표시 */}
      </div>
    );
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
            <StarRating onRatingChange={setRating} /> {/* 별점 컴포넌트 사용, 별점 설정 시 상태 업데이트 */}
          </div>
          <div className="register-button-container">
            <button className="register-button" onClick={handleRegister}>등록</button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MovieDetails;
