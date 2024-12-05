import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Navbar from '../ui/Navbar';
import RegiMovieList from '../list/RegiMovieList';
import '../styles/MainPage.css';

const MainPage = () => {
  const { userId } = useParams(); // URL에서 userId 가져오기
  const [movies, setMovies] = useState([]); // 영화 목록
  const [nickname, setNickname] = useState(''); // 사용자 닉네임
  const [isLoading, setIsLoading] = useState(true); // 로딩 상태
  const navigate = useNavigate();

  useEffect(() => {
    const fetchMovies = async () => {
      const accessToken = localStorage.getItem('accessToken');

      if (!userId || !accessToken) {
        alert('로그인이 필요합니다. 로그인 페이지로 이동합니다.');
        navigate('/'); // 로그인 페이지로 이동
        return;
      }

      try {
        const response = await fetch(`/member/user/${userId}`, {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        });

        if (!response.ok) throw new Error('API 호출 실패');

        const data = await response.json();
        setMovies(data.reviewInfos || []); // 영화 데이터 저장
        setNickname(data.member.nickname || ''); // 사용자 닉네임 저장
      } catch (error) {
        console.error('영화 정보를 불러오는 데 실패했습니다:', error.message);
      } finally {
        setIsLoading(false);
      }
    };

    fetchMovies();
  }, [userId, navigate]); // userId가 변경될 때 데이터를 다시 가져옴

  const handleSearchClick = () => {
    navigate('/movie/view'); // 영화 등록 페이지로 이동
  };

  if (isLoading) {
    return (
      <div className="main-page">
        <Navbar />
        <p>로딩 중...</p>
      </div>
    );
  }

  return (
    <div className="main-page">
      <Navbar />
      <div className="main-content">
        {movies.length === 0 ? (
          <div className="no-movies">
            <h2>{nickname} 님을 위한 영화가 아직 없어요 ㅠ</h2>
            <p>바로 등록하러 가볼까요?</p>
            <button className="add-movie-button" onClick={handleSearchClick}>
              영화 등록하기
            </button>
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
