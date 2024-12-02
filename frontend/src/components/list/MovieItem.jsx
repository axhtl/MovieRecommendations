import React from 'react';
import '../styles/MovieItem.css';

const MovieItem = ({ movie, onClick }) => {
  const handleImageError = (e) => {
    e.target.src = '/icons/default-image-url.jpg'; // 기본 이미지로 설정
  };

  // TMDB의 `poster_path` 사용하여 이미지 URL 설정
  const imageUrl = movie.poster_path
    ? `https://image.tmdb.org/t/p/w500${movie.poster_path}`
    : '/icons/default-image-url.jpg'; // 기본 이미지 사용

  return (
    <div className="movie-item" onClick={() => onClick(movie.id)}>
      <img
        src={imageUrl}
        alt={movie.title} // 이미지 설명에 기본 제목 또는 오리지널 제목 사용
        onError={handleImageError} // 이미지 로드 실패 시 기본 이미지로 대체
      />
      <p className="movie-title">{movie.title}</p> {/* 영화 제목을 이미지 하단에 표시 */}
    </div>
  );
};

export default MovieItem;
