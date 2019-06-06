package com.denspark.strelets.cinematrix.database.entity;

import androidx.room.*;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity(tableName = "people")
@ToString
public class Person implements ContainerEntity {

    @PrimaryKey
    @Expose
    @Getter @Setter private int id;

    @Expose
    @Getter @Setter private String name;

    @ColumnInfo(name = "origin_name")
    @Expose
    @Getter @Setter private String originName;

    @ColumnInfo(name = "photo_url")
    @Expose
    @Getter @Setter private String photoUrl;

    @Expose
    @Getter @Setter private String birthplace;

    @Ignore
    @Expose
    @Getter @Setter private List<Integer> filmsParticipationId;

    @Expose
    @Getter @Setter private String bio;

    @Ignore
    @Expose
    @Getter @Setter private List<Integer> filmsGenresId;

    @Ignore
    @Getter @Setter private List<Genre> filmsGenres;

    @ColumnInfo(name = "page_url")
    @Expose
    @Getter @Setter private String pageUrl;

    @ColumnInfo(name = "date_of_birth")
    @Expose
    @Getter @Setter private long dateOfBirth;

    @Expose
    @Getter @Setter private String height;

}