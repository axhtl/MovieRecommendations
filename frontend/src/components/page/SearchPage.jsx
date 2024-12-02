import React, { useEffect, useState } from 'react';
import Navbar from '../ui/Navbar';
import MovieList from '../list/MovieList';
import '../styles/SearchPage.css';
import { useLocation, useNavigate } from 'react-router-dom';

const SearchPage = () => {
  const [searchResults, setSearchResults] = useState([]);
  const [searchTerm, setSearchTerm] = useState(''); // 검색어 상태 관리
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    // URL에서 쿼리 파라미터 추출
    const queryParams = new URLSearchParams(location.search);
    const results = queryParams.get('results');
    const query = queryParams.get('query'); // 검색어 추출

    try {
      const parsedResults = results ? JSON.parse(decodeURIComponent(results)) : [];
      setSearchResults(parsedResults);
    } catch (error) {
      console.error('결과 파싱 중 오류:', error);
    }

    if (query) {
      setSearchTerm(query); // 검색어 저장
    }
  }, [location.search]);

  const handleMovieClick = (movieCd) => {
    console.log('Navigating to movie details with ID:', movieCd); // 로그 추가
    navigate(`/search-details/${movieCd}`);
  };
  

  return (
    <div className="search-page">
      <Navbar />
      <div className="search-content">
        {searchResults.length === 0 && (
          <p className="no-results-message">검색 결과가 없습니다.</p> // 검색 결과가 없을 때 메시지 표시
        )}
        {searchResults.length > 0 && (
          <div className="movie-category">
            <h2>'{searchTerm}' 검색 결과</h2>
            <MovieList movies={searchResults} onMovieClick={handleMovieClick} />
          </div>
        )}
      </div>
    </div>
  );
};

export default SearchPage;
