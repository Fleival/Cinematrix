package com.denspark.strelets.cinematrix.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import lombok.Getter;
import lombok.Setter;

@Entity(tableName = "person_movie_join",
        primaryKeys = {"personId", "movieId"},
        foreignKeys = {
                @ForeignKey(entity = Person.class,
                        parentColumns = "id",
                        childColumns = "personId"),
                @ForeignKey(entity = FilmixMovie.class,
                        parentColumns = "id",
                        childColumns = "movieId")
        })
public class PersonMoviesJoin implements JoinEntity {
    @Getter @Setter private int  personId;
    @Getter @Setter private int  movieId;

    public PersonMoviesJoin(int personId, int  movieId){
        this.personId = personId;
        this.movieId = movieId;
    }
}
