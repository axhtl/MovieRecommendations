import React, { useEffect, useState } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import StarRating from './StarRating';
import Navbar from '../ui/Navbar';
import axios from 'axios';
import '../styles/MovieDetails.css';

const MovieDetails = () => {
  const { movieId } = useParams(); // URL에서 영화 ID 가져오기
  const location = useLocation();
  const navigate = useNavigate();

  const [movieDetails, setMovieDetails] = useState(null); // 영화 세부 정보 상태
  const [credits, setCredits] = useState(null); // 배우 및 감독 정보 상태
  const [rating, setRating] = useState(0); // 사용자가 등록한 별점 상태

  useEffect(() => {
    // 영화 세부 정보 및 크레딧 정보 가져오기
    const fetchMovieDetails = async () => {
      if (!movieId) {
        console.error('영화 ID가 없습니다.');
        return;
      }

      try {
        const language = new URLSearchParams(location.search).get('language') || 'ko';

        const [detailsResponse, creditsResponse] = await Promise.all([
          axios.get(`/api/movies/detail/${movieId}`, {
            params: { language },
            headers: { Authorization: `Bearer YOUR_ACCESS_TOKEN_HERE` },
          }),
          axios.get(`/api/movies/${movieId}/credits`, {
            params: { language },
            headers: { Authorization: `Bearer YOUR_ACCESS_TOKEN_HERE` },
          }),
        ]);

        setMovieDetails(detailsResponse.data); // 영화 세부 정보 저장
        setCredits(creditsResponse.data); // 배우 및 감독 정보 저장
      } catch (error) {
        console.error('영화 세부 정보 및 크레딧 불러오기 오류:', error.response?.data || error.message);
        alert('영화 정보를 불러오는 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
      }
    };

    fetchMovieDetails();
  }, [movieId, location.search]);

  const handleRegister = async () => {
    const confirmRegistration = window.confirm('등록하시겠습니까?');
    if (!confirmRegistration) {
      return;
    }
  
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        alert('로그인이 필요합니다.');
        return;
      }
  
      const memberId = localStorage.getItem('memberId');
      if (!memberId) {
        alert('사용자 정보를 불러올 수 없습니다. 다시 로그인하세요.');
        return;
      }
  
      // 서버에 영화와 별점 데이터 등록
      await axios.post(
        `/review/${memberId}`,
        {
          movieId: movieDetails.id || movieDetails.movieId, // 영화 ID
          ranked: rating, // 사용자가 선택한 별점
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        }
      );
  
      alert('영화가 성공적으로 등록되었습니다.');
      navigate(-1);
    } catch (error) {
      console.error('영화 등록 중 오류 발생:', error.response?.data || error.message);
      alert('영화 등록에 실패했습니다. 다시 시도해주세요.');
    }
  };
  

  if (!movieDetails || !credits) {
    return (
      <div className="movie-details">
        <Navbar />
        <p>Loading...</p>
      </div>
    );
  }

  const imageUrl = movieDetails.poster_path
    ? `https://image.tmdb.org/t/p/w500${movieDetails.poster_path}`
    : '/icons/default-image-url.jpg';

  const handleImageError = (e) => {
    e.target.src = '/icons/default-image-url.jpg'; // 이미지 로딩 실패 시 대체 이미지
  };

  // 배우 3명 및 감독 정보 추출
  const cast = credits.cast?.slice(0, 3) || []; // 상위 3명의 배우
  const director = credits.crew?.find(person => person.job === 'Director') || null; // 감독 정보

  return (
    <div className="movie-details">
      <Navbar />
      <div className="movie-details-container">
        <div className="movie-image">
          <img
            src={imageUrl}
            alt={movieDetails.title || '영화 이미지'}
            onError={handleImageError}
          />
        </div>
        <div className="movie-info">
          <h2>{movieDetails.title || '제목 없음'}</h2>
          <p><strong>개봉일:</strong> {movieDetails.release_date || '정보 없음'}</p>
          <p><strong>장르:</strong> {movieDetails.genres?.map(genre => genre.name).join(', ') || '정보 없음'}</p>
          <p><strong>줄거리:</strong> {movieDetails.overview || '정보 없음'}</p>
          <p><strong>감독:</strong> {director?.name || '정보 없음'}</p>
          <p><strong>출연 배우:</strong> {cast.map(actor => actor.name).join(', ') || '정보 없음'}</p>
          <div className="star-rating">
            <p><strong>별점:</strong></p>
            <StarRating rating={rating} onRatingChange={setRating} />
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
