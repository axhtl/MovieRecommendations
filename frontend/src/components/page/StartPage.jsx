import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/StartPage.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faFilm } from '@fortawesome/free-solid-svg-icons';


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
                <FontAwesomeIcon icon={faFilm} size="2x" alt="Movie Icon" />
            </div>
                <h1>MOVIE PICK</h1>
                <button className="login-button" onClick={() => navigate('/member/login')}>로그인</button>
            </div>
        </div>
    );
};

export default StartPage;
