drop table if exists movieInfo;
CREATE TABLE movieInfo (
   movie_info_id bigint NOT NULL AUTO_INCREMENT,
   movieCd varchar(255) NOT NULL,
   movieNm varchar(255) NOT NULL,
   movieEn varchar(255) NOT NULL,
   showTm varchar(255) NOT NULL,
   openDt varchar(255) NOT NULL,
   typeNm varchar(255) NOT NULL,
   nations varchar(255) NOT NULL,
   genres json NOT NULL,
   directors json NOT NULL,
   actors json NOT NULL,
   PRIMARY KEY (movie_info_id)
);

drop table if exists member;
CREATE TABLE member (
   member_id bigint NOT NULL AUTO_INCREMENT,
   password varchar(255) NOT NULL,
   nickname varchar(255) NOT NULL,
   role varchar(255) NOT NULL DEFAULT 'USER' COMMENT 'USER, ADMIN',
   member_status varchar(255) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, WITHDRAWN, SUSPENDED',
   created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
   deleted_at timestamp NULL,
   refresh_token varchar(255) NULL,
   PRIMARY KEY (member_id)
);

drop table if exists survey;
CREATE TABLE survey (
   survey_id bigint NOT NULL AUTO_INCREMENT,
   member_id bigint NOT NULL,
   gender varchar(255) NOT NULL COMMENT 'M, F',
   age int NOT NULL,
   PRIMARY KEY (survey_id),
   FOREIGN KEY (member_id) REFERENCES member(member_id)
);

drop table if exists preferredGenre;
CREATE TABLE preferredGenre (
   preferred_genre_id bigint NOT NULL AUTO_INCREMENT,
   member_id bigint NOT NULL,
   preferred_genre varchar(255) NOT NULL,
   PRIMARY KEY (preferred_genre_id),
   FOREIGN KEY (member_id) REFERENCES member(member_id)
);

drop table if exists preferredActor;
CREATE TABLE preferredActor (
   preferred_actor_id bigint NOT NULL AUTO_INCREMENT,
   member_id bigint NOT NULL,
   preferred_actor varchar(255) NOT NULL,
   PRIMARY KEY (preferred_actor_id),
   FOREIGN KEY (member_id) REFERENCES member(member_id)
);

drop table if exists review;
CREATE TABLE review (
   review_id bigint NOT NULL AUTO_INCREMENT,
   member_id bigint NOT NULL,
   movie_info_id bigint NOT NULL,
   content varchar(1000) NOT NULL,
   ranked varchar(255) NOT NULL,
   created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (review_id),
   FOREIGN KEY (member_id) REFERENCES member(member_id),
   FOREIGN KEY (movie_info_id) REFERENCES movieInfo(movie_info_id)
);

drop table if exists movieGenre;
CREATE TABLE movieGenre (
   genre_id bigint NOT NULL AUTO_INCREMENT,
   movie_info_id bigint NOT NULL,
   movieCd varchar(255) NOT NULL,
   genre varchar(255) NOT NULL,
   PRIMARY KEY (genre_id),
   FOREIGN KEY (movie_info_id) REFERENCES movieInfo(movie_info_id)
);

drop table if exists movieDirector;
CREATE TABLE movieDirector (
   director_id bigint NOT NULL AUTO_INCREMENT,
   movie_info_id bigint NOT NULL,
   movieCd varchar(255) NOT NULL,
   movie_director varchar(255) NOT NULL,
   PRIMARY KEY (director_id),
   FOREIGN KEY (movie_info_id) REFERENCES movieInfo(movie_info_id)
);

drop table if exists movieActor;
CREATE TABLE movieActor (
   actor_id bigint NOT NULL AUTO_INCREMENT,
   movie_info_id bigint NOT NULL,
   movieCd varchar(255) NOT NULL,
   movie_actor varchar(255) NOT NULL,
   PRIMARY KEY (actor_id),
   FOREIGN KEY (movie_info_id) REFERENCES movieInfo(movie_info_id)
);