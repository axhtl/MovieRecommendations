import React, { useState, useEffect } from 'react';
import Navbar from '../ui/Navbar';
import '../styles/RecommendPage.css';

const RecommendPage = () => {
  const [recommendations, setRecommendations] = useState([]);

  useEffect(() => {
    // 추천 영화 데이터를 서버에서 가져오는 부분 (임시로 빈 배열로 초기화)
    // 실제로는 서버에서 데이터를 가져와서 setRecommendations에 설정합니다.
    setRecommendations([
      { id: 1, name: '영화 이름 1', imageUrl: '/images/movie-placeholder.png', rating: 4 },
      { id: 2, name: '영화 이름 2', imageUrl: '/images/movie-placeholder.png', rating: 5 },
      { id: 3, name: '영화 이름 3', imageUrl: '/images/movie-placeholder.png', rating: 3 },
      { id: 4, name: '영화 이름 4', imageUrl: '/images/movie-placeholder.png', rating: 4 },
    ]);
  }, []);

  return (
    <div className="recommend-page">
      <Navbar />
      <div className="recommend-content">
        <h2>&lt;닉네임&gt; 님을 위한 추천 영화는?</h2>
        <div className="movie-list">
          {recommendations.map((movie) => (
            <div key={movie.id} className="movie-item">
              <img src={movie.imageUrl} alt={movie.name} />
              <p>{movie.name}</p>
              <div className="rating">
                {'★'.repeat(movie.rating)}
                {'☆'.repeat(5 - movie.rating)}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default RecommendPage;
