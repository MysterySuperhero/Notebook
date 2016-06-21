package com.mysterysuperhero.notebook.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.mysterysuperhero.notebook.MainActivity;
import com.mysterysuperhero.notebook.R;
import com.mysterysuperhero.notebook.database.DataBaseContract;
import com.mysterysuperhero.notebook.events.NotesLoadedEvent;
import com.mysterysuperhero.notebook.utils.FragmentsVisiblity;
import com.mysterysuperhero.notebook.utils.Note;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by dmitri on 21.06.16.
 */
public class NotesFragment extends Fragment implements FragmentsVisiblity {

    private View positiveAction;
    private EditText nameEditText;
    private EditText noteEditText;
    private RecyclerView notesView;

    public int itemsCount = 0;

    public NotesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.notes_tab, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        EventBus.getDefault().register(this);

        notesView = (RecyclerView) getView().findViewById(R.id.notesRecyclerView);
        notesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        notesView.setItemAnimator(new SlideInUpAnimator());
        notesView.setAdapter(new NotesAdapter(getActivity(), new ArrayList<Note>(), this));
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void buildAddNoteDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.action_add_note_title)
                .customView(R.layout.add_note_dialog, true)
                .positiveText(R.string.action_add_positive)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ContentValues values = new ContentValues();
                        values.put(DataBaseContract.Notes.COLUMN_NAME_NAME, nameEditText.getText().toString());
                        values.put(DataBaseContract.Notes.COLUMN_NAME_TEXT, noteEditText.getText().toString());
                        values.put(DataBaseContract.Notes.COLUMN_NAME_COLOR, MainActivity.DEFAULT_COLOR);

                        getActivity().getContentResolver().insert(DataBaseContract.Notes.CONTENT_URI, values);
                        itemsCount++;
                    }
                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        nameEditText = (EditText) dialog.getCustomView().findViewById(R.id.addNoteDialogNameEditText);
        noteEditText = (EditText) dialog.getCustomView().findViewById(R.id.addNoteDialogNoteEditText);

        addTextChangedListener(nameEditText, noteEditText);
        addTextChangedListener(noteEditText, nameEditText);

        int widgetColor = ThemeSingleton.get().widgetColor;

        MDTintHelper.setTint(noteEditText,
                widgetColor == 0 ? ContextCompat.getColor(getActivity(), R.color.colorAccent) : widgetColor);

        MDTintHelper.setTint(nameEditText,
                widgetColor == 0 ? ContextCompat.getColor(getActivity(), R.color.colorAccent) : widgetColor);

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }

    private void addTextChangedListener(EditText editText, final EditText anotherEditText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!anotherEditText.getText().toString().isEmpty()) {
                    positiveAction.setEnabled(s.toString().trim().length() > 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void buildChangeNoteDialog(final Note note, final NotesAdapter adapter) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.action_change_note_title)
                .customView(R.layout.add_note_dialog, true)
                .positiveText(R.string.action_change_save)
                .negativeText(android.R.string.cancel)
                .neutralText(R.string.action_change_delete)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String[] selectionArgs = { note.getId() };
                        getActivity().getContentResolver().delete(DataBaseContract.Notes.CONTENT_URI,
                                DataBaseContract.Notes._ID + " = ? ", selectionArgs);
                        adapter.removeItemById(note.getId());
                        adapter.notifyDataSetChanged();
                        itemsCount--;
                    }
                } )
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ContentValues values = new ContentValues();
                        values.put(DataBaseContract.Notes.COLUMN_NAME_NAME, nameEditText.getText().toString());
                        values.put(DataBaseContract.Notes.COLUMN_NAME_TEXT, noteEditText.getText().toString());
                        values.put(DataBaseContract.Notes.COLUMN_NAME_COLOR, MainActivity.DEFAULT_COLOR);

                        String[] selectionArgs = { note.getId() };
                        getActivity().getContentResolver().update(DataBaseContract.Notes.CONTENT_URI, values,
                                DataBaseContract.Notes._ID + " = ? ", selectionArgs);
                        note.setName(nameEditText.getText().toString());
                        note.setText(noteEditText.getText().toString());
                        note.setColor(MainActivity.DEFAULT_COLOR);
                        adapter.notifyDataSetChanged();
                    }
                }).build();


        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        nameEditText = (EditText) dialog.getCustomView().findViewById(R.id.addNoteDialogNameEditText);
        noteEditText = (EditText) dialog.getCustomView().findViewById(R.id.addNoteDialogNoteEditText);
        nameEditText.setText(note.getName());
        noteEditText.setText(note.getText());

        addTextChangedListener(nameEditText, noteEditText);
        addTextChangedListener(noteEditText, nameEditText);

        int widgetColor = ThemeSingleton.get().widgetColor;

        MDTintHelper.setTint(noteEditText,
                widgetColor == 0 ? ContextCompat.getColor(getActivity(), R.color.colorAccent) : widgetColor);

        MDTintHelper.setTint(nameEditText,
                widgetColor == 0 ? ContextCompat.getColor(getActivity(), R.color.colorAccent) : widgetColor);

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }

    @Subscribe
    public void onNotesLoadedEvent(NotesLoadedEvent event) {
        Cursor cursor = event.cursor;
        if (cursor.moveToFirst()) {
            System.out.println("Notes cursor loaded!");
            ArrayList<Note> notes = new ArrayList<>();

            if (this.itemsCount != 0) {
                cursor.move(this.itemsCount - 1);
            }

            do {
                notes.add(new Note(
                        cursor.getString(cursor.getColumnIndex(DataBaseContract.Notes._ID)),
                        cursor.getString(cursor.getColumnIndex(DataBaseContract.Notes.COLUMN_NAME_NAME)),
                        cursor.getString(cursor.getColumnIndex(DataBaseContract.Notes.COLUMN_NAME_TEXT)),
                        cursor.getString(cursor.getColumnIndex(DataBaseContract.Notes.COLUMN_NAME_COLOR))
                ));
            } while (cursor.moveToNext());
            ((NotesAdapter) this.notesView.getAdapter()).addToNotes(notes);
            ((NotesAdapter) this.notesView.getAdapter()).notifyDataSetChanged();
            this.itemsCount = this.notesView.getAdapter().getItemCount();
        }
    }

    @Override
    public void fragmentBecameVisible() {
        ((MainActivity) getActivity()).fab.setOnClickListener(null);
        ((MainActivity) getActivity()).fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildAddNoteDialog();
            }
        });
    }
}
