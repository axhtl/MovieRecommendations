import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/StartPage.css';

const StartPage = () => {
    const navigate = useNavigate();

    useEffect(() => {
        // Hide navbar on the start page
        const navbar = document.querySelector('.navbar');
        if (navbar) {
            navbar.style.visibility = 'hidden';
        }

        return () => {
            // Show navbar again when leaving the start page
            if (navbar) {
                navbar.style.visibility = 'visible';
            }
        };
    }, []);

    return (
        <div className="start-page">
            <div className="header">
                <button className="signup-button" onClick={() => navigate('/member/signup')}>회원가입</button>
            </div>
            <div className="content" style={{ height: '100vh' }}>
                <div className="logo">
                    <img src="/icons/group-icon.png" alt="Logo" />
                </div>
                <h1>MOVIE PICK</h1>
                <button className="login-button" onClick={() => navigate('/member/login')}>로그인</button>
            </div>
            <div className="explanation" style={{ padding: '50px', backgroundColor: '#f9f9f9' }}>
                <h2>AI 추천 알고리즘 방식 대한 설명</h2>
                <p>
                    이 웹사이트는 최신 AI 기술을 사용하여 사용자에게 맞춤형 영화 추천을 제공합니다. 다양한 데이터를 분석하고 개인의 취향을 파악하여 최적의 영화를 추천합니다. 이를 통해 사용자는 새로운 영화와 자신이 좋아할 만한 영화를 더욱 쉽게 찾을 수 있습니다.
                </p>
                <p>
                    영화 추천은 사용자의 시청 이력, 좋아하는 장르, 선호하는 배우 등을 바탕으로 머신러닝 알고리즘을 활용해 이루어집니다. AI는 이러한 데이터를 학습하여 점점 더 정확한 추천을 하게 됩니다.
                </p>
                <p>
                    이와 같은 추천 시스템을 통해 사용자는 자신만의 영화 리스트를 만들고 새로운 영화를 발견할 수 있는 즐거움을 경험할 수 있습니다.
                </p>
            </div>
        </div>
    );
};

export default StartPage;
