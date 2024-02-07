package com.example.notemath;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.PopupMenu;
import android.view.MenuInflater;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class NoteDetailActivity extends AppCompatActivity {

    private EditText titleEditText, descEditText;
    private int lastChange, oldLastChange, lineStart;

    private Calculator calc;

    private Note selectedNote;
    private boolean wasChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        initWidgets();
        loadSettings();
        checkForEditNote();
        wasChanged = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSettings();
    }

    private void initWidgets() {
        titleEditText = findViewById(R.id.titleEditText);
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                wasChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        descEditText = findViewById(R.id.descriptionEditText);
        descEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lastChange = descEditText.getSelectionEnd() - 1;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                oldLastChange = lastChange;
                lastChange = start + count - 1;
                lineStart = lastChange;
                while(lineStart > 0 && text.charAt(lineStart - 1) != '\n') {
                    lineStart--;
                }
                wasChanged = true;
                System.out.println("lastChange: " + lastChange + ", oldLastChange: " + oldLastChange + ", lineStart: " + lineStart);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (lastChange >= 0 && text.charAt(lastChange) == '=' && oldLastChange < lastChange) {
                    equalCommand(text);
                }
                else if (lastChange >= 0 && text.charAt(lastChange) == '\n' && oldLastChange < lastChange) {
                    listCommand(text);
                }
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                saveNote(null);
                finish();
            }
        });
    }

    private void checkForEditNote() {
        Intent previousIntent = getIntent();
        int passedNoteID = previousIntent.getIntExtra(Note.NOTE_EDIT_EXTRA, -1);
        selectedNote = Note.getNoteForID(passedNoteID);

        if (selectedNote != null) {
            titleEditText.setText(selectedNote.getTitle());
            descEditText.setText(selectedNote.getDescription());
            if (!selectedNote.getDescription().isEmpty()) {
                descEditText.requestFocus();
                descEditText.setSelection(selectedNote.getLastChange() + 1);
            }
        }
    }

    public void saveNote(View view) {
        if (wasChanged == true) {
            SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
            String title = String.valueOf(titleEditText.getText());
            String desc = String.valueOf(descEditText.getText());
            long date = new Date().getTime();

            if (selectedNote == null) {
                int id = unusedID();
                Note newNote = new Note(id, title, desc, date, lastChange, new NoteSettings(NoteSettings.getDefaultNoteSettings(this)));
                selectedNote = newNote;
                Note.noteArrayList.add(0, selectedNote);
                sqLiteManager.addNoteToDB(selectedNote);
            }
            else {
                selectedNote.setTitle(title);
                selectedNote.setDescription(desc);
                selectedNote.setDate(date);
                selectedNote.setLastChange(lastChange);
                selectedNote.setNoteSettings(selectedNote.getNoteSettings());
                Note.noteArrayList.remove(selectedNote);
                Note.noteArrayList.add(0, selectedNote);
                sqLiteManager.updateNoteInDB(selectedNote);
            }
        }

        if (view != null) {
            finish();
        }
    }

    public void showPopupNoteOptions(View view) {
        PopupMenu popup = new PopupMenu(getApplicationContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.note_options, popup.getMenu());
        popup.show();

        MenuItem deleteButton = popup.getMenu().findItem(R.id.deleteNote);
        deleteButton.setOnMenuItemClickListener(item -> {
            deleteNote(view);
            return false;
        });

        MenuItem settingsButton = popup.getMenu().findItem(R.id.noteSettings);
        settingsButton.setOnMenuItemClickListener(item -> {
            openNoteSettings();
            return false;
        });

        MenuItem helpButton = popup.getMenu().findItem(R.id.noteHelp);
        helpButton.setOnMenuItemClickListener(item -> {
            return false;
        });
    }

    public void openNoteSettings() {
        wasChanged = true;
        saveNote(null);
        Intent noteSettingsIntent = new Intent(this, NoteSettingsActivity.class);
        noteSettingsIntent.putExtra(NoteSettings.SETTINGS_SELECTED_NOTE, selectedNote.getId());
        startActivity(noteSettingsIntent);
    }

    private void loadSettings() {
        NoteSettings settings;
        if (selectedNote != null) {
            settings = selectedNote.getNoteSettings();
        }
        else {
            settings = NoteSettings.getDefaultNoteSettings(this);
        }
        titleEditText.setTextSize(settings.getFontSize());
        descEditText.setTextSize(settings.getFontSize());
        calc = new Calculator(settings.getRoundPrecision());
    }

    public void deleteNote(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("Are you sure you want to delete this note?").setTitle("Delete");
        builder.setPositiveButton("Yes",
                (dialog, which) -> {
                    if (selectedNote != null) {
                        Note.noteArrayList.remove(selectedNote);
                        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
                        sqLiteManager.deleteNoteFromDB(selectedNote);
                    }
                    finish();
                });
        builder.setNegativeButton("No",
                (dialog, which) -> {
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String currentTime() {
        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MMM/yy");
        Date currentDate = new Date(time);
        return sdf.format(currentDate);
    }

    private int unusedID() {
        Set<Integer> usedIds = new HashSet<>();

        for (Note note : Note.noteArrayList) {
            usedIds.add(note.getId());
        }
        int res = 0;
        while (usedIds.contains(res)) {
            res++;
        }
        return res;
    }

    private void equalCommand(String text) {
        String expression = text.substring(lineStart, lastChange);
        expression = getCleanExpression(expression);
        String res = calc.doMath(expression, false);
        String match = new Scanner(res).findInLine("^Error:");
        if (match == null) {
            if (text.charAt(lastChange - 1) == ' ') {
                String newText = text.substring(0, lastChange) + "=";
                newText += " " + res;
                newText += text.substring(lastChange + 1);
                descEditText.setText(newText);
                descEditText.setSelection(oldLastChange + res.length() + 2);
            }
            else {
                String newText = text.substring(0, lastChange - expression.length());
                newText += new Scanner(expression).findInLine("^\\s*") + res;
                newText += text.substring(lastChange + 1);
                descEditText.setText(newText);
                descEditText.setSelection(oldLastChange - new Scanner(expression).findInLine("\\S+.*").length() + res.length());
            }
        }
        else {
            warning(res);
        }
    }

    private String getCleanExpression(String expression) {
        expression = expression.replaceAll("^\\d\\.\\s", "");
        Scanner sc = new Scanner(expression);
        expression = sc.findInLine("[\\d\\(\\)\\+\\-\\*\\/\\^\\.\\s\\%\\!]*$");
        sc.close();
        if (expression == null) {
            expression = "";
        }
        return expression;
    }

    private void listCommand(String text) {
        Scanner sc = new Scanner(text.substring(lineStart, lastChange));
        String match = sc.findInLine("^\\d+\\.\\s");
        sc.close();
        if (match != null) {
            String newText = "";
            int count = Integer.valueOf(match.substring(0, match.length() - 2)) + 1;
            newText += text.substring(0, lastChange + 1);
            newText += count + ". ";
            if (text.length() > lastChange + 2) {
                newText += "\n" + text.substring(lastChange + 2);
            }
            descEditText.setText(newText);
            descEditText.setSelection(oldLastChange + (count +". ").length() + 1);
        }
    }

    private void warning(String warning) {
        Toast.makeText(getApplicationContext(), warning, Toast.LENGTH_SHORT).show();
    }
}
