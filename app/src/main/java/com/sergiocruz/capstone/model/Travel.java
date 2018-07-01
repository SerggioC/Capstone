package com.sergiocruz.capstone.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.sergiocruz.capstone.database.StringListConverter;

import java.util.List;

@Entity
public class Travel {

    @NonNull
    @PrimaryKey(autoGenerate = false)
    private String ID;
    private String name;
    private String description;
    private String price;
    private long date; // in System.currentTimeMillis(), or FBDB timestamp ServerValue.TIMESTAMP
    @TypeConverters(StringListConverter.class)
    private List<String> images;
    @TypeConverters(StringListConverter.class)
    private List<String> videos;
    @TypeConverters(StringListConverter.class)
    private List<String> types;
    private Boolean available;
    private int isFavorite;

    @Ignore
    public Travel() {
        // No Arg constructor for Firebase
    }

    public Travel(String ID, String name, String description, String price, long date, List<String> images, List<String> videos, List<String> types, int isFavorite, Boolean available) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.price = price;
        this.date = date;
        this.images = images;
        this.videos = videos;
        this.types = types;
        this.isFavorite = isFavorite;
        this.available = available;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getVideos() {
        return videos;
    }

    public void setVideos(List<String> videos) {
        this.videos = videos;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    @Ignore
    @Override
    public String toString() {

        StringBuilder imageListString = new StringBuilder();
        if (images != null && images.size() > 0) {
            for (int i = 0; i < images.size(); i++) {
                imageListString.append(images.get(i));
            }
        }

        StringBuilder videosListString = new StringBuilder();
        if (videos != null && videos.size() > 0) {
            for (int i = 0; i < videos.size(); i++) {
                videosListString.append(videos.get(i));
            }
        }

        StringBuilder travelListString = new StringBuilder();
        if (types != null && types.size() > 0) {
            for (int i = 0; i < types.size(); i++) {
                travelListString.append(types.get(i));
            }
        }

        return "Travel{" +
                "ID= " + ID +
                ", name= '" + name + '\'' +
                ", description= '" + description + '\'' +
                ", price= '" + price + '\'' +
                ", date= " + date +
                ", images= " + imageListString +
                ", videos= " + videosListString +
                ", types= " + travelListString +
                ", isFavorite= " + isFavorite +
                ", available= " + available +
                '}';
    }
}
