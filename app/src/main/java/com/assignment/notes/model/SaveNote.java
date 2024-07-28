package com.assignment.notes.model;

public interface SaveNote {

    public void uploadNote(String title, String content);
    public void saveNote(String docID, String updatedTitle, String updatedContent);
}
