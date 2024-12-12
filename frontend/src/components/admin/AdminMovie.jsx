import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AdminNavbar from '../ui/AdminNavbar';
import axios from 'axios';
import '../styles/admin/AdminUsers.css'; // 기존 스타일 재사용

function AdminMovie() {
  const [movies, setMovies] = useState([]); // 영화 데이터를 저장할 상태
  const navigate = useNavigate();

  useEffect(() => {
    const memberId = localStorage.getItem('memberId');
    const token = localStorage.getItem('token');

    // 관리자 권한 확인
    if (!memberId || memberId !== '14' || !token) {
      alert('접근 권한이 없습니다.');
      navigate('/');
      return; // navigate 이후 불필요한 코드 실행 방지
    }

    const fetchMovieData = async () => {
      try {
        const userResponse = await axios.get('/member/users', {
          headers: { Authorization: `Bearer ${token}` },
        });

        const allMovies = [];

        // 각 사용자별로 영화 데이터 가져오기
        for (const user of userResponse.data) {
          const memberId = user.member.memberId;
          const reviewInfos = user.reviewInfos || [];

          // 영화 리뷰 정보가 있는 경우만 처리
          if (reviewInfos.length > 0) {
            const movieTitlesResponse = await axios.get(`/member/${memberId}/movie-titles`, {
              headers: { Authorization: `Bearer ${token}` },
            });

            reviewInfos.forEach((reviewInfo) => {
              const movieTitle = movieTitlesResponse.data.find(
                (title) => title.movieId === reviewInfo.movieInfo.movieId
              );

              allMovies.push({
                reviewId: reviewInfo.reviewId,
                memberId: user.member.memberId,
                memberName: user.member.membername,
                movieTitle: movieTitle ? movieTitle.title : 'Unknown',
                reviewContent: reviewInfo.movieInfo.title,
              });
            });
          }
        }

        setMovies(allMovies);
      } catch (error) {
        console.error('영화 데이터를 가져오는 중 오류 발생:', error);
        alert('영화 데이터를 가져올 수 없습니다.');
      }
    };

    fetchMovieData();
  }, [navigate]);

  // 리뷰 삭제 함수
  const handleDelete = async (reviewId) => {
    const token = localStorage.getItem('token');
    try {
      await axios.delete(`/review/${reviewId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      alert(`리뷰 ID ${reviewId} 삭제 완료`);
      setMovies((prevMovies) => prevMovies.filter((movie) => movie.reviewId !== reviewId));
    } catch (error) {
      console.error('리뷰 삭제 중 오류 발생:', error);
      alert('리뷰를 삭제할 수 없습니다.');
    }
  };

  return (
    <div className="admin-page">
      <AdminNavbar />
      <div className="admin-content">
        <h1>영화 기록 조회</h1>
        <table>
          <thead>
            <tr>
              <th>Member ID</th>
              <th>Member Name</th>
              <th>Movie Name</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {movies.map((movie) => (
              <tr key={movie.reviewId}>
                <td>{movie.memberId}</td>
                <td>{movie.memberName}</td>
                <td>{movie.reviewContent}</td>
                <td>
                  <button
                    className="action-button"
                    onClick={() => handleDelete(movie.reviewId)}
                  >
                    삭제
                  </button>
                </td> {/* <td> 닫는 태그 추가 */}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default AdminMovie;
