package com.denspark.strelets.cinematrix.not_for_production.playlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class Translations {

    @SerializedName("subscriptions")
    @Expose
    private List<Object> subscriptions;

    @SerializedName("video")
    @Expose
    private Map<String, String> video;

    private Map<String, List<File>> videoFileList;

    private Map<String, List<Season>> mSeasons;

    @SerializedName("history")
    @Expose
    private List<Object> history;

    @SerializedName("pl")
    @Expose
    private String pl;

    @SerializedName("ok")
    @Expose
    private boolean ok;

    public void setSubscriptions(List<Object> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<Object> getSubscriptions() {
        return subscriptions;
    }

    public Map<String, String> getVideo() {
        return video;
    }

    public void setVideo(Map<String, String> video) {
        this.video = video;
    }

    public void setHistory(List<Object> history) {
        this.history = history;
    }

    public List<Object> getHistory() {
        return history;
    }

    public void setPl(String pl) {
        this.pl = pl;
    }

    public String getPl() {
        return pl;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public boolean isOk() {
        return ok;
    }

    public Map<String, List<File>> getVideoFileList() {
        return videoFileList;
    }

    public void setVideoFileList(Map<String, List<File>> videoFileList) {
        this.videoFileList = videoFileList;
    }

    public Map<String, List<Season>> getSeasons() {
        return mSeasons;
    }

    public void setSeasons(Map<String, List<Season>> seasons) {
        mSeasons = seasons;
    }

    @Override
    public String toString() {
        return
                "Translations{" +
                        "subscriptions = '" + subscriptions + '\'' +
                        ",video = '" + video + '\'' +
                        ",history = '" + history + '\'' +
                        ",pl = '" + pl + '\'' +
                        ",ok = '" + ok + '\'' +
                        "}";
    }
}