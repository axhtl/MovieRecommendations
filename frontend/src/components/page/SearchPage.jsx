import React, { useEffect, useState } from 'react';
import Navbar from '../ui/Navbar';
import MovieList from '../list/MovieList';
import '../styles/SearchPage.css';
import { useLocation, useNavigate } from 'react-router-dom';

const SearchPage = () => {
  const [searchResults, setSearchResults] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    // URL에서 쿼리 파라미터 추출
    const queryParams = new URLSearchParams(location.search);
    const results = queryParams.get('results');
    const query = queryParams.get('query');

    try {
      const parsedResults = results ? JSON.parse(decodeURIComponent(results)) : [];
      setSearchResults(parsedResults);
    } catch (error) {
      console.error('결과 파싱 중 오류:', error);
    }

    if (query) {
      setSearchTerm(query);
    }
  }, [location.search]);

  const handleMovieClick = (movieId) => {
    console.log('Navigating to movie details with ID:', movieId);
    navigate(`/api/movies/detail/${movieId}?language=ko`); // 경로에 쿼리 파라미터 포함
  };

  return (
    <div className="search-page">
      <Navbar />
      <div className="search-content">
        {searchResults.length === 0 && (
          <p className="no-results-message">검색 결과가 없습니다.</p>
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