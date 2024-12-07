import React from 'react';
import { Link } from 'react-router-dom';

function AdminNavbar() {
  return (
    <nav>
      <ul>
        <li><Link to="/admin/dashboard">대시보드</Link></li>
        <li><Link to="/admin/users">사용자 관리</Link></li>
        <li><Link to="/admin/settings">설정</Link></li>
        <li><Link to="/admin/reports">리포트</Link></li>
      </ul>
    </nav>
  );
}

export default AdminNavbar;
