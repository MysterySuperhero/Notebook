package com.mysterysuperhero.notebook.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mysterysuperhero.notebook.activities.MainActivity;
import com.mysterysuperhero.notebook.R;
import com.mysterysuperhero.notebook.database.DataBaseContract;
import com.mysterysuperhero.notebook.utils.CategoriesGetter;
import com.mysterysuperhero.notebook.utils.Category;
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

        final TextView noteTextView = (TextView) holder.cardView.findViewById(R.id.textTextView);
        noteTextView.setText(notes.get(position).getText());
        if (noteTextView.getText().length() < 10) {
            noteTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48);
        } else {
            if (noteTextView.getText().length() < 25) {
                noteTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            }
        }

        if (notes.get(position).getColor() == null) {
            notes.get(position).setColor(MainActivity.DEFAULT_COLOR);
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

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ArrayList<String> categoriesNames = new ArrayList<>();
                final ArrayList<Category> categories = new ArrayList<>();
                CategoriesGetter.getCategories(context, categoriesNames, categories);
                new MaterialDialog.Builder(context)
                        .title(R.string.category)
                        .items(categoriesNames)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                ContentValues values = new ContentValues();
                                values.put(DataBaseContract.Notes.COLUMN_NAME_CATEGORY, categories.get(which).getId());
                                String[] selectionArgs = { notes.get(position).getId() };
                                context.getContentResolver().update(
                                        DataBaseContract.Notes.CONTENT_URI,
                                        values, DataBaseContract.Notes._ID + " = ? ",
                                        selectionArgs
                                );
                                notes.get(position).setColor(categories.get(which).getColor());
                                notifyDataSetChanged();
                            }
                        })
                        .positiveText(android.R.string.cancel)
                        .show();
                return true;
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

    public void clearAndAddToNotes(ArrayList<Note> newNotes) {
        this.notes.clear();
        this.notes.addAll(newNotes);
    }

    public void clearNotes() {
        this.notes.clear();
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
