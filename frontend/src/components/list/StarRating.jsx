import React, { useState } from 'react';
import '../styles/StarRating.css';

const StarRating = ({ onRatingChange }) => {
  const [rating, setRating] = useState(0);
  const [hover, setHover] = useState(0);

  const handleRating = (value) => {
    setRating(value);
    if (onRatingChange) {
      onRatingChange(value);
    }
  };

  return (
    <div className="star-rating">
      {[...Array(5)].map((_, index) => {
        const value = index + 1;
        const isFilled = value <= (hover || rating);
        const isHalf = value === Math.ceil(hover || rating) && (hover || rating) % 1 !== 0;

        return (
          <span
            key={index}
            className={`star ${isFilled ? 'filled' : ''} ${isHalf ? 'half' : ''}`}
            onMouseEnter={() => setHover(index + 0.5)}
            onMouseLeave={() => setHover(0)}
            onClick={() => handleRating(index + 1)}
            onContextMenu={(e) => {
              e.preventDefault();
              handleRating(index + 0.5);
            }}
          >
            {isHalf ? (
              <svg width="24" height="24" viewBox="0 0 24 24">
                <defs>
                  <linearGradient id={`half-grad-${index}`}>
                    <stop offset="50%" stopColor="#ffc107" />
                    <stop offset="50%" stopColor="#ccc" />
                  </linearGradient>
                </defs>
                <path
                  fill={`url(#half-grad-${index})`}
                  d="M12 .587l3.668 7.431 8.209 1.193-5.938 5.787 1.405 8.188L12 18.902l-7.344 3.864 1.405-8.188L.123 9.211l8.209-1.193z"
                />
              </svg>
            ) : (
              '★'
            )}
          </span>
        );
      })}
    </div>
  );
};

export default StarRating;
