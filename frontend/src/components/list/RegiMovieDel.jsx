import React, { useEffect, useState } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import StarRating from './StarRating';
import Navbar from '../ui/Navbar';
import axios from 'axios';
import '../styles/MovieDetails.css';

const RegiMovieDel = () => {
  const { movieId } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  const { reviewId: initialReviewId } = location.state || {}; // 초기 리뷰 ID 가져오기

  const [movieDetails, setMovieDetails] = useState(null);
  const [credits, setCredits] = useState(null);
  const [rating, setRating] = useState(0);
  const [reviewId, setReviewId] = useState(initialReviewId || null); // 리뷰 ID 상태 관리
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchMovieDetails = async () => {
      if (!movieId) {
        console.error('영화 ID가 없습니다.');
        return;
      }

      try {
        const token = localStorage.getItem('token');
        if (!token) {
          alert('로그인이 필요합니다.');
          navigate('/'); // 로그인 페이지로 이동
          return;
        }

        const language = 'ko';

        const [detailsResponse, creditsResponse] = await Promise.all([
          axios.get(`/api/movies/detail/${movieId}`, {
            params: { language },
            headers: { Authorization: `Bearer ${token}` },
          }),
          axios.get(`/api/movies/${movieId}/credits`, {
            params: { language },
            headers: { Authorization: `Bearer ${token}` },
          }),
        ]);

        setMovieDetails(detailsResponse.data);
        setCredits(creditsResponse.data);

        if (initialReviewId) {
          const reviewResponse = await axios.get(`/review/${initialReviewId}`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          setRating(reviewResponse.data.ranked);
        }
      } catch (error) {
        console.error('영화 세부 정보 불러오기 오류:', error.response?.data || error.message);
        alert('영화 정보를 불러오는 중 문제가 발생했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchMovieDetails();
  }, [movieId, initialReviewId, navigate]);

  const handleRegister = async () => {
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

      const response = await axios.post(
        `/review/${memberId}`,
        {
          movieId,
          ranked: rating, // 별점
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      setReviewId(response.data.id); // 등록된 리뷰 ID 저장
      alert('리뷰가 성공적으로 등록되었습니다.');
    } catch (error) {
      console.error('리뷰 등록 중 오류 발생:', error.response?.data || error.message);
      alert('리뷰 등록에 실패했습니다.');
    }
  };

  const handleDelete = async () => {
    const confirmDelete = window.confirm('정말로 삭제하시겠습니까?');
    if (!confirmDelete) return;

    try {
      const token = localStorage.getItem('token');
      if (!token) {
        alert('로그인이 필요합니다.');
        return;
      }

      await axios.delete(`/review/${reviewId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      alert('리뷰가 삭제되었습니다.');
      setReviewId(null); // 리뷰 ID 초기화
    } catch (error) {
      console.error('리뷰 삭제 중 오류:', error.response?.data || error.message);
      alert('리뷰 삭제에 실패했습니다. 다시 시도해주세요.');
    }
  };

  if (loading) {
    return (
      <div className="movie-details">
        <Navbar />
        <div className="loading-container">
          <p>Loading...</p>
        </div>
      </div>
    );
  }

  if (!movieDetails || !credits) {
    return (
      <div className="movie-details">
        <Navbar />
        <div className="error-container">
          <p>영화 정보를 불러오지 못했습니다. 다시 시도해주세요.</p>
        </div>
      </div>
    );
  }

  const imageUrl = movieDetails.poster_path
    ? `https://image.tmdb.org/t/p/w500${movieDetails.poster_path}`
    : '/icons/default-image-url.jpg';

  const cast = credits.cast?.slice(0, 3) || [];
  const director = credits.crew?.find((person) => person.job === 'Director') || null;

  return (
    <div className="movie-details">
      <Navbar />
      <div className="movie-details-container">
        <div className="movie-image">
          <img src={imageUrl} alt={movieDetails.title || '영화 이미지'} />
        </div>
        <div className="movie-info">
          <h2>{movieDetails.title || '제목 없음'}</h2>
          <p><strong>개봉일:</strong> {movieDetails.release_date || '정보 없음'}</p>
          <p><strong>장르:</strong> {movieDetails.genres?.map((g) => g.name).join(', ') || '정보 없음'}</p>
          <p><strong>줄거리:</strong> {movieDetails.overview || '정보 없음'}</p>
          <p><strong>감독:</strong> {director?.name || '정보 없음'}</p>
          <p><strong>출연 배우:</strong> {cast.map((c) => c.name).join(', ') || '정보 없음'}</p>
          <div className="star-rating">
            <p><strong>별점:</strong></p>
            <StarRating rating={rating} onRatingChange={setRating} readOnly={!!reviewId} />
          </div>
          <div className="button-container">
            {reviewId ? (
              <button className="delete-button" onClick={handleDelete}>리뷰 삭제</button>
            ) : (
              <button className="register-button" onClick={handleRegister}>리뷰 등록</button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegiMovieDel;
