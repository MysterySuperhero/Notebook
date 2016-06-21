package com.mysterysuperhero.notebook.fragments;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mysterysuperhero.notebook.R;
import com.mysterysuperhero.notebook.utils.Note;

import java.util.ArrayList;

/**
 * Created by dmitri on 21.06.16.
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder>  {

    Context context;
    ArrayList<Note> notes;
    NotesFragment fragment;

    public NotesAdapter(Context context, ArrayList<Note> notes, NotesFragment fragment) {
        this.context = context;
        this.notes = notes;
        this.fragment = fragment;
    }

    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notes_item, viewGroup, false);
        return new NotesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NotesViewHolder holder, final int position) {
        TextView nameTextView = (TextView) holder.cardView.findViewById(R.id.nameTextView);
        nameTextView.setText(notes.get(position).getName());

        TextView noteTextView = (TextView) holder.cardView.findViewById(R.id.textTextView);
        noteTextView.setText(notes.get(position).getText());
        if (noteTextView.getText().length() < 10) {
            noteTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48);
        } else {
            if (noteTextView.getText().length() < 25) {
                noteTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            }
        }

        holder.cardView.setCardBackgroundColor(Color.parseColor(
                notes.get(position).getColor()
        ));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.buildChangeNoteDialog(notes.get(position), NotesAdapter.this);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.notes.size();
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

    public void removeItemById(String id) {
        for(int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId().equals(id)) {
                notes.remove(i);
                break;
            }
        }
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        TextView name;
        TextView text;
        String _id;

        NotesViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            name = (TextView)itemView.findViewById(R.id.nameTextView);
            text = (TextView)itemView.findViewById(R.id.textTextView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
//                    itemTouchedCallback.onFeedItemTouchedCallback(taskID, userName.getText().toString(),
//                            shortDescription.getText().toString(), description, date.getText().toString(),
//                            rating.getText().toString());
                }
            });

        }
    }

}
