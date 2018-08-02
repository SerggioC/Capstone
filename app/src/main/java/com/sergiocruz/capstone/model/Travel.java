package com.sergiocruz.capstone.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.sergiocruz.capstone.database.DateConverter;
import com.sergiocruz.capstone.database.StringListConverter;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Travel implements Parcelable {

    @NonNull
    @PrimaryKey(autoGenerate = false)
    private String ID;
    private String name;
    private String country;
    private String description;
    private String price;
    @TypeConverters(DateConverter.class)
    private Long date; // in System.currentTimeMillis(), or FBDB timestamp ServerValue.TIMESTAMP
    @TypeConverters(StringListConverter.class)
    private List<String> images;
    @TypeConverters(StringListConverter.class)
    private List<String> videos;
    @TypeConverters(StringListConverter.class)
    private List<String> types;
    private Boolean available;
    private double latitude;
    private double longitude;
    private int comments;
    private int stars;
    private float rating;
    private Boolean onSale;

    @Ignore
    public Travel() {
        // No Arg constructor for Firebase
    }

    public Travel(@NonNull String ID, String name, String country, String description, String price, Long date, List<String> images, List<String> videos, List<String> types, Boolean available, double latitude, double longitude, int comments, int stars, float rating, Boolean onSale) {
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
        this.latitude = latitude;
        this.longitude = longitude;
        this.comments = comments;
        this.stars = stars;
        this.rating = rating;
        this.onSale = onSale;
    }

    protected Travel(Parcel in) {
        name = in.readString();
        country = in.readString();
        description = in.readString();
        price = in.readString();
        date = in.readByte() == 0x00 ? null : in.readLong();
        if (in.readByte() == 0x01) {
            images = new ArrayList<String>();
            in.readList(images, String.class.getClassLoader());
        } else {
            images = null;
        }
        if (in.readByte() == 0x01) {
            videos = new ArrayList<String>();
            in.readList(videos, String.class.getClassLoader());
        } else {
            videos = null;
        }
        if (in.readByte() == 0x01) {
            types = new ArrayList<String>();
            in.readList(types, String.class.getClassLoader());
        } else {
            types = null;
        }
        byte availableVal = in.readByte();
        available = availableVal == 0x02 ? null : availableVal != 0x00;
        latitude = in.readDouble();
        longitude = in.readDouble();
        comments = in.readInt();
        stars = in.readInt();
        rating = in.readFloat();
        byte onSaleVal = in.readByte();
        available = onSaleVal == 0x02 ? null : onSaleVal != 0x00;
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

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Boolean getOnSale() {
        return onSale;
    }

    public void setOnSale(Boolean onSale) {
        this.onSale = onSale;
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
                ", available= " + available +
                ", comments= " + comments +
                ", stars= " + stars +
                ", rating= " + rating +
                ", onSale= " + onSale +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(country);
        dest.writeString(description);
        dest.writeString(price);
        if (date == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(date);
        }
        if (images == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(images);
        }
        if (videos == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(videos);
        }
        if (types == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(types);
        }
        if (available == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (available ? 0x01 : 0x00));
        }
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(comments);
        dest.writeInt(stars);
        dest.writeFloat(rating);
        if (onSale == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (onSale ? 0x01 : 0x00));
        }
    }


    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Travel> CREATOR = new Parcelable.Creator<Travel>() {
        @Override
        public Travel createFromParcel(Parcel in) {
            return new Travel(in);
        }

        @Override
        public Travel[] newArray(int size) {
            return new Travel[size];
        }
    };


}
