package com.assignment.notes.model;

public interface SaveNote {

    public void uploadNote(String formattedDateTime, String title, String content);
    public void saveNote(String updatedDateTime, String docID, String updatedTitle, String updatedContent);
}
