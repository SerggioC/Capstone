package com.sergiocruz.capstone.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.sergiocruz.capstone.model.Travel;

import java.util.List;

@Dao
public interface DatabaseDAO {

    @Query("SELECT * FROM Travel WHERE isFavorite == 1")
    List<Travel> getAllFavoriteTravels();

}
