import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import StartPage from './components/page/StartPage';
import Navbar from './components/ui/Navbar';
import RegisterPage from './components/page/RegisterPage';
import LoginPage from './components/page/LoginPage';
import MainPage from './components/page/MainPage';
import MyPage from './components/page/MyPage';
import SurveyPage from './components/page/SurveyPage';
import SearchPage from './components/page/SearchPage';
import RecommendPage from './components/page/RecommendPage';

function App() {
  return (
    <Router>
      <Routes>
        {/* StartPage를 표시할 때 Navbar를 숨기기 위해 분기 처리 */}
        <Route path="/" element={<StartPage />} />
        {/* 회원 관련 경로 */}
        <Route path="/member/signup" element={<RegisterPage />} />
        <Route path="/member/login" element={<LoginPage />} />
        <Route path="/member/my" element={<MyPage />} />
        {/* 설문조사 */}
        <Route path="/survey" element={<SurveyPage />} />
        {/* 주요 페이지 */}
        <Route path="/main" element={<MainPage />} />
        {/* 추천 및 검색 */}
        <Route path="/recommend" element={<RecommendPage />} />
        <Route path="/search" element={<SearchPage />} />
        {/* 네비게이션이 포함된 라우터 */}
        <Route
          path="/"
          element={
            <>
              <Navbar />
              <Routes>
                <Route path="/main" element={<MainPage />} />
                <Route path="/member/my" element={<MyPage />} />
                <Route path="/recommend" element={<RecommendPage />} />
                <Route path="/search" element={<SearchPage />} />
              </Routes>
            </>
          }
        />
      </Routes>
    </Router>
  );
}

export default App;
