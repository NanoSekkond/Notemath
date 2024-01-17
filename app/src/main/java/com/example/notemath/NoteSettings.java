package com.example.notemath;

import android.graphics.Color;

public class NoteSettings {
    public static final String SETTINGS_SELECTED_NOTE = "selectedNoteSettings";
    Color main, secondary;
    int fontSize;

    public static NoteSettings defaultNoteSettings = new NoteSettings(new Color(), new Color(), 20);

    public String toString() {
        String res = "";
        res += "FontSize: " + fontSize;
        return res;
    }

    public NoteSettings(Color main, Color secondary, int fontSize) {
        this.main = main;
        this.secondary = secondary;
        this.fontSize = fontSize;
    }

    public NoteSettings(NoteSettings otherNoteSettings) {
        this.main = otherNoteSettings.getMain();
        this.secondary = otherNoteSettings.getSecondary();
        this.fontSize = otherNoteSettings.getFontSize();
    }

    public Color getMain() {
        return main;
    }

    public void setMain(Color main) {
        this.main = main;
    }

    public Color getSecondary() {
        return secondary;
    }

    public void setSecondary(Color secondary) {
        this.secondary = secondary;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
}
