import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/RegiMovieList.css';
import axios from 'axios';

const RegiMovieList = ({ movies }) => {
  const [movieDetails, setMovieDetails] = useState({});
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const movieDetailsPromises = movies.map((review) =>
          axios
            .get(`/api/movies/detail/${review.movieInfo.movieId}?language=ko`)
            .then((response) => ({
              movieId: review.movieInfo.movieId,
              posterPath: response.data.poster_path,
            }))
            .catch(() => ({
              movieId: review.movieInfo.movieId,
              posterPath: null,
            }))
        );

        const movieDetailsResults = await Promise.all(movieDetailsPromises);

        setMovieDetails(
          movieDetailsResults.reduce((acc, detail) => {
            acc[detail.movieId] = detail.posterPath;
            return acc;
          }, {})
        );
      } catch (error) {
        console.error('데이터를 가져오는 중 문제가 발생했습니다:', error);
      }
    };

    fetchData();
  }, [movies]);

  const handleMovieClick = (review) => {
    navigate(`/api/movies/detail/${review.movieInfo.movieId}`, {
      state: { reviewId: review.reviewId },
    });
  };

  return (
    <div className="regi-movie-list">
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
          </div>
        ))}
      </div>
    </div>
  );
};

export default RegiMovieList;
