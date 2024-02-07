package com.example.notemath;

import android.content.Context;
import android.graphics.Color;

public class NoteSettings {
    public static final String SETTINGS_SELECTED_NOTE = "selectedNoteSettings";
    int fontSize, roundPrecision;
    private static NoteSettings defaultNoteSettings;

    public NoteSettings(int fontSize, int roundPrecision) {
        this.fontSize = fontSize;
        this.roundPrecision = roundPrecision;
    }

    public NoteSettings(NoteSettings otherNoteSettings) {
        this.fontSize = otherNoteSettings.getFontSize();
        this.roundPrecision = otherNoteSettings.getRoundPrecision();
    }

    public static NoteSettings getDefaultNoteSettings(Context context) {
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(context);
        if (defaultNoteSettings == null) {
            defaultNoteSettings = sqLiteManager.getNoteFromDB(-2).getNoteSettings();
        }
        return defaultNoteSettings;
    }

    public NoteSettings(String deserialize) {
        String[] values = deserialize.split(",");
        this.fontSize = Integer.parseInt(values[0]);
        this.roundPrecision = Integer.parseInt(values[1]);
    }

    public String toString() {
        String res = "";
        res += "FontSize: " + fontSize + ", ";
        res += "RoundPrecision: " + roundPrecision;
        return res;
    }

    public String serialize() {
        return this.fontSize + "," + this.roundPrecision;
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
