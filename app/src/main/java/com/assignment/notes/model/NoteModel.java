package com.assignment.notes.model;

import androidx.annotation.ColorInt;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

public class NoteModel {


    private String dateTime;
    private String title;

    private String content;

    public NoteModel(){

    }

    public NoteModel(String dateTime, String title, String content){

        this.dateTime=dateTime;
        this.title=title;
        this.content=content;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

