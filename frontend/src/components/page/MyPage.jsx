import React, { useEffect, useState } from 'react';
import Navbar from '../ui/Navbar';
import MemberInfo from '../list/MemberInfo';
import '../styles/MyPage.css';
import axios from 'axios';

const MyPage = () => {
  const [memberInfo, setMemberInfo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const userId = localStorage.getItem('userId'); // LocalStorage에서 userId 가져오기
    const token = localStorage.getItem('token'); // LocalStorage에서 token 가져오기

    // userId와 token 값 확인
    if (!userId || !token) {
      console.error('LocalStorage 값이 없습니다. userId 또는 token을 확인하세요.');
      setError('로그인 정보가 없습니다. 로그인 후 이용해주세요.');
      setLoading(false);
      return;
    }

    const fetchMemberInfo = async () => {
      try {
        console.log(`Fetching data for memberId: ${userId}`); // userId는 실제로 memberId
        const response = await axios.get(`/survey/${userId}`, {
          headers: {
            Authorization: `Bearer ${token}`, // 토큰 추가
          },
        });

        console.log('응답 데이터:', response.data);
        setMemberInfo(response.data); // 상태 업데이트
      } catch (error) {
        console.error('API 호출 오류:', error.response || error.message);
        if (error.response?.status === 401) {
          setError('인증이 만료되었습니다. 다시 로그인해주세요.');
        } else if (error.response?.status === 404) {
          setError('회원 정보를 찾을 수 없습니다.');
        } else {
          setError('서버로부터 데이터를 불러오지 못했습니다.');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchMemberInfo();
  }, []);

  if (loading) return <p>Loading...</p>;
  if (error) return <p>{error}</p>;

  return (
    <div className="my-page">
      <Navbar />
        <MemberInfo data={memberInfo} />
      
    </div>
  );
};

export default MyPage;
