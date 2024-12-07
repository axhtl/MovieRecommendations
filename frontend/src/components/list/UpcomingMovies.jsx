import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { tmdbApiClient } from '../../api/tmdb';
import '../styles/UpcomingMovies.css';

const UpcomingMovies = () => {
  const [upcomingMovies, setUpcomingMovies] = useState([]);
  const [error, setError] = useState(null);
  const navigate = useNavigate();


  useEffect(() => {
    const fetchUpcomingMovies = async () => {
      try {
        const response = await tmdbApiClient.get('/movie/upcoming', {
          params: {
            language: 'ko-KR',
            region: 'KR',
          },
        });
        setUpcomingMovies(response.data.results.slice(0, 2)); // 상위 2개 영화만 저장
      } catch (err) {
        console.error('개봉 예정 영화 데이터를 가져오는 중 오류 발생:', err);
        setError('영화를 불러오는 중 오류가 발생했습니다.');
      }
    };

    fetchUpcomingMovies();
  }, []);

  if (error) return <p>{error}</p>;
  if (!upcomingMovies.length) return <p>로딩 중...</p>;

  return (
    <div className="upcoming-movies">
      <h2>개봉 예정 영화</h2>
      <div className="movie-list">
        {upcomingMovies.map((movie) => (
          <div className="movie-card" key={movie.id}>
            <img
              src={`https://image.tmdb.org/t/p/w300${movie.poster_path}`}
              alt={movie.title}
              className="movie-poster"
            />
            <p className="movie-title">{movie.title}</p>
            <p className="movie-release-date">개봉일: {movie.release_date}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default UpcomingMovies;
