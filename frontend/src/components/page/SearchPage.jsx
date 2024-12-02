import React, { useEffect, useState } from 'react';
import Navbar from '../ui/Navbar';
import '../styles/SearchPage.css';
import { useNavigate, useLocation } from 'react-router-dom';

const SearchPage = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const query = queryParams.get('query');
    if (query) {
      setSearchTerm(query);
      handleSearch(query);
    }
  }, [location.search]);

  const handleSearch = (query) => {
    // 영화 데이터를 서버에서 검색하는 로직을 구현 (현재는 임시 데이터 사용)
    setSearchResults([
      { id: 1, name: '영화 이름 1', imageUrl: '/images/movie-placeholder.png' },
      { id: 2, name: '영화 이름 2', imageUrl: '/images/movie-placeholder.png' },
      { id: 3, name: '영화 이름 3', imageUrl: '/images/movie-placeholder.png' },
    ]);
  };

  const handleMovieClick = (movieId) => {
    // 영화 클릭 시 영화 등록 페이지로 이동
    navigate(`/register-movie/${movieId}`);
  };

  return (
    <div className="search-page">
      <Navbar />
      <div className="search-content">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            handleSearch(searchTerm);
          }}
          className="search-form"
        >
          <input
            type="text"
            placeholder="검색어를 입력하세요"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          <button type="submit">🔍</button>
        </form>
        <div className="search-results">
          {searchResults.length > 0 && (
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
    </div>
  );
};

export default SearchPage;
