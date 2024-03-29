package com.example.notemath;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SQLiteManager extends SQLiteOpenHelper {
    private static SQLiteManager sqLiteManager;
    private static final String DATABASE_NAME = "NotesDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Notes";
    private static final String COUNTER = "Counter";


    private static final String ID_FIELD = "id";
    private static final String TITLE_FIELD = "title";
    private static final String DESC_FIELD = "description";
    private static final String EDITED_FIELD = "date";
    private static final String LAST_CHANGE_FIELD = "lastChange";
    private static final String SETTINGS_FIELD = "settings";


    public SQLiteManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteManager instanceOfDatabase(Context context) {
        if (sqLiteManager == null) {
            sqLiteManager = new SQLiteManager(context);
        }
        return sqLiteManager;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME +
                "(" + COUNTER + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ID_FIELD + " INT, " +
                TITLE_FIELD + " TEXT, " +
                DESC_FIELD + " TEXT, " +
                EDITED_FIELD + " BIGINT, " +
                LAST_CHANGE_FIELD + " INT, " +
                SETTINGS_FIELD + " TEXT" +
                ")";
        db.execSQL(sql);
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_FIELD, -2);
        contentValues.put(TITLE_FIELD, "");
        contentValues.put(DESC_FIELD, "");
        contentValues.put(EDITED_FIELD, 0);
        contentValues.put(LAST_CHANGE_FIELD, 0);
        contentValues.put(SETTINGS_FIELD, "20,2");
        db.insert(TABLE_NAME, null, contentValues);
        System.out.println("Db created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addNoteToDB(Note note) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_FIELD, note.getId());
        contentValues.put(TITLE_FIELD, note.getTitle());
        contentValues.put(DESC_FIELD, note.getDescription());
        contentValues.put(EDITED_FIELD, note.getDate());
        contentValues.put(LAST_CHANGE_FIELD, note.getLastChange());
        contentValues.put(SETTINGS_FIELD, note.getNoteSettings().serialize());

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        System.out.println("Note added " + note.getId());
    }

    public void populateNoteListArray() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor res = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + EDITED_FIELD + " DESC", null);
        if (res.getCount() != 0) {
            while (res.moveToNext()) {
                int id = res.getInt(1);
                if (id >= 0) {
                    String title = res.getString(2);
                    String desc = res.getString(3);
                    long edited = res.getLong(4);
                    int lastChange = res.getInt(5);
                    NoteSettings settings = new NoteSettings(res.getString(6));
                    Note note = new Note(id, title, desc, edited, lastChange, settings);
                    Note.noteArrayList.add(note);
                    System.out.println(note);
                }
            }
        }
    }

    public void updateNoteInDB(Note note) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_FIELD, note.getId());
        contentValues.put(TITLE_FIELD, note.getTitle());
        contentValues.put(DESC_FIELD, note.getDescription());
        contentValues.put(EDITED_FIELD, note.getDate());
        contentValues.put(LAST_CHANGE_FIELD, note.getLastChange());
        contentValues.put(SETTINGS_FIELD, note.getNoteSettings().serialize());

        sqLiteDatabase.update(TABLE_NAME, contentValues, ID_FIELD + " =? ", new String[]{String.valueOf(note.getId())});
    }

    public void deleteNoteFromDB(Note note) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, ID_FIELD + " =? ", new String[]{String.valueOf(note.getId())});
        System.out.println("Note deleted " + note.getId());
    }

    public Note getNoteFromDB(int id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor res = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ID_FIELD + " = " + id, null);
        res.moveToNext();
        String title = res.getString(2);
        String desc = res.getString(3);
        long edited = res.getLong(4);
        int lastChange = res.getInt(5);
        NoteSettings settings = new NoteSettings(res.getString(6));
        return new Note(id, title, desc, edited, lastChange, settings);
    }
}
