package com.denspark.strelets.cinematrix.not_for_production.playlist;

public class File {
    private String quality;
    private String fileUrl;

    public File(String quality, String fileUrl) {
        this.quality = quality;
        this.fileUrl = fileUrl;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
