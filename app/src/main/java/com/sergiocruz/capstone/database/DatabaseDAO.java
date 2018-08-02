package com.sergiocruz.capstone.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.sergiocruz.capstone.model.Comment;
import com.sergiocruz.capstone.model.Travel;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface DatabaseDAO {

    @Query("SELECT * FROM Travel WHERE onSale == 1")
    List<Travel> getAllPromoTravels();

    @Query("SELECT * FROM Comment WHERE commentID == :commentID")
    Comment getCommentByID(String commentID);

    @Insert(onConflict = REPLACE)
    void saveComment(Comment comment);

    @Query("DELETE FROM Comment WHERE commentID == :commentID")
    void deleteCommentByID(String commentID);

}