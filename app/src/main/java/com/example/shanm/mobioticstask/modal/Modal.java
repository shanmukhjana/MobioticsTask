package com.example.shanm.mobioticstask.modal;

public class Modal {
    String description;
    String id;
    String image;
    String title;
    String videoUrl;

    public Modal(String description, String id, String image, String title, String videoUrl) {
        this.description = description;
        this.id = id;
        this.image = image;
        this.title = title;
        this.videoUrl = videoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
