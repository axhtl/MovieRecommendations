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
        if (password !== confirmPassword) {
            alert('Passwords do not match!');
            return;
        }

        const data = {
            membername: membername,
            password: password,
            nickname: nickname,
        };

        try {
            const response = await fetch('http://localhost:8080/member/signup', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            });

            if (response.ok) {
                alert('Registration successful!');
                navigate('/survey'); // 회원가입 후 설문조사 페이지로 이동
            } else {
                alert('Failed to register. Please try again.');
            }
        } catch (error) {
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
