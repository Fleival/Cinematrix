package com.denspark.strelets.cinematrix.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(tableName = "genre")
@ToString
public class Genre implements Nameble {


    @PrimaryKey
    @Getter @Setter private int id;

    @Getter @Setter private String name;

    public Genre(int id, String name){
        this.id = id;
        this.name = name;
    }
}
