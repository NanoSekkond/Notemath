package com.example.notemath;

import android.graphics.Color;

public class NoteSettings {
    public static final String SETTINGS_SELECTED_NOTE = "selectedNoteSettings";
    int fontSize, roundPrecision;

    public static NoteSettings defaultNoteSettings = new NoteSettings(20, 5);

    public String toString() {
        String res = "";
        res += "FontSize: " + fontSize + ", ";
        res += "RoundPrecision: " + roundPrecision;
        return res;
    }

    public NoteSettings(int fontSize, int roundPrecision) {
        this.fontSize = fontSize;
        this.roundPrecision = roundPrecision;
    }

    public NoteSettings(NoteSettings otherNoteSettings) {
        this.fontSize = otherNoteSettings.getFontSize();
        this.roundPrecision = otherNoteSettings.getRoundPrecision();
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getRoundPrecision() {
        return roundPrecision;
    }

    public void setRoundPrecision(int roundPrecision) {
        this.roundPrecision = roundPrecision;
    }
}
