import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/MemberInfo.css';

const MemberInfo = ({ data }) => {
  const navigate = useNavigate();

  if (!data) {
    return <p>Loading...</p>;
  }

  return (
    <div className="member-info">
      <div className="member-card">
        <h3>{data.nickname} 님의 정보</h3>
        <p>ID: {data.membername}</p>
        <p>성별: {data.gender || '미입력'} 나이: {data.age || '미입력'}</p>
        <p>선호 장르: 
        <ul className="genre-list">
          {(data.preferredGenres || []).map((genre, index) => (
            <li key={index}>{genre}</li>
          ))}
        </ul>
        </p>
        <p>선호 배우:
        <ul className="actor-list">
          {(data.preferredActors || []).map((actor, index) => (
            <li key={index}>{actor}</li>
          ))}
        </ul>
        </p>
        <button
          onClick={() => navigate(`/edit/${data.memberId}`)}
          className="edit-button"
        >
          수정
        </button>
      </div>
    </div>
  );
};

export default MemberInfo;
