import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/Navbar.css'; // 스타일 파일 경로
import { FiLogOut, FiSearch } from 'react-icons/fi'; // 로그아웃, 검색 아이콘 추가

function Navbar() {
  const [searchQuery, setSearchQuery] = useState(''); // 검색어 상태 관리
  const navigate = useNavigate(); // 경로 이동을 위한 useNavigate

  const tmdbApiToken = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI2MjIzNWYyOWJhYjEzZGRjOTVlNzRmZGFlMDFlZDg1MCIsIm5iZiI6MTczMjk2NDExMC4yNzcsInN1YiI6IjY3NGFlZjBlYTEyMzE5ZTVjZTBjZmM3YyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.A_KVw6zVQ_no9vKL_mi_WtESpSJajqjwcgAtwWSj4Ns"; // TMDB API 토큰

  // 로그아웃 핸들러 함수
  const handleLogout = () => {
    localStorage.removeItem('accessToken'); // 예시로 로컬스토리지에서 토큰 삭제
    navigate('/'); // 로그아웃 후 메인 페이지로 이동
  };

  // 검색 핸들러 함수 추가
  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      alert('검색어를 입력해주세요.'); // 검색어가 없을 때 알림
      return;
    }

    try {
      // TMDB API에 검색 요청 보내기
      const response = await axios.get('https://api.themoviedb.org/3/search/movie', {
        headers: {
          Authorization: `Bearer ${tmdbApiToken}`,
        },
        params: {
          query: searchQuery,
          language: 'ko',
        },
      });

      if (response.data && response.data.results.length > 0) {
        const searchResults = response.data.results;
        // 검색 결과를 URL에 포함하여 `SearchPage`로 이동
        navigate(`/movie/search?results=${encodeURIComponent(JSON.stringify(searchResults))}&query=${encodeURIComponent(searchQuery)}`);
      } else {
        alert('검색 결과가 없습니다.');
      }
    } catch (error) {
      console.error('검색 오류:', error);
      if (error.response) {
        console.error('서버 응답 오류:', error.response);
      }
      alert('검색 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.');
    }
  };

  // 엔터 키로 검색 실행 핸들러
  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearch(); // Enter 키로 검색 실행
    }
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="logo">
          <img src="/icons/group-icon.png" alt="Logo" />
        </div>
        <ul className="nav-links">
          <li><a href="/main">Home</a></li>
          <li><a href="/member/my">마이 페이지</a></li>
          <li><a href="/recommend">영화 추천</a></li>
        </ul>
        <div className="search-bar">
          <input
            type="text"
            placeholder="영화 검색..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)} // 검색어 입력 핸들링
            onKeyPress={handleKeyPress} // Enter 키로 검색 실행
          />
          <button onClick={handleSearch} className="search-button">
            <FiSearch size={20} /> {/* 돋보기 아이콘 추가 */}
          </button>
          <button className="logout-button" onClick={handleLogout}>
            <FiLogOut size={20} /> {/* 로그아웃 아이콘 추가 */}
          </button>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
