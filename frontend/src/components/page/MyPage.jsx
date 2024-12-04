import React, { useEffect, useState } from 'react';
import Navbar from '../ui/Navbar';
import MemberInfo from '../list/MemberInfo';
import UpcomingMovies from '../list/UpcomingMovies'; // 업커밍무비스 컴포넌트
import MovieList from '../list/MovieList';
import '../styles/MyPage.css';
import axios from 'axios';

const MyPage = () => {
  const [memberInfo, setMemberInfo] = useState(null);
  const [registeredMovies, setRegisteredMovies] = useState([]); // 등록된 영화 목록
  const [upcomingMovies, setUpcomingMovies] = useState([]); // 개봉 예정 영화 목록
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const userId = localStorage.getItem('userId');
    const token = localStorage.getItem('token');

    if (!userId || !token) {
      console.error('LocalStorage 값이 없습니다. userId 또는 token을 확인하세요.');
      setError('로그인 정보가 없습니다. 로그인 후 이용해주세요.');
      setLoading(false);
      return;
    }

    const fetchMemberInfo = async () => {
      try {
        const response = await axios.get(`/survey/${userId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setMemberInfo(response.data);
      } catch (err) {
        console.error('회원 정보 불러오기 오류:', err.message);
        setError('회원 정보를 불러오지 못했습니다.');
      }
    };

    const fetchRegisteredMovies = async () => {
      try {
        const response = await axios.get(`/api/user/movies`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setRegisteredMovies(response.data || []); // 등록된 영화 데이터 설정
      } catch (err) {
        console.error('등록된 영화 데이터 불러오기 오류:', err.message);
        setRegisteredMovies([]); // 오류 발생 시 빈 배열 설정
      }
    };

    const fetchUpcomingMovies = async () => {
      try {
        const response = await axios.get('/movie/upcoming', {
          params: { language: 'ko-KR', region: 'KR' },
          headers: { Authorization: `Bearer ${token}` },
        });
        setUpcomingMovies(response.data.results.slice(0, 2)); // 상위 2개만 저장
      } catch (err) {
        console.error('개봉 예정 영화 데이터 불러오기 오류:', err.message);
        setUpcomingMovies([]); // 오류 발생 시 빈 배열 설정
      }
    };

    fetchMemberInfo();
    fetchRegisteredMovies();
    fetchUpcomingMovies();
    setLoading(false);
  }, []);

  if (loading) return <p>Loading...</p>;
  if (error) return <p>{error}</p>;

  return (
    <div className="my-page">
      <Navbar />
      <div className="content-wrapper">
        <div className="member-info-section">
          <MemberInfo data={memberInfo} />
        </div>
        <div className="registered-movies-section">
          <h2>내가 등록한 영화</h2>
          <MovieList movies={registeredMovies} />
        </div>
      </div>
      <div className="upcoming-movies-section">
        <UpcomingMovies movies={upcomingMovies} />
      </div>
    </div>
  );
};

export default MyPage;
