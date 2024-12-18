import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import '../styles/Edit.css';

const Edit = () => {
  const { memberId } = useParams();
  const [nickname, setNickname] = useState('');
  const [password, setPassword] = useState('');
  const [actorList, setActorList] = useState([]);
  const [newActor, setNewActor] = useState('');
  const [genreList, setGenreList] = useState([]);
  const [newGenre, setNewGenre] = useState('');
  const [loading, setLoading] = useState(true);

  const navigate = useNavigate();

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const token = localStorage.getItem('token');
        const response = await fetch(`/member/user/${memberId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (response.ok) {
          const data = await response.json();
          setNickname(data.nickname || '');
          setActorList(data.preferredActors || []);
          setGenreList(data.preferredGenres || []);
        } else {
          alert('사용자 데이터를 불러오는 데 실패했습니다.');
        }
      } catch (error) {
        console.error('데이터 로드 오류:', error);
        alert('사용자 데이터를 불러오는 중 오류가 발생했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, [memberId]);

  const handleNicknameChange = async () => {
    if (!nickname.trim()) {
      alert('닉네임을 입력하세요.');
      return;
    }
    try {
      const token = localStorage.getItem('token');
      await fetch(`/member/nickname/${memberId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ nickname }),
      });
      alert('닉네임이 성공적으로 수정되었습니다.');
    } catch (error) {
      console.error('닉네임 수정 오류:', error);
      alert('닉네임 수정에 실패했습니다.');
    }
  };

  const handlePasswordChange = async () => {
    if (!password.trim()) {
      alert('비밀번호를 입력하세요.');
      return;
    }
    try {
      const token = localStorage.getItem('token');
      await fetch(`/member/password/${memberId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ password }),
      });
      alert('비밀번호가 성공적으로 수정되었습니다.');
      setPassword('');
    } catch (error) {
      console.error('비밀번호 수정 오류:', error);
      alert('비밀번호 수정에 실패했습니다.');
    }
  };

  const handleAddActor = async () => {
    if (!newActor.trim()) {
      alert('배우 이름을 입력하세요.');
      return;
    }
  
    try {
      const token = localStorage.getItem('token'); // 인증 토큰 가져오기
      const response = await fetch(`/survey/${memberId}/actor`, {
        method: 'POST',
        headers: {
          'Content-Type': 'text/plain', // Content-Type을 text/plain으로 설정
          Authorization: `Bearer ${token}`, // 인증 헤더 추가
        },
        body: newActor.trim(), // 문자열 그대로 전송
      });
  
      if (response.ok) {
        // 성공적으로 추가되었으면 리스트 업데이트
        setActorList((prev) => [...prev, newActor.trim()]);
        setNewActor(''); // 입력 필드 초기화
        alert('선호 배우가 성공적으로 추가되었습니다.');
      } else {
        // 실패한 경우 응답 메시지 출력
        const errorMessage = await response.text();
        alert(`배우 추가 실패: ${errorMessage}`);
      }
    } catch (error) {
      console.error('배우 추가 오류:', error);
      alert('배우 추가 중 오류가 발생했습니다.');
    }
  };
  
  const handleRemoveActor = async (preferredActorId) => {
    try {
      const token = localStorage.getItem('token'); // 인증 토큰 가져오기
      const response = await fetch(`/survey/preferred-actors/${preferredActorId}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`, // 인증 헤더 추가
        },
      });
  
      if (response.ok) {
        // 삭제 성공 시 리스트 업데이트
        setActorList((prev) => prev.filter((item) => item.id !== preferredActorId));
        alert('선호 배우가 성공적으로 삭제되었습니다.');
      } else {
        // 삭제 실패 시 응답 메시지 출력
        const errorMessage = await response.text();
        alert(`배우 삭제 실패: ${errorMessage}`);
      }
    } catch (error) {
      console.error('배우 삭제 오류:', error);
      alert('배우 삭제 중 오류가 발생했습니다.');
    }
  };
  
  const handleAddGenre = async () => {
    if (!newGenre.trim()) {
      alert('장르 이름을 입력하세요.');
      return;
    }
  
    try {
      const token = localStorage.getItem('token'); // 인증 토큰 가져오기
      const response = await fetch(`/survey/${memberId}/genre`, {
        method: 'POST',
        headers: {
          'Content-Type': 'text/plain', // Content-Type을 text/plain으로 설정
          Authorization: `Bearer ${token}`, // 인증 헤더 추가
        },
        body: newGenre.trim(), // 문자열 그대로 전송
      });
  
      if (response.ok) {
        // 성공적으로 추가되었으면 리스트 업데이트
        setGenreList((prev) => [...prev, newGenre.trim()]);
        setNewGenre(''); // 입력 필드 초기화
        alert('선호 장르가 성공적으로 추가되었습니다.');
      } else {
        // 실패한 경우 응답 메시지 출력
        const errorMessage = await response.text();
        alert(`장르 추가 실패: ${errorMessage}`);
      }
    } catch (error) {
      console.error('장르 추가 오류:', error);
      alert('장르 추가 중 오류가 발생했습니다.');
    }
  };  

  const handleRemoveGenre = async (preferredGenreId) => {
    try {
      const token = localStorage.getItem('token'); // 인증 토큰 가져오기
      const response = await fetch(`/survey/preferred-genres/${preferredGenreId}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`, // 인증 헤더 추가
        },
      });
  
      if (response.ok) {
        // 삭제 성공 시 리스트 업데이트
        setGenreList((prev) => prev.filter((item) => item.id !== preferredGenreId));
        alert('선호 장르가 성공적으로 삭제되었습니다.');
      } else {
        // 삭제 실패 시 응답 메시지 출력
        const errorMessage = await response.text();
        alert(`장르 삭제 실패: ${errorMessage}`);
      }
    } catch (error) {
      console.error('장르 삭제 오류:', error); // 오류 디버깅
      alert('장르 삭제 중 오류가 발생했습니다.');
    }
  };
  

  if (loading) {
    return <div>로딩 중...</div>;
  }

  return (
    <div className="edit-page">
      <h2 className="edit-title">회원 정보 수정</h2>

      <div className="form-section">
        <label className="form-label">닉네임 수정</label>
        <div className="input-group">
          <input
            type="text"
            className="form-input"
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
            placeholder="닉네임을 입력하세요"
          />
          <button
            type="button"
            className="form-button"
            onClick={handleNicknameChange}
          >
            수정
          </button>
        </div>
      </div>

      <div className="form-section">
        <label className="form-label">비밀번호 수정</label>
        <div className="input-group">
          <input
            type="password"
            className="form-input"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="비밀번호를 입력하세요"
          />
          <button
            type="button"
            className="form-button"
            onClick={handlePasswordChange}
          >
            수정
          </button>
        </div>
      </div>

      <div className="form-section">
        <label className="form-label">좋아하는 배우 관리</label>
        <div className="input-group">
          <input
            type="text"
            className="form-input"
            value={newActor}
            onChange={(e) => setNewActor(e.target.value)}
            placeholder="배우 이름을 입력하세요"
          />
          <button
            type="button"
            className="form-button"
            onClick={handleAddActor}
          >
            추가
          </button>
        </div>
        <div className="actor-list">
          {actorList.length > 0 ? (
            actorList.map((actor, index) => (
              <div key={index} className="actor-item">
                {actor}
                <button
                  type="button"
                  className="delete-button"
                  onClick={() => handleRemoveActor(actor.id)}
                >
                  X
                </button>
              </div>
            ))
          ) : (
            <p>추가된 배우가 없습니다.</p>
          )}
        </div>
      </div>

      <div className="form-section">
        <label className="form-label">좋아하는 장르 관리</label>
        <div className="input-group">
          <input
            type="text"
            className="form-input"
            value={newGenre}
            onChange={(e) => setNewGenre(e.target.value)}
            placeholder="장르 이름을 입력하세요"
          />
          <button
            type="button"
            className="form-button"
            onClick={handleAddGenre}
          >
            추가
          </button>
        </div>
        <div className="genre-list">
          {genreList.length > 0 ? (
            genreList.map((genre, index) => (
              <div key={index} className="genre-item">
                <span>{genre}</span>
                <button
                  type="button"
                  className="delete-button"
                  onClick={() => handleRemoveGenre(genre)}
                >
                  X
                </button>
              </div>
            ))
          ) : (
            <p>추가된 장르가 없습니다.</p>
          )}
        </div>
      </div>


      <button type="button" className="back-button" onClick={() => navigate(-1)}>
        돌아가기
      </button>
    </div>
  );
};

export default Edit;
