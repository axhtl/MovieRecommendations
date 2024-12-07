import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/Navbar.css';
import { FiLogOut, FiSearch } from 'react-icons/fi';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faFilm } from '@fortawesome/free-solid-svg-icons';

function Navbar() {
  const [userId, setUserId] = useState(null); // userId 상태 관리
  const [searchQuery, setSearchQuery] = useState('');
  const navigate = useNavigate();

  // 로컬 스토리지에서 userId 가져오기
  useEffect(() => {
    const storedUserId = localStorage.getItem('memberId');
    setUserId(storedUserId); // 상태 업데이트
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('memberId');
    localStorage.removeItem('accessToken');
    alert('로그아웃 되었습니다.');
    navigate('/'); // 로그인 페이지로 이동
  };

  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      alert('검색어를 입력해 주세요.');
      return;
    }

    try {
      const response = await axios.get('/api/movies/search', {
        params: {
          query: searchQuery,
          includeAdult: false,
          language: 'ko',
          page: 1,
        },
      });

      if (response.data && response.data.results.length > 0) {
        const searchResults = response.data.results;
        navigate(
          `/movie/search?results=${encodeURIComponent(
            JSON.stringify(searchResults)
          )}&query=${encodeURIComponent(searchQuery)}`
        );
      } else {
        alert('검색 결과가 없습니다.');
      }
    } catch (error) {
      console.error('검색 오류:', error);
      alert('검색 중 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.');
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  const navigateTo = (path) => {
    if (userId) {
      navigate(path.replace(':userId', userId));
    } else {
      alert('로그인이 필요합니다.');
    }
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="logo">
          <FontAwesomeIcon icon={faFilm} size="2x" alt="Movie Icon" />
        </div>
        <ul className="nav-links">
          <li>
            <button
              className="nav-link-button"
              onClick={() => navigateTo('/main/:userId')}
            >
              Home
            </button>
          </li>
          <li>
            <button
              className="nav-link-button"
              onClick={() => navigateTo('/api/ai/predict/:userId')}
            >
              영화 추천
            </button>
          </li>
          <li>
            <button
              className="nav-link-button"
              onClick={() => navigateTo('/my/:userId')}
            >
              마이 페이지
            </button>
          </li>
        </ul>
        <div className="search-bar">
          <input
            type="text"
            placeholder="영화 검색..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onKeyPress={handleKeyPress}
          />
          <button onClick={handleSearch} className="search-button">
            <FiSearch size={20} />
          </button>
          <button className="logout-button" onClick={handleLogout}>
            <FiLogOut size={20} />
          </button>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
