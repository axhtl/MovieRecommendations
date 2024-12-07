import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Navbar from '../ui/Navbar';
import MemberInfo from '../list/MemberInfo';
import RegiMovieList from '../list/RegiMovieList';
import UpcomingMovies from '../list/UpcomingMovies';
import axios from 'axios';
import '../styles/MyPage.css';

const MyPage = () => {
  const { userId } = useParams(); // URL에서 userId 가져오기
  const [memberData, setMemberData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');

    // 토큰 유효성 확인
    const validateToken = () => {
      if (!token) {
        alert('로그인이 필요합니다. 로그인 페이지로 이동합니다.');
        navigate('/');
        return false;
      }

      const parts = token.split('.');
      if (parts.length !== 3) {
        alert('유효하지 않은 토큰입니다. 다시 로그인해주세요.');
        localStorage.removeItem('token');
        navigate('/');
        return false;
      }
      return true;
    };

    if (!validateToken()) return;

    const fetchData = async () => {
      try {
        const response = await axios.get(`/member/user/${userId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        setMemberData(response.data);
      } catch (err) {
        console.error('데이터를 불러오는 중 문제가 발생했습니다:', err);
        setError(
          err.response?.data?.message || '데이터를 불러오는 중 문제가 발생했습니다.'
        );
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [userId, navigate]);

  if (loading) return <div>로딩 중...</div>;
  if (error) return <div>{error}</div>;

  // 탈퇴 처리 함수
  const handleWithdraw = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) throw new Error('Token is missing or invalid.');

      await axios.put(
        `/member/withdraw/${userId}`,
        {},
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      alert('탈퇴가 완료되었습니다.');
      localStorage.removeItem('token');
      navigate('/'); // 로그인 페이지로 이동
    } catch (err) {
      console.error('탈퇴 중 문제가 발생했습니다:', err);
      alert('탈퇴 중 오류가 발생했습니다. 다시 시도해주세요.');
    }
  };

  // 로그아웃 처리 함수
  const handleLogout = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) throw new Error('Token is missing or invalid.');

      await axios.post(
        '/member/logout',
        {},
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      alert('로그아웃되었습니다.');
      localStorage.removeItem('token');
      navigate('/'); // 로그인 페이지로 이동
    } catch (err) {
      console.error('로그아웃 중 문제가 발생했습니다:', err);
      alert('로그아웃 중 오류가 발생했습니다. 다시 시도해주세요.');
    }
  };

  return (
    <div className="my-page">
      <Navbar />
      <div className="content-wrapper">
        <div className="top-section">
          <div className="member-info-section">
            <MemberInfo
              member={memberData?.member}
              survey={memberData?.survey}
              preferredGenres={memberData?.preferredGenres}
              preferredActors={memberData?.preferredActors}
            />
          </div>
          <div className="upcoming-movies-container">
            <UpcomingMovies />
          </div>
        </div>

        <div className="registered-movies-section">
          <h2>내가 등록한 영화</h2>
          {memberData?.reviewInfos?.length === 0 ? (
            <p>등록된 영화가 없습니다.</p>
          ) : (
            <RegiMovieList movies={memberData.reviewInfos} />
          )}
        </div>

        {/* 탈퇴와 로그아웃 버튼 */}
        <div className="account-actions">
          <button className="account-button logout-button" onClick={handleLogout}>
            로그아웃
          </button>
          <button className="account-button withdraw-button" onClick={handleWithdraw}>
            회원 탈퇴
          </button>
        </div>
      </div>
    </div>
  );
};

export default MyPage;
