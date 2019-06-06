package com.denspark.strelets.cinematrix.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import lombok.Getter;
import lombok.Setter;

@Entity(tableName = "person_genre_join",
        primaryKeys = {"personId", "genreId"},
        foreignKeys = {
                @ForeignKey(entity = Person.class,
                        parentColumns = "id",
                        childColumns = "personId"),
                @ForeignKey(entity = Genre.class,
                        parentColumns = "id",
                        childColumns = "genreId")
        })
public class PersonGenreJoin implements JoinEntity {
    @Getter @Setter private int  personId;
    @Getter @Setter private int  genreId;

    public PersonGenreJoin(int personId, int  genreId){
        this.personId = personId;
        this.genreId = genreId;
    }
}
