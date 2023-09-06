package com.sima.mapsactivity.roomDb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.sima.mapsactivity.model.Place;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface IDOA {

    @Query("SELECT * FROM Place ")
    Flowable<List<Place>> getall();

    @Insert
    Completable insert(Place place);

    @Delete
    Completable delete(Place place);



}
