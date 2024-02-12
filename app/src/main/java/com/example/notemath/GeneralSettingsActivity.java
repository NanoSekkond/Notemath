package com.example.notemath;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Objects;

public class GeneralSettingsActivity extends AppCompatActivity {

    LinearLayout settingsButton, languageButton;
    TextView languageString, settingsString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_settings);
        initWidgets();
        setLocale();
    }

    @Override
    protected void onResume() {
        super.onResume();
        revertColors();
    }

    private void initWidgets() {
        settingsButton = findViewById(R.id.settingsLayout);
        languageButton = findViewById(R.id.languageLayout);
        languageString = findViewById(R.id.languageSelector);
        settingsString = findViewById(R.id.settingsSelector);
        setOnClickListeners();
    }

    private void revertColors() {
        settingsButton.setBackgroundColor(getResources().getColor(R.color.white, null));
    }
    private void setOnClickListeners() {
        settingsButton.setOnClickListener(view -> {
            settingsButton.setBackgroundColor(getResources().getColor(R.color.gray, null));
            openNoteSettings();
        });

        languageButton.setOnClickListener(view -> {
            openLanguageMenu();
        });
    }

    private void openNoteSettings() {
        Intent noteSettingsIntent = new Intent(this, NoteSettingsActivity.class);
        startActivity(noteSettingsIntent);
    }

    private void openLanguageMenu() {
        LocaleManager localeManager = LocaleManager.instanceOfLocale();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String en = localeManager.getString("language_english");
        String es = localeManager.getString("language_spanish");
        String[] languageList = {en, es};
        final int[] choice = {localeManager.getLocale()};
        builder.setTitle("Select a language:");
        builder.setSingleChoiceItems(languageList, localeManager.getLocale(), (dialog, item) -> {
            String lang = languageList[item];
            if (Objects.equals(lang, en)) {
                choice[0] = LocaleManager.EN;
            } else if (Objects.equals(lang, es)) {
                choice[0] = LocaleManager.ES;
            }
        });
        builder.setOnCancelListener(dialog -> {
            localeManager.setLocale(choice[0]);
            setLocale();
            saveLanguageToDb(choice[0]);
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setLocale() {
        LocaleManager localeManager = LocaleManager.instanceOfLocale();
        languageString.setText(localeManager.getString("language_setting"));
        settingsString.setText(localeManager.getString("default_settings"));
    }

    private void saveLanguageToDb(int locale) {
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        Note defaultSettingsNote = new Note(-2, "", "", 0, locale, sqLiteManager.getNoteFromDB(-2).getNoteSettings());
        sqLiteManager.updateNoteInDB(defaultSettingsNote);
    }

    public void saveSettings(View view) {
        finish();
    }
}