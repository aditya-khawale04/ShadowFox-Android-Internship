package com.example.sffacebook.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("picture")
    private PictureData picture;

    @SerializedName("link")
    private String profileLink;

    public User() {
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PictureData getPicture() {
        return picture;
    }

    public void setPicture(PictureData picture) {
        this.picture = picture;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public String getProfilePictureUrl() {
        if (picture != null && picture.getData() != null) {
            return picture.getData().getUrl();
        }
        return null;
    }

    public static class PictureData {
        @SerializedName("data")
        private PictureInfo data;

        public PictureInfo getData() {
            return data;
        }

        public void setData(PictureInfo data) {
            this.data = data;
        }
    }

    public static class PictureInfo {
        @SerializedName("url")
        private String url;

        @SerializedName("height")
        private int height;

        @SerializedName("width")
        private int width;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }
    }
}
