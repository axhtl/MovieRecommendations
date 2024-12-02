import React, { useState, useEffect } from 'react';
import Navbar from '../ui/Navbar';
import '../styles/MyPage.css';

const MyPage = () => {
  // eslint-disable-next-line no-unused-vars
  const [userInfo, setUserInfo] = useState(null);
  const [myMovies, setMyMovies] = useState([]);

  useEffect(() => {
    // 유저 정보 및 내가 등록한 영화 데이터를 서버에서 가져오는 부분 (임시로 빈 배열로 초기화)
    setMyMovies([
      { id: 1, name: '영화 이름 1', imageUrl: '/images/movie-placeholder.png' },
      { id: 2, name: '영화 이름 2', imageUrl: '/images/movie-placeholder.png' },
      { id: 3, name: '영화 이름 3', imageUrl: '/images/movie-placeholder.png' },
      { id: 4, name: '영화 이름 4', imageUrl: '/images/movie-placeholder.png' },
    ]);
  }, []);

  return (
    <div className="my-page">
      <Navbar />
      <div className="my-page-content">
        <div className="user-info">
          <h2>내 정보가 담긴 내용</h2>
          <p>이름: {userInfo.name}</p>
          <p>이메일: {userInfo.email}</p>
          <button className="edit-button">수정</button>
        </div>
        <div className="my-movies">
          <h2>내가 등록한 영화</h2>
          <div className="movie-list">
            {myMovies.map((movie) => (
              <div key={movie.id} className="movie-item">
                <img src={movie.imageUrl} alt={movie.name} />
                <p>{movie.name}</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default MyPage;
