package com.example.notemath;

import java.util.ArrayList;

public class Note {

    public static ArrayList<Note> noteArrayList = new ArrayList<>();
    public static String NOTE_EDIT_EXTRA = "noteEdit";
    private int id;
    private String title;
    private String description;
    private long edited;

    private NoteSettings noteSettings;

    private int lastChange;

    public String toString(){
        String res = "";
        res += id + ", " + title + ", " + description + ", " + noteSettings.toString();
        return res;
    }

    public Note(int id, String title, String description, long edited, int lastChange, NoteSettings noteSettings) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.edited = edited;
        this.lastChange = lastChange;
        this.noteSettings = noteSettings;
    }

    public Note(Note otherNote) {
        this.id = otherNote.getId();
        this.title = otherNote.getTitle();
        this.description = otherNote.getDescription();
        this.edited = otherNote.getDate();
        this.lastChange = otherNote.getLastChange();
        this.noteSettings = otherNote.getNoteSettings();
    }

    public static Note getNoteForID(int passedID) {
        for (Note note : noteArrayList) {
            if (note.getId() == passedID) {
                return note;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDate() {
        return edited;
    }

    public void setDate(long edited) {
        this.edited = edited;
    }

    public int getLastChange() {
        return lastChange;
    }

    public void setLastChange(int lastChange) {
        this.lastChange = lastChange;
    }

    public NoteSettings getNoteSettings() {return noteSettings;}

    public void setNoteSettings(NoteSettings noteSettings) {this.noteSettings = noteSettings;}
}
