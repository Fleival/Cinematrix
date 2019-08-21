package com.denspark.strelets.cinematrix.not_for_production.playlist;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Season {

    @SerializedName("folder")
    private List<Episode> folder;

    @SerializedName("title")
    private String title;

    public void setFolder(List<Episode> folder) {
        this.folder = folder;
    }

    public List<Episode> getFolder() {
        return folder;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return
                "Season{" +
                        "folder = '" + folder + '\'' +
                        ",title = '" + title + '\'' +
                        "}";
    }
}