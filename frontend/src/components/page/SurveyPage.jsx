import React from 'react';
import '../styles/SurveyPage.css';

const SurveyPage = () => {
  return (
    <div className="survey-page">
      <h2>설문조사</h2>
      <p>아래 설문조사를 작성해 주세요.</p>
      <form className="survey-form">
        <div className="form-group">
          <label htmlFor="age">나이:</label>
          <input type="number" id="age" name="age" min="10" max="100" />
        </div>
        <div className="form-group">
          <label htmlFor="gender">성별:</label>
          <select id="gender" name="gender">
            <option value="">선택하세요</option>
            <option value="male">남성</option>
            <option value="female">여성</option>
            <option value="other">기타</option>
          </select>
        </div>
        <div className="form-group">
          <label htmlFor="favorite-genre">좋아하는 영화 장르:</label>
          <input type="text" id="favorite-genre" name="favorite-genre" />
        </div>
        <button type="submit">제출</button>
      </form>
    </div>
  );
};

export default SurveyPage;
