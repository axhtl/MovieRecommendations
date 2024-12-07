import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/LoginPage.css';

const LoginPage = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false); // 로딩 상태 추가
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    if (!username || !password) {
      alert('아이디와 비밀번호를 입력해 주세요.');
      return;
    }

    setLoading(true); // 로딩 상태 활성화

    try {
      const response = await axios.post('/member/login', {
        membername: username,
        password: password,
      });

      const { memberId, role, statusCode, message, accessToken } = response.data;

      if (statusCode === 200 && memberId && accessToken) {
        alert(message || '로그인 성공!');

        // Local Storage에 토큰 및 사용자 정보 저장
        localStorage.setItem('memberId', memberId);
        localStorage.setItem('role', role); // role 저장
        localStorage.setItem('token', accessToken);

        // role에 따라 페이지 이동
        if (role === 'ADMIN') {
          navigate('/admin/dashboard', { replace: true }); // 관리자 전용 화면으로 이동
        } else if (role === 'USER') {
          navigate(`/main/${memberId}`, { replace: true }); // 사용자 전용 화면으로 이동
        } else {
          alert('알 수 없는 권한입니다. 다시 시도해 주세요.');
        }
      } else {
        alert(message || '로그인 실패. 다시 시도해 주세요.');
      }
    } catch (error) {
      console.error('로그인 에러:', error);
      alert(
        error.response?.data?.message || '로그인 중 문제가 발생했습니다. 다시 시도해 주세요.'
      );
    } finally {
      setLoading(false); // 로딩 상태 비활성화
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
          disabled={loading} // 로딩 중 입력 비활성화
        />
        <input
          type="password"
          placeholder="비밀번호"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          disabled={loading} // 로딩 중 입력 비활성화
        />
        <button type="submit" disabled={loading}>
          {loading ? '로그인 중...' : '로그인'}
        </button>
      </form>
    </div>
  );
};

export default LoginPage;
