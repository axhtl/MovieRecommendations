import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
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
import RegiMovieDel from './components/list/RegiMovieDel';
import AdminNavbar from './components/ui/AdminNavbar'; // 관리자 전용 Navbar 추가
import AdminDashboard from './components/admin/AdminDashboard'; // 관리자 페이지 예시
import AdminSettings from './components/admin/AdminSettings'; // 관리자 페이지 예시

function App() {
  // 로그인한 사용자 정보 가져오기
  const member = JSON.parse(localStorage.getItem('member'));
  const isAdmin = member && member.role === 'ADMIN'; // role이 'ADMIN'인지 확인

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
        <Route path="/api/ai/predict/:userId" element={<RecommendPage />} />
        <Route path="/movie/search" element={<SearchPage />} />
        <Route path="/api/movies/detail/:movieId" element={<MovieDetails />} />
        <Route path="/movie/view" element={<ViewPage />} />
        <Route path="/edit/:memberId" element={<MemberInfoEdit />} />
        <Route path="/movies/:movieId" element={<RegiMovieDel />} />
        
        {/* 네비게이션이 포함된 라우터 */}
        <Route
          path="/"
          element={
            <>
              {isAdmin ? <AdminNavbar /> : <Navbar />} {/* 관리자와 일반 사용자 네비게이션 분리 */}
              <Routes>
                <Route path="/main/:userId" element={<MainPage />} />
                <Route path="/my/:userId" element={<MyPage />} />
                <Route path="/api/ai/predict/:userId" element={<RecommendPage />} />
                <Route path="/movie/search" element={<SearchPage />} />
                <Route path="/api/movies/detail/:movieId" element={<MovieDetails />} />
                <Route path="/movie/view" element={<ViewPage />} />
                <Route path="/edit/:memberId" element={<MemberInfoEdit />} />
                <Route path="/movies/:movieId" element={<RegiMovieDel />} />
              </Routes>
            </>
          }
        />
        
        {/* 관리자 전용 라우트 */}
        {isAdmin && (
          <Route path="/admin" element={<AdminNavbar />}>
            <Route path="dashboard" element={<AdminDashboard />} />
            <Route path="settings" element={<AdminSettings />} />
          </Route>
        )}

        {/* 관리자가 아닐 경우 /admin 경로 접근 차단 */}
        {!isAdmin && (
          <Route path="/admin/*" element={<Navigate to="/" replace />} />
        )}
      </Routes>
    </Router>
  );
}

export default App;
