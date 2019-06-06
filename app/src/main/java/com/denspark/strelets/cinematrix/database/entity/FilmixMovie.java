package com.denspark.strelets.cinematrix.database.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity(tableName = "movie")
public class FilmixMovie implements ContainerEntity{
    @PrimaryKey
    @Expose
    @Getter @Setter private int id;

    @Expose
    @Getter @Setter private String name;

    @Expose
    @Getter @Setter private String nameORIG;

    @Expose
    @Getter @Setter private String country;

    @Expose
    @Getter @Setter private String filmPosterUrl;

    @Expose
    @Getter @Setter private String filmPageUrl;

    @Expose
    @Getter @Setter private String year;

    @Expose
    @Getter @Setter private String quality;

    @Expose
    @Getter @Setter private Date uploadDate;

    @Expose
    @Getter @Setter private String duration;

    @Expose
    @Getter @Setter private String descriptionStory;

    @Ignore
    @Expose
    @Getter @Setter private List<Integer> actorsId;

    @Ignore
    @Expose
    @Getter @Setter private List<Integer> genresId;

    @Ignore
    @Getter @Setter private List<Genre> genres;

}