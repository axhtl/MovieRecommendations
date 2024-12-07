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
import AdminNavbar from './components/ui/AdminNavbar';
import AdminDashboard from './components/admin/AdminDashboard';
import AdminSettings from './components/admin/AdminSettings';

function App() {
  // 로그인한 사용자 정보 가져오기
  const role = localStorage.getItem('role'); // 로컬 스토리지에서 role 가져오기

  return (
    <Router>
      <Routes>
        {/* StartPage 및 회원 관련 경로 */}
        <Route path="/" element={<StartPage />} />
        <Route path="/member/signup" element={<RegisterPage />} />
        <Route path="/member/login" element={<LoginPage />} />
        <Route path="/survey" element={<SurveyPage />} />

        {/* 관리자 전용 라우트 */}
        {role === 'ADMIN' && (
          <>
            <Route path="/admin" element={<AdminNavbar />}>
              <Route path="dashboard" element={<AdminDashboard />} />
              <Route path="settings" element={<AdminSettings />} />
            </Route>
            <Route path="*" element={<Navigate to="/admin/dashboard" replace />} />
          </>
        )}

        {/* 사용자 전용 라우트 */}
        {role === 'USER' && (
          <>
            <Route path="/main/:userId" element={<MainPage />} />
            <Route path="/my/:userId" element={<MyPage />} />
            <Route path="/recommend" element={<RecommendPage />} />
            <Route path="/movie/search" element={<SearchPage />} />
            <Route path="/api/movies/detail/:movieId" element={<MovieDetails />} />
            <Route path="/movie/view" element={<ViewPage />} />
            <Route path="/edit/:memberId" element={<MemberInfoEdit />} />
            <Route path="/movies/:movieId" element={<RegiMovieDel />} />

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
                    <Route path="/movies/:movieId" element={<RegiMovieDel />} />
                  </Routes>
                </>
              }
            />
            <Route path="*" element={<Navigate to="/main/1" replace />} />
          </>
        )}

        {/* 권한 없는 사용자 */}
        {(!role || (role !== 'ADMIN' && role !== 'USER')) && (
          <Route path="*" element={<Navigate to="/member/login" replace />} />
        )}
      </Routes>
    </Router>
  );
}

export default App;
