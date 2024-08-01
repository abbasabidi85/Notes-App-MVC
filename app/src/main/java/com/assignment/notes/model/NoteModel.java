package com.assignment.notes.model;

import androidx.annotation.ColorInt;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

public class NoteModel {


    private String dateTime;
    private String title;

    private String content;

    @ServerTimestamp
    private Timestamp timeStamp;

    public NoteModel(){

    }

    public NoteModel(String dateTime, String title, String content, Timestamp timeStamp){

        this.dateTime=dateTime;
        this.title=title;
        this.content=content;
        this.timeStamp=timeStamp;
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

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }
}

