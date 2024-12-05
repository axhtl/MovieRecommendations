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
import MovieDetails from './components/list/MovieDetails';
import ViewPage from './components/page/ViewPage';
import MemberInfoEdit from './components/list/MemberInfoEdit';

function App() {
  return (
    <Router>
      <Routes>
        {/* StartPage를 표시할 때 Navbar를 숨기기 위해 분기 처리 */}
        <Route path="/" element={<StartPage />} />
        {/* 회원 관련 경로 */}
        <Route path="/member/signup" element={<RegisterPage />} />
        <Route path="/member/login" element={<LoginPage />} />
        <Route path="/my/:userId" element={<MyPage />} />
        {/* 설문조사 */}
        <Route path="/survey" element={<SurveyPage />} />
        {/* 주요 페이지 */}
        <Route path="/main/:userId" element={<MainPage />} />
        {/* 추천 및 검색 */}
        <Route path="/recommend" element={<RecommendPage />} />
        <Route path="/movie/search" element={<SearchPage />} />
        <Route path="/api/movies/detail/:movieId" element={<MovieDetails />} />
        <Route path="/movie/view" element={<ViewPage />} />
        <Route path="/edit/:memberId" element={<MemberInfoEdit />} />
        {/* 네비게이션이 포함된 라우터 */}
        <Route
          path="/"
          element={
            <>
              <Navbar />
              <Routes>
                <Route path="/main/:userId" element={<MainPage />} />
                <Route path="/my/:userId" element={<MyPage />} />
                <Route path="/recommend" element={<RecommendPage />} />
                <Route path="/movie/search" element={<SearchPage />} />
                <Route path="/api/movies/detail/:movieId" element={<MovieDetails />} />
                <Route path="/movie/view" element={<ViewPage />} />
                <Route path="/edit/:memberId" element={<MemberInfoEdit />} />
              </Routes>
            </>
          }
        />
      </Routes>
    </Router>
  );
}

export default App;
