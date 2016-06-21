package com.mysterysuperhero.notebook;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
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

        View view;

        if (convertView == null) {

            view = new View(context);

            view = inflater.inflate(R.layout.notes_gridview_item, null);

        } else {
            view = (View) convertView;
        }

        TextView nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        nameTextView.setText(notes.get(position).getName());

        TextView noteTextView = (TextView) view.findViewById(R.id.textTextView);
        noteTextView.setText(notes.get(position).getText());

        ((CardView) view.findViewById(R.id.cardView)).setCardBackgroundColor(Color.parseColor(
                notes.get(position).getColor()
        ));

        return view;
    }

    @Override
    public int getCount() {
        return this.notes.size();
    }

    @Override
    public Note getItem(int position) {
        return this.notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void addToNotes(ArrayList<Note> newNotes) {
        this.notes.addAll(newNotes);
    }

    public Note getItemById(String id) {
        for(Note note : notes) {
            if (note.getId().equals(id))
                return note;
        }
        return null;
    }

}
