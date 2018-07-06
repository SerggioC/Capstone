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
    private String country;
    private String description;
    private String price;
    private Long date; // in System.currentTimeMillis(), or FBDB timestamp ServerValue.TIMESTAMP
    @TypeConverters(StringListConverter.class)
    private List<String> images;
    @TypeConverters(StringListConverter.class)
    private List<String> videos;
    @TypeConverters(StringListConverter.class)
    private List<String> types;
    private Boolean available;
    private int isFavorite;
    private double latitude;
    private double longitude;

    @Ignore
    public Travel() {
        // No Arg constructor for Firebase
    }

    public Travel(@NonNull String ID, String name, String country, String description, String price, Long date, List<String> images, List<String> videos, List<String> types, Boolean available, int isFavorite, double latitude, double longitude) {
        this.ID = ID;
        this.name = name;
        this.country = country;
        this.description = description;
        this.price = price;
        this.date = date;
        this.images = images;
        this.videos = videos;
        this.types = types;
        this.available = available;
        this.isFavorite = isFavorite;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @NonNull
    public String getID() {
        return ID;
    }

    public void setID(@NonNull String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
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

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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
                ", name= " + name +
                ", country= " + country +
                ", description= " + description +
                ", price= " + price +
                ", date= " + String.valueOf(date) +
                ", images= " + imageListString +
                ", videos= " + videosListString +
                ", types= " + travelListString +
                ", isFavorite= " + isFavorite +
                ", available= " + available +
                '}';
    }
}
