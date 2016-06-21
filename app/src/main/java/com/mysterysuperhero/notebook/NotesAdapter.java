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
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder>  {

    Context context;
    ArrayList<Note> notes;

    public NotesAdapter(Context context, ArrayList<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        // TODO Auto-generated method stub
//
//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        View view;
//
//        if (convertView == null) {
//
//            view = new View(context);
//
//            view = inflater.inflate(R.layout.notes_item, null);
//
//        } else {
//            view = (View) convertView;
//        }
//
//        TextView nameTextView = (TextView) view.findViewById(R.id.nameTextView);
//        nameTextView.setText(notes.get(position).getName());
//
//        TextView noteTextView = (TextView) view.findViewById(R.id.textTextView);
//        noteTextView.setText(notes.get(position).getText());
//
//        ((CardView) view.findViewById(R.id.cardView)).setCardBackgroundColor(Color.parseColor(
//                notes.get(position).getColor()
//        ));
//
//        view.findViewById(R.id.cardView).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((MainActivity) context).buildChangeNoteDialog(notes.get(position), NotesAdapter.this);
//            }
//        });
//
//        return view;
//    }


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

        holder.cardView.setCardBackgroundColor(Color.parseColor(
                notes.get(position).getColor()
        ));

        holder.cardView.findViewById(R.id.cardView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).buildChangeNoteDialog(notes.get(position), NotesAdapter.this);
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
