package com.example.notemath;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class NoteSettingsActivity extends AppCompatActivity {

    SeekBar fontSizeBar, roundPrecisionBar;
    Note selectedNote;
    NoteSettings selectedNoteSettings;
    TextView fontSizeTitle, roundPrecisionTitle, exampleText;

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
        fontSizeTitle = findViewById(R.id.fontSizeTitle);
        //exampleText = findViewById(R.id.exampleText);
        fontSizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += minFontSize;
                fontSizeTitle.setText("Font size: " + progress);
                //exampleText.setTextSize(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        fontSizeBar.setProgress(selectedNoteSettings.getFontSize() - minFontSize);
        fontSizeTitle.setText("Font size: " + selectedNoteSettings.getFontSize());

        roundPrecisionBar = findViewById(R.id.roundPrecisionBar);
        roundPrecisionTitle = findViewById(R.id.roundPrecisionTitle);
        roundPrecisionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                roundPrecisionTitle.setText("Digits after decimal point: " + progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        roundPrecisionBar.setProgress(selectedNoteSettings.getRoundPrecision());
        roundPrecisionTitle.setText("Digits after decimal point: " + selectedNoteSettings.getRoundPrecision());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                saveSettings(null);
                finish();
            }
        });
    }

    public void saveSettings(View view) {
        int fontSize = fontSizeBar.getProgress() + minFontSize;
        int roundPrecision = roundPrecisionBar.getProgress();
        selectedNoteSettings.setFontSize(fontSize);
        selectedNoteSettings.setRoundPrecision(roundPrecision);
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        if (selectedNote != null) {
            sqLiteManager.updateNoteInDB(selectedNote);
        }
        else {
            Note defaultSettingsNote = new Note(-2, "", "", 0, 0, selectedNoteSettings);
            sqLiteManager.updateNoteInDB(defaultSettingsNote);
        }
        finish();
    }

    private void getNote() {
        Intent previousIntent = getIntent();
        int passedNoteID = previousIntent.getIntExtra(NoteSettings.SETTINGS_SELECTED_NOTE, -1);
        selectedNote = Note.getNoteForID(passedNoteID);
        if (selectedNote == null) {
            selectedNoteSettings = NoteSettings.getDefaultNoteSettings(this);
        }
        else {
            selectedNoteSettings = selectedNote.getNoteSettings();
        }
    }

    public void moreInfo(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        int buttonID = view.getId();
        if (buttonID == R.id.fontSizeButton) {
            builder.setMessage("Changes the font size of your title and description.");
        }
        else if (buttonID == R.id.roundPrecisionButton) {
            builder.setMessage("Changes the amount of digits after the decimal dot.");
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
