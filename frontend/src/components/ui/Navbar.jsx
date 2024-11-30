import React from 'react';
import { Link } from 'react-router-dom';
import '../styles/Navbar.css';

function Navbar() {
  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="logo">
          <img src="/icons/group-icon.png" alt="Logo" />
        </div>
        <ul className="nav-links">
          <li><Link to="/main">Home</Link></li>
          <li><Link to="/my">마이 페이지</Link></li>
          <li><Link to="/recommend">영화 추천</Link></li>
        </ul>
        <div className="search-bar">
          <input type="text" placeholder="영화 검색..." />
          <button>🔍</button>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;