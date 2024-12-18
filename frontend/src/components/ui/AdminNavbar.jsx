import React from 'react';
import { Link } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import '../styles/Navbar.css';
import { faFilm } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { FiLogOut } from 'react-icons/fi';

function AdminNavbar() {

  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('memberId');
    localStorage.removeItem('token'); // JWT 토큰 삭제
    alert('로그아웃 되었습니다.');
    navigate('/'); // 로그인 페이지로 이동
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="logo">
          <FontAwesomeIcon icon={faFilm} size="2x" alt="Movie Icon" />
        </div>
        <ul className="nav-links">
          <li><Link to="/admin/users">회원 관리</Link></li>
          <li><Link to="/admin/survey">설문조사 조회</Link></li>
          <li><Link to="/admin/movie">영화 기록 조회</Link></li>
          <li><Link to="/admin/reports">통계 조회</Link></li>
        </ul>
        <div className="search-bar">
          <button className="logout-button" onClick={handleLogout}>
            <FiLogOut size={20} />
          </button>
        </div>
      </div>
    </nav>
  );
}

export default AdminNavbar;
