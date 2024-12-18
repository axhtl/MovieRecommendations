import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Navbar from '../ui/Navbar';
import Chatbot from '../list/Chatbot';
import '../styles/RecommendPage.css';
import '../styles/Chatbot.css';

const RecommendPage = () => {
  const { userId } = useParams();
  const navigate = useNavigate();
  const [recommendations, setRecommendations] = useState([]);
  const [nickname, setNickname] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedReason, setSelectedReason] = useState(null);
  const [chatOpen, setChatOpen] = useState(false);

  const fetchRecommendationReason = async (movieCd) => {
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`/api/ai/chatbot/llm/reason/${movieCd}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ text: `${movieCd}` }),
      });

      if (!response.ok) {
        console.error(`추천 이유 요청 실패: ${response.status}`);
        return '추천 이유를 가져올 수 없습니다.';
      }

      const data = await response.json();
      return data.llm_response || '추천 이유 없음';
    } catch (error) {
      console.error(`추천 이유 요청 중 오류 발생: ${error}`);
      return '추천 이유를 가져올 수 없습니다.';
    }
  };

  const fetchNicknameAndRecommendations = async () => {
    const accessToken = localStorage.getItem('token');

    if (!userId || !accessToken) {
      setError('로그인이 필요합니다.');
      setLoading(false);
      return;
    }

    try {
      const nicknameResponse = await fetch(`/member/user/${userId}`, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });

      if (!nicknameResponse.ok) {
        throw new Error('닉네임 요청 실패');
      }

      const nicknameData = await nicknameResponse.json();
      setNickname(nicknameData.member?.nickname || '');

      const recommendationsResponse = await fetch(`/api/ai/predict/${userId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${accessToken}`,
        },
      });

      if (!recommendationsResponse.ok) {
        throw new Error('추천 요청 실패');
      }

      const recommendationsData = await recommendationsResponse.json();

      const movies = await Promise.all(
        recommendationsData.map(async (movie) => {
          try {
            const detailResponse = await fetch(
              `/api/movies/detail/${movie.movieCd}?language=ko`,
              {
                headers: {
                  Authorization: `Bearer ${accessToken}`,
                },
              }
            );

            if (!detailResponse.ok) {
              return null;
            }

            const detailData = await detailResponse.json();
            const reason = await fetchRecommendationReason(movie.movieCd);

            return {
              id: movie.movieCd,
              title: detailData.title,
              posterPath: detailData.poster_path
                ? `https://image.tmdb.org/t/p/w500${detailData.poster_path}`
                : null,
              reason,
            };
          } catch (error) {
            return null;
          }
        })
      );

      setRecommendations(movies.filter((movie) => movie !== null));
    } catch (error) {
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleMovieClick = (movieId) => {
    navigate(`/api/movies/detail/${movieId}?language=ko`);
  };

  const openReasonModal = (reason) => {
    setSelectedReason(reason);
  };

  const closeReasonModal = () => {
    setSelectedReason(null);
  };

  useEffect(() => {
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
          <div className="movie-list">
            {recommendations.map((movie) => (
              <div className="movie-item" key={movie.id}>
                <div onClick={() => handleMovieClick(movie.id)}>
                  {movie.posterPath ? (
                    <img
                      src={movie.posterPath}
                      alt={movie.title}
                      className="movie-poster"
                    />
                  ) : (
                    <div className="no-poster">포스터 없음</div>
                  )}
                  <h3 className="movie-title">{movie.title}</h3>
                </div>
                <button
                  className="reason-button"
                  onClick={() => openReasonModal(movie.reason)}
                >
                  추천 이유 보기
                </button>
              </div>
            ))}
          </div>
        )}
      </div>

      {selectedReason && (
        <div className="reason-modal">
          <div className="modal-content">
            <p>{selectedReason}</p>
            <button className="close-button" onClick={closeReasonModal}>
              닫기
            </button>
          </div>
        </div>
      )}

      <button
        className="chatbot-button"
        aria-label="챗봇 열기"
        onClick={() => setChatOpen(true)}
      >
        💬
      </button>

      {chatOpen && (
        <Chatbot isOpen={chatOpen} onClose={() => setChatOpen(false)} />
      )}
    </div>
  );
};

export default RecommendPage;
