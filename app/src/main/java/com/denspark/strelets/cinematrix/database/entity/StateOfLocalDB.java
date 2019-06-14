package com.denspark.strelets.cinematrix.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;


@Entity(tableName = "local_db_state")
public class StateOfLocalDB {
    @PrimaryKey(autoGenerate = true)
    @Getter @Setter private int id;
    @Expose
    @Getter @Setter private int maxId;
    @Expose
    @Getter @Setter private long moviesCount;
}
