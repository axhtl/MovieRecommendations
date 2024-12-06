import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import StarRating from './StarRating';
import Navbar from '../ui/Navbar';
import axios from 'axios';
import '../styles/MovieDetails.css';

const RegiMovieDel = () => {
  const { movieId } = useParams();
  const navigate = useNavigate();

  const [movieDetails, setMovieDetails] = useState(null); // 영화 상세 정보
  const [credits, setCredits] = useState(null); // 영화 출연진 정보
  const [rating, setRating] = useState(0); // 별점
  const [reviewId, setReviewId] = useState(null); // 리뷰 ID 상태 관리
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchMovieAndReviews = async () => {
      if (!movieId) {
        console.error('영화 ID가 없습니다.');
        return;
      }

      try {
        const token = localStorage.getItem('token');
        const memberId = localStorage.getItem('memberId');
        if (!token || !memberId) {
          alert('로그인이 필요합니다.');
          navigate('/'); // 로그인 페이지로 이동
          return;
        }

        const language = 'ko';

        // 영화 상세 정보와 출연진 정보 가져오기
        const [detailsResponse, creditsResponse, reviewsResponse] = await Promise.all([
          axios.get(`/api/movies/detail/${movieId}`, {
            params: { language },
            headers: { Authorization: `Bearer ${token}` },
          }),
          axios.get(`/api/movies/${movieId}/credits`, {
            params: { language },
            headers: { Authorization: `Bearer ${token}` },
          }),
          axios.get(`/member/user/${memberId}`, {
            headers: { Authorization: `Bearer ${token}` },
          }),
        ]);

        setMovieDetails(detailsResponse.data);
        setCredits(creditsResponse.data);

        // `reviews` 배열에서 현재 `movieId`와 일치하는 리뷰 찾기
        const review = reviewsResponse.data.reviews.find(
          (r) => r.movieId === Number(movieId)
        );

        if (review) {
          setRating(Number(review.ranked)); // 리뷰의 별점 설정
          setReviewId(review.reviewId); // 리뷰 ID 설정
        } else {
          setRating(0); // 리뷰가 없으면 기본값 0
        }
      } catch (error) {
        console.error('데이터를 가져오는 중 오류 발생:', error.response?.data || error.message);
        alert('영화 정보를 불러오는 중 문제가 발생했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchMovieAndReviews();
  }, [movieId, navigate]);

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
      navigate(-1); // 이전 페이지로 이동
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

  const cast = credits.cast?.slice(0, 3) || []; // 상위 3명의 출연진
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
            <StarRating
              rating={rating} // 리뷰의 별점 전달
              onRatingChange={setRating}
              readOnly={!!reviewId} // 리뷰가 있을 경우 수정 불가
            />
          </div>
          <div className="button-container">
            {reviewId ? (
              <button className="delete-button" onClick={handleDelete}>리뷰 삭제</button>
            ) : (
              <button className="register-button">리뷰 없음</button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegiMovieDel;
