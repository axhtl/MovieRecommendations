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

        // 유효성 검사: 모든 필드가 입력되었는지 확인
        if (!nickname || !membername || !password || !confirmPassword) {
            alert('모든 필드를 입력해 주세요.');
            return;
        }

        // 비밀번호 확인
        if (password !== confirmPassword) {
            alert('비밀번호가 일치하지 않습니다.');
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
                console.log('회원가입 성공:', result); // 응답 확인
                const memberId = result.id; // 백엔드 응답에서 memberId 추출

                if (!memberId) {
                    console.error("Member ID is missing in the response.");
                    alert("회원가입이 완료되었지만 회원 정보를 불러오는데 실패했습니다. 다시 시도해 주세요.");
                    return;
                }

                // 성공 메시지와 페이지 이동
                alert('회원가입이 성공적으로 완료되었습니다!');
                navigate(`/survey?userId=${memberId}&membername=${membername}&password=${password}`);            } else {
                // 실패 시 처리
                const errorResult = await response.json();
                console.error('회원가입 실패:', errorResult.message || 'Unknown error');
                alert(`회원가입에 실패했습니다. 오류: ${errorResult.message || '다시 시도해 주세요.'}`);
            }
        } catch (error) {
            // 네트워크 또는 서버 오류 처리
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
