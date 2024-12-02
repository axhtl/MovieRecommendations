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
      if (response.data.success) {
        alert(response.data.message); // 성공 메시지
        localStorage.setItem('token', response.data.token); // JWT 토큰 저장 (필요 시)
        navigate('/main'); // MainPage로 이동
      } else {
        alert(response.data.message || 'Invalid username or password.');
      }
    } catch (error) {
      console.error('Login error:', error.response || error.message);
      alert('Failed to login. Please try again.');
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
