import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Navbar from '../ui/Navbar';
import RegiMovieList from '../list/RegiMovieList';
import RecommendationCard from '../list/RecommendationCard';
import '../styles/MainPage.css';

const MainPage = () => {
  const { userId } = useParams();
  const [movies, setMovies] = useState([]);
  const [nickname, setNickname] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchMovies = async () => {
      const accessToken = localStorage.getItem('token');

      if (!userId || !accessToken) {
        alert('로그인이 필요합니다. 로그인 페이지로 이동합니다.');
        navigate('/');
        return;
      }

      try {
        const userResponse = await fetch(`/member/user/${userId}`, {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        });

        if (!userResponse.ok) {
          throw new Error('사용자 데이터 요청 실패');
        }

        const userData = await userResponse.json();
        setMovies(userData.reviewInfos || []);
        setNickname(userData.member?.nickname || '');
      } catch (error) {
        console.error('데이터를 불러오는 데 실패했습니다:', error.message);
      }
    };

    fetchMovies();
  }, [userId, navigate]);

  const handleSearchClick = () => {
    navigate('/movie/view');
  };

  const recommendations = [
    {
      title: '등록한 리뷰 기반 추천',
      description: '사용자가 입력한 리뷰 점수를 바탕으로 가장 적합한 영화를 추천합니다.',
      imageUrl: '/icons/image1.png',
    },
    {
      title: '설문조사 기반 추천',
      description: '설문조사를 통해 사용자의 취향을 분석하여 맞춤 영화를 추천합니다.',
      imageUrl: '/icons/image2.png',
    },
    {
      title: 'LLM 기반 추천',
      description: 'AI 챗봇과 대화를 통해 사용자에게 더 적합한 맞춤 영화를 추천합니다.',
      imageUrl: '/icons/image3.jpg',
    },
  ];

  return (
    <div className="main-page">
      <Navbar />
      <div className="main-content">
        {movies.length === 0 ? (
          <div className="no-movies">
            <h1>MOVIE PICK</h1>
            <h4>{nickname} 님을 위한 영화 추천 플랫폼</h4>
            <p style={{ whiteSpace: 'pre-wrap' }}>
              무비픽은 사용자의 영화 기록 데이터를 기반으로 하는 영화 추천 AI 웹 서비스예요.
              총 3가지 방법을 이용해 사용자 맞춤형 영화를 추천해 드려요. 
            </p>
            <div className="recommendation-list">
              {recommendations.map((item, index) => (
                <RecommendationCard
                  key={index}
                  title={item.title}
                  description={item.description}
                  imageUrl={item.imageUrl}
                />
              ))}
            </div>
            <div className="add-movie-section">
              <p>{nickname} 님, 바로 등록하러 가볼까요?</p>
            </div>
            <div className="add-movie-section">
              <button className="add-movie-button" onClick={handleSearchClick}>
                영화 등록하기
              </button>
            </div>
          </div>
        ) : (
          <div>
            <h2>{nickname} 님이 등록한 영화</h2>
            <div className="movie-section">
              <RegiMovieList movies={movies} />
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default MainPage;
