import React from 'react';
import '../styles/RecommendationCard.css'; // 스타일 파일 분리

const RecommendationCard = ({ title, description, imageUrl }) => {
  return (
    <div className="recommendation-card">
      <img src={imageUrl} alt={title} className="recommendation-image" />
      <h3 className="recommendation-title">{title}</h3>
      <p className="recommendation-description">{description}</p>
    </div>
  );
};

export default RecommendationCard;
