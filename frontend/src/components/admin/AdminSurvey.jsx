import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AdminNavbar from '../ui/AdminNavbar';
import axios from 'axios';
import '../styles/admin/AdminUsers.css'; // 기존 스타일 재사용

function AdminSurvey() {
  const [surveys, setSurveys] = useState([]); // 설문 데이터를 저장할 상태
  const navigate = useNavigate();

  useEffect(() => {
    const memberId = localStorage.getItem('memberId');
    const token = localStorage.getItem('token');

    // 관리자 권한 확인
    if (!memberId || memberId !== '14' || !token) {
      alert('접근 권한이 없습니다.');
      navigate('/');
    }

    // 설문 데이터 가져오기
    const fetchSurveys = async () => {
      try {
        const response = await axios.get('/member/users', {
          headers: {
            Authorization: `Bearer ${token}`, // 토큰 인증 헤더 추가
          },
        });
        setSurveys(response.data); // 설문 데이터 설정
      } catch (error) {
        console.error('설문 데이터를 가져오는 중 오류 발생:', error);
        alert('설문 데이터를 가져올 수 없습니다.');
      }
    };

    fetchSurveys();
  }, [navigate]);

  return (
    <div className="admin-page">
      <AdminNavbar />
      <div className="admin-content">
        <h1>설문 기록 조회</h1>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Member Name</th>
              <th>Age</th>
              <th>Gender</th>
              <th>Preferred Genres</th>
            </tr>
          </thead>
          <tbody>
            {surveys.map((survey, index) => (
              <tr key={index}>
                <td>{survey.member.memberId}</td>
                <td>{survey.member.membername}</td>
                <td>{survey.survey.age}</td>
                <td>{survey.survey.gender}</td>
                <td>{survey.preferredGenres.join(', ')}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default AdminSurvey;
