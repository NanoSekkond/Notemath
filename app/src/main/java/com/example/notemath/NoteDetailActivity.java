package com.example.notemath;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
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

    private static final Calculator calc = new Calculator(5);

    private Note selectedNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        initWidgets();
        loadSettings();
        checkForEditNote();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSettings();
    }

    private void initWidgets() {
        titleEditText = findViewById(R.id.titleEditText);
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
                //System.out.println("lastChange: " + lastChange + ", oldLastChange: " + oldLastChange + ", lineStart: " + lineStart);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Editable text = s;
                if (lastChange >= 0 && text.charAt(lastChange) == '=' && oldLastChange < lastChange) {
                    equalCommand(text);
                }
                else if (lastChange >= 0 && text.charAt(lastChange) == '\n' && oldLastChange < lastChange) {
                    listCommand(text);
                }
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
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        String title = String.valueOf(titleEditText.getText());
        String desc = String.valueOf(descEditText.getText());
        long date = new Date().getTime();

        if (selectedNote == null) {
            int id = unusedID();
            Note newNote = new Note(id, title, desc, date, lastChange, new NoteSettings(NoteSettings.defaultNoteSettings));
            selectedNote = newNote;
            Note.noteArrayList.add(0, selectedNote);
            sqLiteManager.addNoteToDB(selectedNote);
        }
        else {
            selectedNote.setTitle(title);
            selectedNote.setDescription(desc);
            selectedNote.setDate(date);
            selectedNote.setLastChange(lastChange);
            Note.noteArrayList.remove(selectedNote);
            Note.noteArrayList.add(0, selectedNote);
            sqLiteManager.updateNoteInDB(selectedNote);
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

    public void setBold(View view) {
        int start = descEditText.getSelectionStart();
        int end = descEditText.getSelectionEnd();
        SpannableStringBuilder newText = new SpannableStringBuilder(descEditText.getText());
        newText.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        descEditText.setText(newText);
        descEditText.setSelection(oldLastChange + 1);
        System.out.println(newText);
    }

    public void setItalic(View view) {
        int start = descEditText.getSelectionStart();
        int end = descEditText.getSelectionEnd();
        SpannableStringBuilder newText = new SpannableStringBuilder(descEditText.getText());
        newText.setSpan(new android.text.style.StyleSpan(Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        descEditText.setText(newText);
        descEditText.setSelection(oldLastChange + 1);
        System.out.println(newText);
    }

    public void openNoteSettings() {
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
            settings = NoteSettings.defaultNoteSettings;
        }
        titleEditText.setTextSize(settings.getFontSize());
        descEditText.setTextSize(settings.getFontSize());
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

    private void equalCommand(Editable text) {
        String expression = text.toString().substring(lineStart, lastChange);
        expression = getCleanExpression(expression);
        String res = calc.doMath(expression);
        String match = new Scanner(res).findInLine("^Error:");
        if (match == null) {
            if (text.charAt(lastChange - 1) == ' ') {
                SpannableStringBuilder newText = new SpannableStringBuilder(text.subSequence(0, lastChange))
                        .append("= ")
                        .append(res)
                        .append(text.subSequence(lastChange + 1, text.length()));
                descEditText.setText(newText);
                descEditText.setSelection(oldLastChange + res.length() + 2);
            }
            else {
                SpannableStringBuilder newText = new SpannableStringBuilder(text.subSequence(0, lastChange - expression.length()))
                        .append(new Scanner(expression).findInLine("^\\s*"))
                        .append(res)
                        .append(text.subSequence(lastChange + 1, text.length()));
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

    private void listCommand(Editable text) {
        Scanner sc = new Scanner(text.toString().substring(lineStart, lastChange));
        String match = sc.findInLine("^\\d+\\.\\s");
        sc.close();
        if (match != null) {
            int count = Integer.parseInt(match.substring(0, match.length() - 2)) + 1;
            SpannableStringBuilder newText = new SpannableStringBuilder(text.subSequence(0, lastChange + 1))
                    .append(String.valueOf(count))
                    .append(". ");
            if (text.length() > lastChange + 2) {
                newText.append("\n")
                        .append(text.subSequence(lastChange + 2, text.length()));
            }
            descEditText.setText(newText);
            descEditText.setSelection(oldLastChange + (count +". ").length() + 1);
        }
    }

    private void warning(String warning) {
        Toast.makeText(getApplicationContext(), warning, Toast.LENGTH_SHORT).show();
    }
}
