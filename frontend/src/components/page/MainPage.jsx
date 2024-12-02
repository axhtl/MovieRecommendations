import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom'; // useNavigate 추가
import Navbar from '../ui/Navbar';
import '../styles/MainPage.css';

const MainPage = () => {
  const [movies, setMovies] = useState([]);
  const navigate = useNavigate(); // navigate 사용 준비

  useEffect(() => {
    setMovies([]); // 초기값 설정
  }, []);

  const handleSearchClick = () => {
    navigate('/movie/view'); // /movie/search 경로로 이동
  };

  return (
    <div className="main-page">
      <Navbar />
      <div className="main-content">
        {movies.length === 0 ? (
          <div className="no-movies">
            <h2>아직 사용자를 위한 영화가 없어요 ㅠ</h2>
            <p>바로 등록하러 가볼까요?</p>
            <button className="add-movie-button" onClick={handleSearchClick}>
              영화 등록하기
            </button>
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
