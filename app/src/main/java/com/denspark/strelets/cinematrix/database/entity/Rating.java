package com.denspark.strelets.cinematrix.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(tableName = "rating")
@ToString
public class Rating {

    @PrimaryKey
    @Getter
    @Setter
    private int id;

    @Getter
    @Setter
    private int negRating;

    @Getter
    @Setter
    private int posRating;


}