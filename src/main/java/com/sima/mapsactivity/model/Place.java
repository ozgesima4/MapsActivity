package com.sima.mapsactivity.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Place implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "PLACENAME")
    public String name;

    @ColumnInfo(name = "LATITUDE")
    public double latitude;

    @ColumnInfo(name = "LONGITUDE")
    public double longitude;

    // Boş bir constructor ekleyin
    public Place() {
    }

    // Veri eklerken kullanılacak constructor
    public Place(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
