import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/LoginPage.css';

const LoginPage = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate(); // 페이지 이동을 위한 useNavigate 사용

  const handleLogin = async (e) => {
    e.preventDefault();

    // 유효성 검사: 모든 필드가 입력되었는지 확인
    if (!username || !password) {
      alert('모든 필드를 입력해 주세요.');
      return;
    }

    // 요청 데이터 생성
    const loginData = {
      membername: username,
      password: password,
    };

    try {
      // 로그인 API 호출
      const response = await axios.post('/member/login', loginData, {
        headers: {
          'Content-Type': 'application/json',
        },
      });

      // 서버 응답 처리
      console.log('Login Response:', response.data);
      
      // 응답 데이터에서 필요한 정보를 추출
      const { memberId, statusCode, message, accessToken } = response.data;

      if (statusCode === 200 && accessToken) {
        alert(message);
        
        // 토큰과 멤버 ID 저장
        localStorage.setItem('token', accessToken);
        localStorage.setItem('memberId', memberId); // 멤버 ID 저장
        
        console.log('Login successful. Navigating to main...');
        
        // 메인 페이지로 이동
        navigate('/main', { replace: true });
      } else {
        console.error('Login failed:', message);
        alert(message || 'Invalid username or password.');
      }

    } catch (error) {
      if (error.response) {
        console.error('Login error:', error.response.data);
        alert(`서버 오류: ${error.response.data.message || 'Failed to login'}`);
      } else {
        console.error('Network or unknown error:', error.message);
        alert('네트워크 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.');
      }
    }
  };

  return (
    <div className="login-page">
      <form onSubmit={handleLogin} className="login-form">
        <h2>로그인</h2>
        <input
          type="text"
          placeholder="아이디"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <input
          type="password"
          placeholder="비밀번호"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <button type="submit">로그인</button>
      </form>
    </div>
  );
};

export default LoginPage;
