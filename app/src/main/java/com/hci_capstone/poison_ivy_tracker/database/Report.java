package com.hci_capstone.poison_ivy_tracker.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity
public class Report {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "uid")
    private String uid;

    @ColumnInfo(name = "plant_type")
    private String plantType;

    @ColumnInfo(name = "latitude")
    private float latitude;

    @ColumnInfo(name = "longitude")
    private float longitude;

    @ColumnInfo(name = "date_time")
    private Date date;

    @ColumnInfo(name = "image_location")
    private String imageLocation;

    public Report (String uid, String plantType, float latitude, float longitude, Date date, String imageLocation) {
        this.uid = uid;
        this.plantType = plantType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.imageLocation = imageLocation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPlantType() {
        return plantType;
    }

    public void setPlantType(String plantType) {
        this.plantType = plantType;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UID: ").append(uid).append(" | ");
        sb.append("PlantType: ").append(plantType).append(" | ");
        sb.append("Latitude: ").append(latitude).append(" | ");
        sb.append("Longitude: ").append(longitude).append(" | ");
        sb.append("Date: ").append(date.toString()).append(" | ");
        sb.append("ImageLocation: ").append(imageLocation);
        return sb.toString();
    }
}
