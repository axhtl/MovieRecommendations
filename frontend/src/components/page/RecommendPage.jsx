import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import Navbar from '../ui/Navbar';
import MovieList from '../list/MovieList'; // 재사용되는 MovieList
import Chatbot from '../list/Chatbot'; // 새롭게 만든 Chatbot 컴포넌트
import '../styles/RecommendPage.css';
import '../styles/Chatbot.css';

const RecommendPage = () => {
  const { userId } = useParams();
  const [recommendations, setRecommendations] = useState([]);
  const [nickname, setNickname] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  // 채팅창 상태
  const [chatOpen, setChatOpen] = useState(false);

  useEffect(() => {
    const fetchNicknameAndRecommendations = async () => {
      const accessToken = localStorage.getItem('token'); // 토큰 받아오기

      if (!userId || !accessToken) {
        setError('로그인이 필요합니다.');
        setLoading(false);
        return;
      }

      try {
        // 닉네임 가져오기
        const nicknameResponse = await fetch(`/member/user/${userId}`, {
          headers: {
            Authorization: `Bearer ${accessToken}`, // 토큰 추가
          },
        });

        if (!nicknameResponse.ok) {
          const errorMessage = await nicknameResponse.text();
          throw new Error(`닉네임 요청 실패: ${errorMessage}`);
        }

        const nicknameData = await nicknameResponse.json();
        setNickname(nicknameData.member?.nickname || '');

        // 추천 영화 가져오기
        const recommendationsResponse = await fetch(`/api/ai/predict/${userId}`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${accessToken}`, // 토큰 추가
          },
        });

        if (!recommendationsResponse.ok) {
          const errorMessage = await recommendationsResponse.text();
          throw new Error(`추천 요청 실패: ${errorMessage}`);
        }

        const recommendationsData = await recommendationsResponse.json();

        // movieCd 기반 상세 영화 데이터 가져오기
        const movies = await Promise.all(
          recommendationsData.map(async (movie) => {
            try {
              const detailResponse = await fetch(
                `/api/movies/detail/${movie.movieCd}?language=ko`,
                {
                  headers: {
                    Authorization: `Bearer ${accessToken}`, // 토큰 추가
                  },
                }
              );

              if (!detailResponse.ok) {
                console.error(`영화 상세 정보 요청 실패: ${movie.movieCd}`);
                return null;
              }

              const detailData = await detailResponse.json();
              return {
                id: movie.movieCd,
                title: detailData.name,
                posterPath: detailData.poster_path,
              };
            } catch (error) {
              console.error(`영화 상세 정보 요청 중 오류 발생: ${movie.movieCd}`, error);
              return null;
            }
          })
        );

        setRecommendations(movies.filter((movie) => movie !== null)); // 유효한 데이터만 추가
      } catch (error) {
        console.error('Error:', error.message);
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    fetchNicknameAndRecommendations();
  }, [userId]);

  if (loading) {
    return (
      <div className="recommend-page">
        <Navbar />
        <div className="recommend-content">
          <p>데이터를 불러오는 중입니다...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="recommend-page">
      <Navbar />
      <div className="recommend-content">
        <h2>{nickname} 님을 위한 추천 영화는?</h2>
        {error ? (
          <p className="error-message">{error}</p>
        ) : (
          <MovieList movies={recommendations} onMovieClick={(id) => console.log(`영화 클릭됨: ${id}`)} />
        )}
      </div>

      {/* 챗봇 버튼 */}
      <button
        className="chatbot-button"
        aria-label="챗봇 열기"
        onClick={() => setChatOpen(!chatOpen)}
      >
        💬
      </button>

      {/* Chatbot 컴포넌트 */}
      <Chatbot isOpen={chatOpen} onClose={() => setChatOpen(false)} />
    </div>
  );
};

export default RecommendPage;
