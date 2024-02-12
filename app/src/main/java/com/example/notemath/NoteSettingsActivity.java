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
    String fontSizeString, roundPrecisionString, fontSizeDescString, roundPrecisionDescString;

    int minFontSize = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setLocale();
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
                fontSizeTitle.setText(fontSizeString + progress);
                //exampleText.setTextSize(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        fontSizeBar.setProgress(selectedNoteSettings.getFontSize() - minFontSize);
        fontSizeTitle.setText(fontSizeString + selectedNoteSettings.getFontSize());

        roundPrecisionBar = findViewById(R.id.roundPrecisionBar);
        roundPrecisionTitle = findViewById(R.id.roundPrecisionTitle);
        roundPrecisionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                roundPrecisionTitle.setText(roundPrecisionString + progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        roundPrecisionBar.setProgress(selectedNoteSettings.getRoundPrecision());
        roundPrecisionTitle.setText(roundPrecisionString + selectedNoteSettings.getRoundPrecision());

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
            builder.setMessage(fontSizeDescString);
        }
        else if (buttonID == R.id.roundPrecisionButton) {
            builder.setMessage(roundPrecisionDescString);
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setLocale() {
        LocaleManager localeManager = LocaleManager.instanceOfLocale();
        fontSizeString = localeManager.getString("font_size_title");
        fontSizeDescString = localeManager.getString("font_size_desc");
        roundPrecisionString = localeManager.getString("decimal_points_title");
        roundPrecisionDescString = localeManager.getString("decimal_points_desc");
    }
}
