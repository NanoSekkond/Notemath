package com.example.notemath;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NoteSettingsActivity extends AppCompatActivity {

    SeekBar fontSizeBar;
    Note selectedNote;
    TextView sliderText, exampleText;

    int minFontSize = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getNote();
        initWidgets();
    }

    private void initWidgets() {
        fontSizeBar = findViewById(R.id.fontSizeBar);
        sliderText = findViewById(R.id.fontSizeTitle);
        //exampleText = findViewById(R.id.exampleText);
        fontSizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += minFontSize;
                sliderText.setText("Font size: " + progress);
                //exampleText.setTextSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        fontSizeBar.setProgress(selectedNote.getNoteSettings().getFontSize() - minFontSize);
    }

    public void saveSettings(View view) {
        NoteSettings selectedNoteSettings = selectedNote.getNoteSettings();
        int fontSize = fontSizeBar.getProgress() + minFontSize;
        selectedNoteSettings.setFontSize(fontSize);
        finish();
    }

    private void getNote() {
        Intent previousIntent = getIntent();
        int passedNoteID = previousIntent.getIntExtra(NoteSettings.SETTINGS_SELECTED_NOTE, -1);
        selectedNote = Note.getNoteForID(passedNoteID);
    }
}
