import React, { useEffect, useState } from 'react';
import Navbar from '../ui/Navbar';
import '../styles/MyPage.css';
import axios from 'axios';

const MyPage = () => {
  const [memberInfo, setMemberInfo] = useState(null);
  const [surveyInfo, setSurveyInfo] = useState(null);

  useEffect(() => {
    // 회원 정보를 불러오는 함수
    const fetchMemberInfo = async () => {
      try {
        const response = await axios.get('/member/signup'); // 회원 정보 API 호출
        setMemberInfo(response.data);
      } catch (error) {
        console.error('회원 정보 불러오기 오류:', error);
      }
    };

    // 설문조사 정보를 불러오는 함수
    const fetchSurveyInfo = async () => {
      try {
        const response = await axios.get('/survey'); // 설문조사 정보 API 호출
        setSurveyInfo(response.data);
      } catch (error) {
        console.error('설문조사 정보 불러오기 오류:', error);
      }
    };

    fetchMemberInfo();
    fetchSurveyInfo();
  }, []);

  if (!memberInfo || !surveyInfo) {
    return <p>Loading...</p>; // 데이터를 불러오는 동안 로딩 메시지 표시
  }

  return (
    <div className="my-page">
      <Navbar />
      <div className="my-info">
        <h2>내 정보</h2>
        <p><strong>닉네임:</strong> {memberInfo.nickname}</p>
        <p><strong>로그인 아이디:</strong> {memberInfo.membername}</p>
        <p><strong>성별:</strong> {surveyInfo.gender}</p>
        <p><strong>나이:</strong> {surveyInfo.age}</p>
      </div>
      <button className="update-button">수정</button>
    </div>
  );
};

export default MyPage;
