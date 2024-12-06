import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import StarRating from './StarRating';
import '../styles/RegiMovieList.css';
import axios from 'axios';

const RegiMovieList = ({ movies }) => {
  const [movieDetails, setMovieDetails] = useState({});
  const [ratings, setRatings] = useState({});
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const token = localStorage.getItem('token');
        const memberId = localStorage.getItem('memberId');
        if (!token || !memberId) {
          alert('로그인이 필요합니다.');
          return;
        }

        // 회원 정보 가져오기
        const userResponse = await axios.get(`/member/user/${memberId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        const userReviews = userResponse.data.reviews || [];
        const reviewInfos = userResponse.data.reviewInfos || [];

        // 별점 데이터 매핑
        const reviewsMap = userReviews.reduce((acc, review) => {
          acc[review.reviewId] = Number(review.ranked);
          return acc;
        }, {});

        // 영화 포스터 데이터 가져오기
        const posterPromises = reviewInfos.map((info) =>
          axios
            .get(`/api/movies/detail/${info.movieInfo.movieId}`, {
              params: { language: 'ko' },
              headers: { Authorization: `Bearer ${token}` },
            })
            .then((response) => ({
              movieId: info.movieInfo.movieId,
              posterPath: response.data.poster_path,
            }))
            .catch(() => ({
              movieId: info.movieInfo.movieId,
              posterPath: null,
            }))
        );

        const posterResults = await Promise.all(posterPromises);

        // 포스터 데이터 매핑
        const posterMap = posterResults.reduce((acc, detail) => {
          acc[detail.movieId] = detail.posterPath;
          return acc;
        }, {});

        setRatings(reviewsMap);
        setMovieDetails(posterMap);
      } catch (error) {
        console.error('데이터를 가져오는 중 오류 발생:', error.response?.data || error.message);
        alert('영화 정보를 불러오는 중 문제가 발생했습니다.');
      }
    };

    fetchData();
  }, [movies]);

  const scrollLeft = () => {
    document.querySelector('.movie-list-container').scrollBy({
      left: -200,
      behavior: 'smooth',
    });
  };

  const scrollRight = () => {
    document.querySelector('.movie-list-container').scrollBy({
      left: 200,
      behavior: 'smooth',
    });
  };

  const handleMovieClick = (review) => {
    navigate(`/movies/${review.movieInfo.movieId}`, {
      state: { reviewId: review.reviewId },
    });
  };

  return (
    <div className="regi-movie-list">
      <button className="scroll-button left" onClick={scrollLeft}>
        &#8249;
      </button>

      <div className="movie-list-container">
        {movies.map((review) => (
          <div
            key={review.reviewId}
            className="movie-item"
            onClick={() => handleMovieClick(review)}
          >
            <img
              src={
                movieDetails[review.movieInfo.movieId]
                  ? `https://image.tmdb.org/t/p/w500${movieDetails[review.movieInfo.movieId]}`
                  : '/icons/default-image-url.jpg'
              }
              alt={review.movieInfo.title || '영화 이미지'}
              onError={(e) => {
                e.target.src = '/icons/default-image-url.jpg';
              }}
            />
            <h3>{review.movieInfo.title || '제목 없음'}</h3>
            <div className="movie-rating">
              <StarRating
                rating={ratings[review.reviewId] || 0}
                readOnly
              />
            </div>
          </div>
        ))}
      </div>

      <button className="scroll-button right" onClick={scrollRight}>
        &#8250;
      </button>
    </div>
  );
};

export default RegiMovieList;
