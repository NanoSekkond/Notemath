package com.example.notemath;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GeneralSettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_settings);
    }

    public void openNoteSettings(View view) {
        Intent noteSettingsIntent = new Intent(this, NoteSettingsActivity.class);
        startActivity(noteSettingsIntent);
    }

    public void saveSettings(View view) {
        finish();
    }
}