import React, { useState, useEffect } from 'react';
import '../styles/StarRating.css';

const StarRating = ({ onRatingChange, rating: initialRating = 0, readOnly = false }) => {
  const [rating, setRating] = useState(initialRating);
  const [hover, setHover] = useState(null); // hover 상태를 null로 초기화

  useEffect(() => {
    setRating(initialRating); // 초기 별점 설정
  }, [initialRating]);

  const handleRating = (value) => {
    if (readOnly) return; // 읽기 전용일 경우 클릭 무시
    setRating(value); // 별점 상태 업데이트
    if (onRatingChange) {
      onRatingChange(value); // 부모 컴포넌트에 전달
    }
  };

  return (
    <div className="star-rating">
      {[...Array(5)].map((_, index) => {
        const value = index + 1; // 별점 값 (1~5)
        const isFilled = value <= (hover || rating); // hover 상태 또는 rating 상태 확인

        return (
          <span
            key={index}
            className={`star ${isFilled ? 'filled' : ''}`}
            onMouseEnter={() => !readOnly && setHover(value)} // hover 상태 업데이트
            onMouseLeave={() => !readOnly && setHover(null)} // hover 상태 초기화
            onClick={() => handleRating(value)} // 별점 선택
            style={{ cursor: readOnly ? 'default' : 'pointer' }} // 읽기 전용일 경우 커서 변경
          >
            ★
          </span>
        );
      })}
    </div>
  );
};

export default StarRating;
