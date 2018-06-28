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
    private int travelID;
    private String travelName;
    private String description;
    private String price;
    private long date; // in System.currentTimeMillis(), or FBDB timestamp ServerValue.TIMESTAMP
    @TypeConverters(StringListConverter.class)
    private List<String> imagesList;
    @TypeConverters(StringListConverter.class)
    private List<String> videosList;
    @TypeConverters(StringListConverter.class)
    private List<String> travelTypes;
    private int isAvailable;
    private int isFavorite;

    @Ignore
    public Travel() {
        // No Arg constructor for Firebase
    }

    public Travel(int travelID, String travelName, String description, String price, long date, List<String> imagesList, List<String> videosList, List<String> travelTypes, int isFavorite, int isAvailable) {
        this.travelID = travelID;
        this.travelName = travelName;
        this.description = description;
        this.price = price;
        this.date = date;
        this.imagesList = imagesList;
        this.videosList = videosList;
        this.travelTypes = travelTypes;
        this.isFavorite = isFavorite;
        this.isAvailable = isAvailable;
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

    public List<String> getTravelTypes() {
        return travelTypes;
    }

    public void setTravelTypes(List<String> travelTypes) {
        this.travelTypes = travelTypes;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public int getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(int isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Ignore
    @Override
    public String toString() {

        StringBuilder imageListString = new StringBuilder();
        if (imagesList != null && imagesList.size() > 0) {
            for (int i = 0; i < imagesList.size(); i++) {
                imageListString.append(imagesList.get(i));
            }
        }

        StringBuilder videosListString = new StringBuilder();
        if (videosList != null && videosList.size() > 0) {
            for (int i = 0; i < videosList.size(); i++) {
                videosListString.append(videosList.get(i));
            }
        }

        StringBuilder travelListString = new StringBuilder();
        if (travelTypes != null && travelTypes.size() > 0) {
            for (int i = 0; i < travelTypes.size(); i++) {
                travelListString.append(travelTypes.get(i));
            }
        }

        return "Travel{" +
                "travelID= " + travelID +
                ", travelName= '" + travelName + '\'' +
                ", description= '" + description + '\'' +
                ", price= '" + price + '\'' +
                ", date= " + date +
                ", imagesList= " + imageListString +
                ", videosList= " + videosListString +
                ", travelTypes= " + travelListString +
                ", isFavorite= " + isFavorite +
                ", isAvailable= " + isAvailable +
                '}';
    }
}
