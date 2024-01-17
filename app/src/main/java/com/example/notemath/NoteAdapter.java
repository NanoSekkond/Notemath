package com.example.notemath;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NoteAdapter extends ArrayAdapter<Note> {
    public NoteAdapter(Context context, List<Note> notes) {
        super(context, 0, notes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Note note = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.note_cell, parent, false);
        }

        TextView title = convertView.findViewById(R.id.cellTitle);
        TextView desc = convertView.findViewById(R.id.cellDesc);
        TextView date = convertView.findViewById(R.id.cellDate);

        if (note.getTitle().isEmpty()) {
            title.setText("Title");
        }
        else {
            title.setText(note.getTitle());
        }
        desc.setText(note.getDescription());
        date.setText(dateFormat(note.getDate()));

        return convertView;
    }

    private String dateFormat(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MMM/yy");
        Date currentDate = new Date(time);
        return sdf.format(currentDate);
    }
}
