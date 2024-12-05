import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/MemberInfo.css';

const MemberInfo = ({ member, survey, preferredGenres, preferredActors })=> {

  const navigate = useNavigate();
  if (!member || !survey) {
    return <p>로딩 중...</p>;
  }

  return (
    <div className="member-info">
      <div className="member-card">
        <h3>{member.nickname} 님의 정보</h3>
        <p><strong>ID:</strong> {member.membername}</p>
        <p><strong>성별:</strong> {survey.gender || '미입력'} <strong>  나이:</strong> {survey.age || '미입력'}</p>
        <p><strong>선호 장르:</strong>
        <ul className="genre-list">
          {preferredGenres && preferredGenres.length > 0 ? (
            preferredGenres.map((genre, index) => <li key={index}>{genre}</li>)
          ) : (
            <li>선호 장르가 없습니다.</li>
          )}
        </ul>
        </p>
        <p><strong>선호 배우:</strong>
        <ul className="actor-list">
          {preferredActors && preferredActors.length > 0 ? (
            preferredActors.map((actor, index) => <li key={index}>{actor}</li>)
          ) : (
            <li>선호 배우가 없습니다.</li>
          )}
        </ul>
        </p>
        <button
          onClick={() => navigate(`/edit/${member.memberId}`)}
          className="edit-button"
        >
          수정
        </button>
      </div>
    </div>
  );
};

export default MemberInfo;
