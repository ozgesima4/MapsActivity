package com.sima.mapsactivity.roomDb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.sima.mapsactivity.model.Place;

@Database(entities = {Place.class}, version = 1)
public abstract class Placedb extends RoomDatabase {
    public abstract IDOA placedao();

}
