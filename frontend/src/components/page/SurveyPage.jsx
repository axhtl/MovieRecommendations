import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import '../styles/SurveyPage.css';

const genresList = [
  '드라마', '애니메이션', '기타', '액션', '어드벤쳐', '미스터리', '가족',
  '코미디', '뮤지컬', '범죄', '공연', '공포(호러)', '다큐멘터리',
  '판타지', '성인물(에로)', 'SF', '스릴러', '전쟁', '멜로/로맨스'
];

const SurveyPage = () => {
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const userId = searchParams.get('userId');

  const navigate = useNavigate();
  const [actors, setActors] = useState('');
  const [actorList, setActorList] = useState([]);
  const [selectedGenres, setSelectedGenres] = useState([]);
  const [gender, setGender] = useState('');
  const [age, setAge] = useState('');

  const addActor = () => {
    if (actors.trim() !== '') {
      setActorList([...actorList, actors]);
      setActors('');
    }
  };

  const removeActor = (index) => {
    const newActorList = actorList.filter((_, i) => i !== index);
    setActorList(newActorList);
  };

  const handleGenreChange = (genre) => {
    if (selectedGenres.includes(genre)) {
      setSelectedGenres(selectedGenres.filter((item) => item !== genre));
    } else {
      setSelectedGenres([...selectedGenres, genre]);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const surveyData = {
      gender: gender,
      age: age,
      preferredGenres: selectedGenres,
      preferredActors: actorList,
    };

    try {
      const response = await fetch(`/survey/${userId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(surveyData),
      });

      if (response.ok) {
        alert('설문 제출 성공!');
        navigate('/'); // 설문 제출 후 시작 페이지로 이동
      } else {
        alert('설문 제출에 실패했습니다. 다시 시도해 주세요.');
      }
    } catch (error) {
      alert('오류가 발생했습니다. 잠시 후 다시 시도해 주세요.');
    }
  };

  return (
    <div className="survey-page">
      <h2>우선! 당신에 대해 알려주세요.</h2>
      <p>아래 설문조사를 작성해 주세요.</p>
      <form className="survey-form" onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="age">나이</label>
          <input
            type="number"
            id="age"
            name="age"
            min="10"
            max="100"
            value={age}
            onChange={(e) => setAge(e.target.value)}
          />
        </div>
        <div className="form-group">
          <label htmlFor="gender">성별</label>
          <select
            id="gender"
            name="gender"
            value={gender}
            onChange={(e) => setGender(e.target.value)}
          >
            <option value="">선택하세요</option>
            <option value="M">남성</option>
            <option value="F">여성</option>
          </select>
        </div>
        <div className="form-group">
          <label>좋아하는 배우</label>
          <div className="actor-input-group">
            <input
              type="text"
              value={actors}
              onChange={(e) => setActors(e.target.value)}
              placeholder="배우 이름을 입력하세요"
            />
            <button type="button" onClick={addActor}>추가</button>
          </div>
          <div className="actor-list">
            {actorList.map((actor, index) => (
              <div key={index} className="actor-item">
                {actor}
                <button type="button" onClick={() => removeActor(index)}>X</button>
              </div>
            ))}
          </div>
        </div>
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
        <button type="submit">제출</button>
      </form>
    </div>
  );
};

export default SurveyPage;
