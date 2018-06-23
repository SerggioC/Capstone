package com.sergiocruz.capstone.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.sergiocruz.capstone.database.StringListConverter;

import java.util.List;

@Entity
public class Travel {

    @PrimaryKey(autoGenerate = false)
    int travelID;
    String travelName;
    String description;
    String price;
    long date; // in System.currentTimeMillis()
    @TypeConverters(StringListConverter.class)
    List<String> imagesList;
    @TypeConverters(StringListConverter.class)
    List<String> videosList;
    int isFavorite;

    public Travel(int travelID, String travelName, String description, String price, long date, List<String> imagesList, List<String> videosList, int isFavorite) {
        this.travelID = travelID;
        this.travelName = travelName;
        this.description = description;
        this.price = price;
        this.date = date;
        this.imagesList = imagesList;
        this.videosList = videosList;
        this.isFavorite = isFavorite;
    }

    public int getTravelID() {
        return travelID;
    }

    public void setTravelID(int travelID) {
        this.travelID = travelID;
    }

    public String getTravelName() {
        return travelName;
    }

    public void setTravelName(String travelName) {
        this.travelName = travelName;
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

    public List<String> getImagesList() {
        return imagesList;
    }

    public void setImagesList(List<String> imagesList) {
        this.imagesList = imagesList;
    }

    public List<String> getVideosList() {
        return videosList;
    }

    public void setVideosList(List<String> videosList) {
        this.videosList = videosList;
    }

    public int isFavorite() {
        return isFavorite;
    }

    public void setFavorite(int favorite) {
        isFavorite = favorite;
    }

    @Ignore
    @Override
    public String toString() {
        StringBuilder imageListString = new StringBuilder();
        for (int i = 0; i < imagesList.size(); i++) {
            imageListString.append(imagesList.get(i));
        }
        StringBuilder videosListString = new StringBuilder();
        for (int i = 0; i < videosList.size(); i++) {
            videosListString.append(videosList.get(i));
        }

        return "Travel{" +
                "travelID=" + travelID +
                ", travelName='" + travelName + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                ", date=" + date +
                ", imagesList=" + imageListString +
                ", videosList=" + videosListString +
                '}';
    }
}
