package com.denspark.strelets.cinematrix.not_for_production.playlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Episode {

    @SerializedName("file")
    @Expose
    private String file;

    private List<File> mFiles;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("title")
    @Expose
    private String title;

    public void setFile(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public List<File> getFiles() {
        return mFiles;
    }

    public void setFiles(List<File> files) {
        mFiles = files;
    }

    @Override
    public String toString() {
        return
                "Episode{" +
                        "file = '" + file + '\'' +
                        ",id = '" + id + '\'' +
                        ",title = '" + title + '\'' +
                        "}";
    }
}