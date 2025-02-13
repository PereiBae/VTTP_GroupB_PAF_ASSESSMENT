CREATE DATABASE movies;

use movies;

CREATE TABLE imdb(
                     imdb varchar(16) not null,
                     vote_average float default 0, -- vote average
                     vote_count int default 0, -- vote counts
                     release_date date, -- when the movie was released
                     revenue decimal (15,2) default 1000000, -- movie revenue
                     budget decimal(15,2) default 1000000, -- movie budget
                     runtime int default 90, -- movie runtime
                     constraint pk_imdb_id primary key (imdb)
);