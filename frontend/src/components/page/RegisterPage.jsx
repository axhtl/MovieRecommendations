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

        // 비밀번호 확인
        if (password !== confirmPassword) {
            alert('Passwords do not match!');
            return;
        }

        // 회원가입 데이터
        const data = {
            membername: membername,
            password: password,
            nickname: nickname,
        };

        try {
            // 회원가입 요청
            const response = await fetch('/member/signup', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            });

            if (response.ok) {
                const result = await response.json(); // 서버 응답
                const userId = result.userId; // 백엔드 응답에서 userId 추출
                const registeredNickname = result.nickname; // 백엔드에서 받은 nickname

                // 성공 메시지와 페이지 이동
                alert('Registration successful!');
                navigate(`/survey?userId=${userId}&nickname=${encodeURIComponent(registeredNickname)}`);
            } else {
                // 실패 시 처리
                const errorMessage = await response.text();
                console.error('Failed to register:', errorMessage);
                alert('Failed to register. Please try again.');
            }
        } catch (error) {
            // 네트워크 또는 서버 오류 처리
            console.error('Error:', error);
            alert('Error occurred. Please try again later.');
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
                    placeholder="아이디"
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
