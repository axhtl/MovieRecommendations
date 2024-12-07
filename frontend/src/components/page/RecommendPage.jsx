import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import Navbar from '../ui/Navbar';
import MovieList from '../list/MovieList'; // 재사용되는 MovieList
import '../styles/RecommendPage.css';

const RecommendPage = () => {
  const { userId } = useParams();
  const [recommendations, setRecommendations] = useState([]);
  const [nickname, setNickname] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchNicknameAndRecommendations = async () => {
      const accessToken = localStorage.getItem('token');

      if (!userId || !accessToken) {
        setError('로그인이 필요합니다.');
        setLoading(false);
        return;
      }

      try {
        // 닉네임 가져오기
        const nicknameResponse = await fetch(`/member/user/${userId}`, {
          headers: {
            Authorization: `Bearer ${accessToken}`,
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
                `/api/movies/detail/${movie.movieCd}?language=ko`
              );

              if (!detailResponse.ok) {
                console.error(`영화 상세 정보 요청 실패: ${movie.movieCd}`);
                return null;
              }

              const detailData = await detailResponse.json();
              return {
                id: movie.movieCd,
                title: detailData.name, // MovieItem에서 사용하는 title
                posterPath: detailData.poster_path, // MovieItem에서 사용하는 posterPath
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
        <h2>{nickname ? `${nickname} 님을 위한 추천 영화는?` : '추천 영화 목록'}</h2>
        {error ? (
          <p className="error-message">{error}</p>
        ) : (
          <MovieList movies={recommendations} onMovieClick={(id) => console.log(`영화 클릭됨: ${id}`)} />
        )}
      </div>
    </div>
  );
};

export default RecommendPage;
