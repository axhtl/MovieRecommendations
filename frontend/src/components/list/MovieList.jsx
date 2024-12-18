import React from 'react';
import MovieItem from './MovieItem';
import '../styles/MovieList.css';

const MovieList = ({ movies, onMovieClick }) => {
  return (
    <div className="movie-list">
      {movies.map((movie) => (
        <MovieItem key={movie.id || movie.movieCd} movie={movie} onClick={onMovieClick} />
      ))}
    </div>
  );
};

export default MovieList;
