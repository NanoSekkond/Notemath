package com.example.notemath;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

public class MainActivity extends AppCompatActivity {

    private ListView noteListView;
    private final LocaleManager localeManager = LocaleManager.instanceOfLocale();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //The last changed is used to store the Language in the DB, this is an awful solution but it works and
        //I don't want to make another table just to store one (1) setting.
        localeManager.setLocale(SQLiteManager.instanceOfDatabase(this).getNoteFromDB(-2).getLastChange());
        /*
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        for (int i = 0; i < 1000; i++) {
            Note newNote = new Note(i, "sos muy lindo", String.valueOf(i), i * 1000 * 60, 0, NoteSettings.defaultNoteSettings);
            sqLiteManager.addNoteToDB(newNote);
        }*/
        initWidgets();
        loadFromDBToMemory();
        setNoteAdapter();
        setOnClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNoteAdapter();
    }

    private void initWidgets() {
        noteListView = findViewById(R.id.noteListView);
    }

    private void setNoteAdapter() {
        NoteAdapter noteAdapter = new NoteAdapter(getApplicationContext(), Note.noteArrayList);
        noteListView.setAdapter(noteAdapter);
    }

    private void loadFromDBToMemory() {
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        sqLiteManager.populateNoteListArray();
    }

    private void setOnClickListeners() {
        noteListView.setOnItemClickListener((parent, view, position, id) -> {
            Note selectedNote = (Note) noteListView.getItemAtPosition(position);
            Intent editNoteIntent = new Intent(getApplicationContext(), NoteDetailActivity.class);
            editNoteIntent.putExtra(Note.NOTE_EDIT_EXTRA, selectedNote.getId());
            startActivity(editNoteIntent);
        });

        noteListView.setOnItemLongClickListener((parent, view, position, id) -> {
            PopupMenu popup = new PopupMenu(getApplicationContext(), view, Gravity.END);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.main_activity_note_menu, popup.getMenu());
            popup.show();

            Note selectedNote = (Note) noteListView.getItemAtPosition(position);
            MenuItem deleteButton = popup.getMenu().findItem(R.id.deleteNote);
            deleteButton.setTitle(LocaleManager.instanceOfLocale().getString("delete_button"));
            deleteButton.setOnMenuItemClickListener(item -> {
                deleteNote(selectedNote);
                return false;
            });
            return true;
        });
    }

    public void newNote(View view) {
        Intent newNoteIntent = new Intent(this, NoteDetailActivity.class);
        startActivity(newNoteIntent);
    }

    private void deleteNote(Note selectedNote) {
        LocaleManager localeManager = LocaleManager.instanceOfLocale();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(localeManager.getString("delete_desc")).setTitle(localeManager.getString("delete_title"));
        builder.setPositiveButton(localeManager.getString("yes_button"),
                (dialog, which) -> {
                    if (selectedNote != null) {
                        Note.noteArrayList.remove(selectedNote);
                        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
                        sqLiteManager.deleteNoteFromDB(selectedNote);
                    }
                    finish();
                });
        builder.setNegativeButton(localeManager.getString("no_button"),
                (dialog, which) -> {
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showPopupOptions(View view) {
        PopupMenu popup = new PopupMenu(getApplicationContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.general_options, popup.getMenu());
        popup.show();

        MenuItem settingsButton = popup.getMenu().findItem(R.id.settings);
        settingsButton.setTitle(localeManager.getString("settings_button"));
        settingsButton.setOnMenuItemClickListener(item -> {
            openSettings(view);
            return false;
        });

        MenuItem deleteAllButton = popup.getMenu().findItem(R.id.deleteAll);
        deleteAllButton.setOnMenuItemClickListener(item -> {
            SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
            for (Note note : Note.noteArrayList) {
                sqLiteManager.deleteNoteFromDB(note);
            }
            return false;
        });
    }

    private void openSettings(View view) {
        Intent settingsIntent = new Intent(this, GeneralSettingsActivity.class);
        startActivity(settingsIntent);
    }
}