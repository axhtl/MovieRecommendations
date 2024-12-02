import React from 'react';
import '../styles/MovieItem.css';

const MovieItem = ({ movie, onClick }) => {
  const handleImageError = (e) => {
    e.target.src = '/icons/default-image-url.jpg'; // 이미지 로드 실패 시 기본 이미지 사용
  };

  const imageUrl = movie.poster_path
    ? `https://image.tmdb.org/t/p/w500${movie.poster_path}`
    : movie.imageUrl || '/icons/default-image-url.jpg'; // 최신 영화 데이터와 검색 결과를 모두 처리

  return (
    <div className="movie-item" onClick={() => onClick && onClick(movie.id || movie.movieCd)}>
      <img
        src={imageUrl}
        alt={movie.title || movie.movieNm}
        onError={handleImageError} // 이미지 로드 실패 시 기본 이미지 대체
      />
      <p className="movie-title">{movie.title || movie.movieNm}</p> {/* 영화 제목 표시 */}
    </div>
  );
};

export default MovieItem;
