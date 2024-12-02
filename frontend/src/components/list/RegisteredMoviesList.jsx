// src/components/list/RegisteredMoviesList.jsx
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import MovieList from './MovieList';

const RegisteredMoviesList = () => {
  const [registeredMovies, setRegisteredMovies] = useState([]);

  useEffect(() => {
    // 등록된 영화를 서버에서 불러오는 함수
    const fetchRegisteredMovies = async () => {
      try {
        const response = await axios.get('/api/user/movies'); // 서버에서 등록된 영화 리스트를 가져옴
        setRegisteredMovies(response.data);
      } catch (error) {
        console.error('영화 목록 불러오기 오류:', error);
      }
    };

    fetchRegisteredMovies();
  }, []);

  return (
    <div className="registered-movies-list">
      <h2>등록된 영화 목록</h2>
      {registeredMovies.length > 0 ? (
        <MovieList movies={registeredMovies} /> // MovieList 컴포넌트를 사용해 등록된 영화 리스트를 렌더링
      ) : (
        <p>등록된 영화가 없습니다.</p>
      )}
    </div>
  );
};

export default RegisteredMoviesList;
