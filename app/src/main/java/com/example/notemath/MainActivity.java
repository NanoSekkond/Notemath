package com.example.notemath;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

public class MainActivity extends AppCompatActivity {

    private ListView noteListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        for (int i = 0; i < 1000; i++) {
            Note newNote = new Note(i, "sos muy lindo", String.valueOf(i), i * 1000 * 60, 0, NoteSettings.defaultNoteSettings);
            sqLiteManager.addNoteToDB(newNote);
        }*/
        initWidgets();
        loadFromDBToMemory();
        setNoteAdapter();
        setOnClickListener();
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

    private void setOnClickListener() {
        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note selectedNote = (Note) noteListView.getItemAtPosition(position);
                Intent editNoteIntent = new Intent(getApplicationContext(), NoteDetailActivity.class);
                editNoteIntent.putExtra(Note.NOTE_EDIT_EXTRA, selectedNote.getId());
                startActivity(editNoteIntent);
            }
        });
    }

    public void newNote(View view) {
        Intent newNoteIntent = new Intent(this, NoteDetailActivity.class);
        startActivity(newNoteIntent);
    }

    public void showPopupOptions(View view) {
        PopupMenu popup = new PopupMenu(getApplicationContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.general_options, popup.getMenu());
        popup.show();

        MenuItem settingsButton = popup.getMenu().findItem(R.id.settings);
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
        System.out.println("Opening Settings");
    }
}