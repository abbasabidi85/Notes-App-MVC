package com.assignment.notes.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.assignment.notes.model.NoteModel;

import java.util.List;

@Dao
public interface DataAccessObject {

    @Insert(onConflict = REPLACE)
    void insert(NoteModel note);

    @Query("SELECT * FROM notes ORDER BY ID DESC")
    List<NoteModel> getAll();

    @Query("UPDATE notes SET title = :title, content = :content WHERE ID= :ID")
    void update(int ID, String title, String content);

    @Delete
    void delete(NoteModel note);
}
