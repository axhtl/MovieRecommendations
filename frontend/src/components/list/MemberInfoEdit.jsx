import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import '../styles/Edit.css';

const Edit = () => {
  const { memberId } = useParams(); // URL에서 memberId 가져오기
  const [nickname, setNickname] = useState('');
  const [password, setPassword] = useState('');
  const [actorList, setActorList] = useState([]); // 배우 리스트는 문자열 배열
  const [newActor, setNewActor] = useState('');
  const [genreList, setGenreList] = useState([]); // 장르 리스트는 문자열 배열
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
          setActorList(data.preferredActors || []); // 서버에서 actorList 받아옴
          setGenreList(data.preferredGenres || []); // 서버에서 genreList 받아옴
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

  // 닉네임 수정
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

  // 비밀번호 수정
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

  // 배우 추가
  const handleAddActor = async () => {
    if (!newActor.trim()) {
      alert('배우 이름을 입력하세요.');
      return;
    }

    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`/survey/${memberId}/actor`, {
        method: 'POST',
        headers: {
          'Content-Type': 'text/plain',
          Authorization: `Bearer ${token}`,
        },
        body: newActor,
      });

      if (response.ok) {
        setActorList([...actorList, newActor]); // 로컬 상태에 추가
        setNewActor(''); // 입력 필드 초기화
        alert('배우가 성공적으로 추가되었습니다.');
      } else {
        alert('배우 추가에 실패했습니다. 다시 시도해주세요.');
      }
    } catch (error) {
      console.error('배우 추가 중 오류 발생:', error);
      alert('오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
    }
  };

  // 배우 삭제
  const handleRemoveActor = async (actor) => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`/survey/preferred-actors/${actor}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        setActorList(actorList.filter((item) => item !== actor)); // 로컬 상태에서 해당 배우 삭제
        alert('배우가 성공적으로 삭제되었습니다.');
      } else {
        alert('배우 삭제에 실패했습니다. 다시 시도해주세요.');
      }
    } catch (error) {
      console.error('배우 삭제 중 오류 발생:', error);
      alert('오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
    }
  };

  // 장르 추가
  const handleAddGenre = async () => {
    if (!newGenre.trim()) {
      alert('장르 이름을 입력하세요.');
      return;
    }

    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`/survey/${memberId}/genre`, {
        method: 'POST',
        headers: {
          'Content-Type': 'text/plain',
          Authorization: `Bearer ${token}`,
        },
        body: newGenre,
      });

      if (response.ok) {
        setGenreList([...genreList, newGenre]); // 로컬 상태에 추가
        setNewGenre(''); // 입력 필드 초기화
        alert('장르가 성공적으로 추가되었습니다.');
      } else {
        alert('장르 추가에 실패했습니다. 다시 시도해주세요.');
      }
    } catch (error) {
      console.error('장르 추가 중 오류 발생:', error);
      alert('오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
    }
  };

  // 장르 삭제
  const handleRemoveGenre = async (genre) => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`/survey/preferred-genres/${genre}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        setGenreList(genreList.filter((item) => item !== genre)); // 로컬 상태에서 해당 장르 삭제
        alert('장르가 성공적으로 삭제되었습니다.');
      } else {
        alert('장르 삭제에 실패했습니다. 다시 시도해주세요.');
      }
    } catch (error) {
      console.error('장르 삭제 중 오류 발생:', error);
      alert('오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
    }
  };

  if (loading) {
    return <div>로딩 중...</div>;
  }

  return (
    <div className="edit-page">
      <h2>회원 정보 수정</h2>

      {/* 닉네임 수정 */}
      <div className="form-group">
        <label>닉네임</label>
        <input
          type="text"
          value={nickname}
          onChange={(e) => setNickname(e.target.value)}
          placeholder="닉네임을 입력하세요"
        />
        <button type="button" onClick={handleNicknameChange} className="save-button">수정</button>
      </div>

      {/* 비밀번호 수정 */}
      <div className="form-group">
        <label>비밀번호</label>
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="비밀번호를 입력하세요"
        />
        <button type="button" onClick={handlePasswordChange} className="save-button">수정</button>
      </div>

      {/* 배우 관리 */}
      <div className="form-group">
        <label>좋아하는 배우 관리</label>
        <div className="actor-input-group">
          <input
            type="text"
            value={newActor}
            onChange={(e) => setNewActor(e.target.value)}
            placeholder="배우 이름을 입력하세요"
          />
          <button type="button" onClick={handleAddActor}>추가</button>
        </div>
        <div className="actor-list">
          {actorList.map((actor, index) => (
            <div key={index} className="actor-item">
              {actor}
              <button type="button" onClick={() => handleRemoveActor(actor)}>삭제</button>
            </div>
          ))}
        </div>
      </div>

      {/* 장르 관리 */}
      <div className="form-group">
        <label>좋아하는 장르 관리</label>
        <div className="genre-input-group">
          <input
            type="text"
            value={newGenre}
            onChange={(e) => setNewGenre(e.target.value)}
            placeholder="장르 이름을 입력하세요"
          />
          <button type="button" onClick={handleAddGenre}>추가</button>
        </div>
        <div className="genre-list">
          {genreList.map((genre, index) => (
            <div key={index} className="genre-item">
              {genre}
              <button type="button" onClick={() => handleRemoveGenre(genre)}>삭제</button>
            </div>
          ))}
        </div>
      </div>

      {/* 돌아가기 버튼 */}
      <button
        type="button"
        className="save-button"
        onClick={() => navigate(-1)} // 이전 페이지로 이동
      >
        돌아가기
      </button>
    </div>
  );
};

export default Edit;
