package com.denspark.strelets.cinematrix.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import lombok.Getter;
import lombok.Setter;

@Entity(tableName = "movie_genre_join",
        primaryKeys = { "movieId", "genreId" },
        indices = {@Index("genreId"),@Index("movieId")},
        foreignKeys = {
                @ForeignKey(entity = FilmixMovie.class,
                        parentColumns = "id",
                        childColumns = "movieId"),
                @ForeignKey(entity = Genre.class,
                        parentColumns = "id",
                        childColumns = "genreId")
        })
public class MovieGenreJoin implements JoinEntity {
    @Getter @Setter private int  movieId;
    @Getter @Setter private int  genreId;

    public MovieGenreJoin(int movieId, int  genreId){
        this.movieId = movieId;
        this.genreId = genreId;
    }
}