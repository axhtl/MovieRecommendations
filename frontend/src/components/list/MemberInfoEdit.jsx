import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/Edit.css';

const genresList = [
  '드라마', '애니메이션', '액션', '어드벤쳐', '미스터리', '가족',
  '코미디', '뮤지컬', '범죄', '공연', '공포(호러)', '다큐멘터리',
  '판타지', '성인물(에로)', 'SF', '스릴러', '전쟁', '멜로/로맨스', '기타',
];

const Edit = () => {
  const [nickname, setNickname] = useState('');
  const [password, setPassword] = useState('');
  const [actorList, setActorList] = useState([]);
  const [selectedGenres, setSelectedGenres] = useState([]);
  const [newActor, setNewActor] = useState('');
  const [loading, setLoading] = useState(true);

  const navigate = useNavigate(); // navigate 추가

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const token = localStorage.getItem('token');
        const response = await fetch('/member/user/1', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (response.ok) {
          const data = await response.json();
          setNickname(data.nickname || '');
          setActorList(data.preferredActors || []);
          setSelectedGenres(data.preferredGenres || []);
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
  }, []);

  // 닉네임 수정
  const handleNicknameChange = async () => {
    if (!nickname.trim()) {
      alert('닉네임을 입력하세요.');
      return;
    }
    try {
      const token = localStorage.getItem('token');
      await fetch(`/member/nickname`, {
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
      await fetch(`/member/password`, {
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
  const addActor = () => {
    if (!newActor.trim()) {
      alert('배우 이름을 입력하세요.');
      return;
    }
    setActorList([...actorList, newActor]);
    setNewActor('');
  };

  // 배우 삭제
  const removeActor = (index) => {
    setActorList(actorList.filter((_, i) => i !== index));
  };

  // 장르 선택/해제
  const handleGenreChange = (genre) => {
    if (selectedGenres.includes(genre)) {
      setSelectedGenres(selectedGenres.filter((item) => item !== genre));
    } else {
      setSelectedGenres([...selectedGenres, genre]);
    }
  };

  // 설문 데이터 제출
  const handleSubmit = async (e) => {
    e.preventDefault();

    const surveyData = {
      preferredGenres: selectedGenres,
      preferredActors: actorList,
    };

    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`/survey/edit`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(surveyData),
      });

      if (response.ok) {
        alert('설문 수정이 완료되었습니다.');
      } else {
        alert('설문 수정에 실패했습니다. 다시 시도해주세요.');
      }
    } catch (error) {
      console.error('설문 수정 중 오류 발생:', error);
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

      <form className="edit-form" onSubmit={handleSubmit}>
        {/* 선호 배우 관리 */}
        <div className="form-group">
          <label>좋아하는 배우</label>
          <div className="actor-input-group">
            <input
              type="text"
              value={newActor}
              onChange={(e) => setNewActor(e.target.value)}
              placeholder="배우 이름을 입력하세요"
            />
            <button type="button" onClick={addActor}>추가</button>
          </div>
          <div className="actor-list">
            {actorList.map((actor, index) => (
              <div key={index} className="actor-item">
                {actor}
                <button type="button" onClick={() => removeActor(index)}>삭제</button>
              </div>
            ))}
          </div>
        </div>

        {/* 선호 장르 관리 */}
        <div className="form-group">
          <label>좋아하는 영화 장르</label>
          <div className="checkbox-group">
            {genresList.map((genre, index) => (
              <div key={index} className="checkbox-item">
                <input
                  type="checkbox"
                  id={`genre-${index}`}
                  value={genre}
                  checked={selectedGenres.includes(genre)}
                  onChange={() => handleGenreChange(genre)}
                />
                <label htmlFor={`genre-${index}`}>{genre}</label>
              </div>
            ))}
          </div>
        </div>

        <button type="submit" className="submit-button">수정하기</button>
      </form>

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
