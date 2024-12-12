import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AdminNavbar from '../ui/AdminNavbar';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import { Pie } from 'react-chartjs-2';
import '../styles/admin/AdminReports.css'; // 스타일 파일

// Chart.js 요소 등록
ChartJS.register(ArcElement, Tooltip, Legend);

function AdminReports() {
  const [userData, setUserData] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const memberId = localStorage.getItem('memberId');
    const token = localStorage.getItem('token');

    if (!memberId || memberId !== '14' || !token) {
      alert('접근 권한이 없습니다.');
      navigate('/');
      return;
    }

    const fetchUserData = async () => {
      try {
        const response = await fetch('/member/users', {
          headers: { Authorization: `Bearer ${token}` },
        });
        const data = await response.json();
        setUserData(data);
      } catch (error) {
        console.error('데이터를 가져오는 중 오류 발생:', error);
      }
    };

    fetchUserData();
  }, [navigate]);

  // 통계 데이터 계산
  const calculateStats = () => {
    const genderStats = { male: 0, female: 0 };
    const ageStats = {};
    const genreStats = {};

    userData.forEach((user) => {
      // 성별 통계
      if (user.survey.gender === 'M') genderStats.male += 1;
      if (user.survey.gender === 'F') genderStats.female += 1;

      // 나이 통계
      const ageGroup = `${Math.floor(user.survey.age / 10) * 10}대`; // 10대, 20대 형식
      ageStats[ageGroup] = (ageStats[ageGroup] || 0) + 1;

      // 선호 장르 통계
      user.preferredGenres.forEach((genre) => {
        genreStats[genre] = (genreStats[genre] || 0) + 1;
      });
    });

    return { genderStats, ageStats, genreStats };
  };

  const { genderStats, ageStats, genreStats } = calculateStats();

  // 차트 데이터 생성
  const pieChartData = (labels, data) => ({
    labels,
    datasets: [
      {
        label: '통계',
        data,
        backgroundColor: ['#36A2EB', '#FF6384', '#FFCE56', '#4BC0C0', '#9966FF'],
        hoverBackgroundColor: ['#36A2EB', '#FF6384', '#FFCE56', '#4BC0C0', '#9966FF'],
      },
    ],
  });

  return (
    <div className="admin-page">
      <AdminNavbar />
      <div className="admin-content">
        <h1>서비스 통계 조회</h1>
        <div className="charts-container">
          {/* 사용자 선호 장르 통계 */}
          <div className="chart">
            <h3>사용자 선호 장르 통계</h3>
            <Pie data={pieChartData(Object.keys(genreStats), Object.values(genreStats))} />
          </div>

          {/* 성별 통계 */}
          <div className="chart">
            <h3>성별 통계</h3>
            <Pie data={pieChartData(['Male', 'Female'], [genderStats.male, genderStats.female])} />
          </div>

          {/* 연령별 선호 장르 통계 */}
          {Object.entries(ageStats).map(([ageGroup, count]) => (
            <div className="chart" key={ageGroup}>
              <h3>{ageGroup} 선호 장르 통계</h3>
              <Pie data={pieChartData(Object.keys(genreStats), Object.values(genreStats))} />
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default AdminReports;
