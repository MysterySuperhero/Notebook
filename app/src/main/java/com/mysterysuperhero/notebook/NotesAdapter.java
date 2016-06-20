package com.mysterysuperhero.notebook;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mysterysuperhero.notebook.utils.Note;

import java.util.ArrayList;

/**
 * Created by dmitri on 21.06.16.
 */
public class NotesAdapter extends BaseAdapter {

    Context context;
    ArrayList<Note> notes;

    public NotesAdapter(Context context, ArrayList<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.notes_gridview_item, null);

            // set value into textview
            TextView nameTextView = (TextView) gridView.findViewById(R.id.nameTextView);
            nameTextView.setText(notes.get(position).getName());

            TextView noteTextView = (TextView) gridView.findViewById(R.id.textTextView);
            nameTextView.setText(notes.get(position).getText());

        } else {
            gridView = (View) convertView;
        }

        gridView.setBackgroundColor(Color.parseColor(notes.get(position).getColor()));

        return gridView;
    }

    @Override
    public int getCount() {
        return this.notes.size();
    }

    public String getItem(int position) {
        return this.notes.get(position).getText();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void addToNotes(ArrayList<Note> newNotes) {
        this.notes.addAll(newNotes);
    }
}
