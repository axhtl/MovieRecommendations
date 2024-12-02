import React, { useEffect, useState } from 'react';
import Navbar from '../ui/Navbar';
import '../styles/SearchPage.css';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';

const SearchPage = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [itemPerPage, setItemPerPage] = useState(10); // itemPerPage 상태 추가
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    // URL에서 쿼리 파라미터 추출
    const queryParams = new URLSearchParams(location.search);
    const query = queryParams.get('movieName');
    if (query) {
      setSearchTerm(query);
      handleSearch(query, itemPerPage); // itemPerPage 전달
    } else {
      setSearchResults([]); // 검색어가 없을 경우 검색 결과 초기화
    }
  }, [location.search, itemPerPage]);

  const handleSearch = async (query, itemsPerPage) => {
    try {
      // 서버로 검색 요청 보내기
      const response = await axios.get('http://localhost:8080/search-movies', {
        params: { movieName: query, itemPerPage: itemsPerPage }, // itemPerPage 전달
      });
      // 검색 결과 업데이트
      setSearchResults(response.data);
    } catch (error) {
      console.error('검색 오류:', error);
      setSearchResults([]); // 검색 실패 시 결과 초기화
    }
  };

  const handleMovieClick = (movieId) => {
    // 영화 클릭 시 영화 등록 페이지로 이동
    navigate(`/registermovie/${movieId}`);
  };

  return (
    <div className="search-page">
      <Navbar />
      <div className="search-content">
        <div className="search-options">
          {/* itemPerPage 선택 기능 추가 */}
          <label>
            페이지당 아이템 수:
            <select
              value={itemPerPage}
              onChange={(e) => setItemPerPage(Number(e.target.value))}
            >
              <option value={5}>5</option>
              <option value={10}>10</option>
              <option value={15}>15</option>
              <option value={20}>20</option>
            </select>
          </label>
        </div>
        {searchResults.length === 0 ? (
          <p>검색 결과가 없습니다.</p> // 검색 결과가 없을 때 메시지 표시
        ) : (
          <div className="movie-list">
            {searchResults.map((movie) => (
              <div
                key={movie.id}
                className="movie-item"
                onClick={() => handleMovieClick(movie.id)}
              >
                <img src={movie.imageUrl} alt={movie.name} />
                <p>{movie.name}</p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default SearchPage;
