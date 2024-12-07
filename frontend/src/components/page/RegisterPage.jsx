import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/RegisterPage.css';

const RegisterPage = () => {
  const [nickname, setNickname] = useState('');
  const [membername, setMembername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();

    // 유효성 검사
    if (!nickname || !membername || !password || !confirmPassword) {
      alert('모든 필드를 입력해 주세요.');
      return;
    }

    if (password !== confirmPassword) {
      alert('비밀번호가 일치하지 않습니다.');
      return;
    }

    const data = {
      membername: membername,
      password: password,
      nickname: nickname,
    };

    try {
      const response = await fetch('/member/signup', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      });

      if (response.ok) {
        const result = await response.json();
        const { id, role } = result; // 응답에서 id와 role 추출

        if (!id || !role) {
          console.error('응답에 id 또는 role 정보가 없습니다.');
          alert('회원가입은 성공했지만 추가 정보를 저장하지 못했습니다.');
          return;
        }

        // Local Storage에 role 저장
        localStorage.setItem('memberId', id);
        localStorage.setItem('role', role);

        // 성공 메시지와 페이지 이동
        alert('회원가입이 성공적으로 완료되었습니다!');
        navigate(`/survey?userId=${id}`);
      } else {
        const errorResult = await response.json();
        console.error('회원가입 실패:', errorResult.message || 'Unknown error');
        alert(`회원가입에 실패했습니다: ${errorResult.message || '다시 시도해 주세요.'}`);
      }
    } catch (error) {
      console.error('서버와의 연결에 문제가 발생했습니다:', error);
      alert('오류가 발생했습니다. 잠시 후 다시 시도해 주세요.');
    }
  };

  return (
    <div className="register-page">
      <form onSubmit={handleRegister} className="register-form">
        <h2>회원가입</h2>
        <input
          type="text"
          placeholder="닉네임"
          value={nickname}
          onChange={(e) => setNickname(e.target.value)}
        />
        <input
          type="email"
          placeholder="아이디 (이메일)"
          value={membername}
          onChange={(e) => setMembername(e.target.value)}
        />
        <input
          type="password"
          placeholder="비밀번호"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <input
          type="password"
          placeholder="비밀번호 확인"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
        />
        <button type="submit">회원가입</button>
      </form>
    </div>
  );
};

export default RegisterPage;
