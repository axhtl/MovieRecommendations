import React, { useState, useEffect } from 'react';
import Navbar from '../ui/Navbar';
import '../styles/MainPage.css';

const MainPage = () => {
  const [movies, setMovies] = useState([]);

  useEffect(() => {
    setMovies([]);
  }, []);

  return (
    <div className="main-page">
      <Navbar />
      <div className="main-content">
        {movies.length === 0 ? (
          <div className="no-movies">
            <h2>아직 사용자를 위한 영화가 없어요 ㅠ</h2>
            <p>바로 등록하러 가볼까요?</p>
            <button className="add-movie-button">영화 검색하기</button>
          </div>
        ) : (
          <div className="movie-section">
            <div className="my-movies">
              <h2>내가 등록한 영화</h2>
              <div className="movie-list">
                {movies.map((movie) => (
                  <div key={movie.id} className="movie-item">
                    <img src={movie.imageUrl} alt={movie.name} />
                    <p>{movie.name}</p>
                  </div>
                ))}
              </div>
            </div>
            <div className="recommended-movies">
              <h2>&lt;닉네임&gt; 님을 위한 영화</h2>
              <div className="movie-list">
                {movies.map((movie) => (
                  <div key={movie.id} className="movie-item">
                    <img src={movie.imageUrl} alt={movie.name} />
                    <p>{movie.name}</p>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default MainPage;