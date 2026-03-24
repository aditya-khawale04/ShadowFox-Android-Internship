package com.example.sffacebook.models;

import com.google.gson.annotations.SerializedName;

public class Feed {
    @SerializedName("id")
    private String id;

    @SerializedName("message")
    private String message;

    @SerializedName("story")
    private String story;

    @SerializedName("created_time")
    private String createdTime;

    @SerializedName("type")
    private String type;

    @SerializedName("picture")
    private String picture;

    @SerializedName("link")
    private String link;

    @SerializedName("caption")
    private String caption;

    @SerializedName("description")
    private String description;

    @SerializedName("from")
    private From from;

    public Feed() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public From getFrom() {
        return from;
    }

    public void setFrom(From from) {
        this.from = from;
    }

    public String getContent() {
        return message != null ? message : (story != null ? story : "");
    }

    public static class From {
        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
