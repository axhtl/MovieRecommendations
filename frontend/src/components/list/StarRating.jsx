import React, { useState, useEffect } from 'react';
import '../styles/StarRating.css';

const StarRating = ({ onRatingChange, rating: initialRating = 0, readOnly = false }) => {
  const [rating, setRating] = useState(Number(initialRating)); // 숫자로 변환하여 설정
  const [hover, setHover] = useState(null);

  useEffect(() => {
    console.log('Initial Rating:', initialRating); // 전달된 초기 rating 확인
    setRating(Number(initialRating)); // 초기 별점 숫자로 변환
  }, [initialRating]);

  const handleRating = (value) => {
    if (readOnly) return; // 읽기 전용이면 클릭 불가
    setRating(value); // rating 업데이트
    if (onRatingChange) {
      onRatingChange(value); // 부모 컴포넌트에 값 전달
    }
  };

  return (
    <div className="star-rating">
      {[...Array(5)].map((_, index) => {
        const value = index + 1;
        const isFilled = value <= (hover || rating); // hover 상태 또는 rating 상태 확인

        return (
          <span
            key={index}
            className={`star ${isFilled ? 'filled' : ''}`} // 클래스 조건에 따라 추가
            onMouseEnter={() => !readOnly && setHover(value)} // hover 상태 업데이트
            onMouseLeave={() => !readOnly && setHover(null)} // hover 상태 초기화
            onClick={() => handleRating(value)} // 클릭 시 별점 설정
            style={{ cursor: readOnly ? 'default' : 'pointer' }} // 읽기 전용이면 포인터 비활성화
          >
            ★
          </span>
        );
      })}
    </div>
  );
};

export default StarRating;
