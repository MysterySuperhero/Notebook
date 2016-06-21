package com.mysterysuperhero.notebook;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.mysterysuperhero.notebook.database.DataBaseContract;
import com.mysterysuperhero.notebook.utils.Note;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private View positiveAction;
    private EditText nameEditText;
    private EditText noteEditText;
    private ListView notesView;

    private static final String DEFAULT_COLOR = "#FFFFFF";
    public static final int NOTES_LOADER = 0;
    public static final int CATEGORIES_LOADER = 1;

    private int itemsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        initFloatingActionButton(fab);

        getSupportLoaderManager().initLoader(NOTES_LOADER, null, this);
        getSupportLoaderManager().initLoader(CATEGORIES_LOADER, null, this);

        notesView = (ListView) findViewById(R.id.notesListView);
        notesView.setAdapter(new NotesAdapter(this, new ArrayList<Note>()));
    }

    private void initFloatingActionButton(FloatingActionButton button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(MainActivity.this)
                        .title(R.string.action_add)
                        .items(getString(R.string.action_add_note), getString(R.string.action_add_category))
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        buildAddNoteDialog();
                                        break;
                                    case 1:
                                        buildAddCategoryDialog();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });
    }

    private void buildAddNoteDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
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
                        values.put(DataBaseContract.Notes.COLUMN_NAME_COLOR, DEFAULT_COLOR);

                        getContentResolver().insert(DataBaseContract.Notes.CONTENT_URI, values);
                    }
                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        nameEditText = (EditText) dialog.getCustomView().findViewById(R.id.addNoteDialogNameEditText);
        noteEditText = (EditText) dialog.getCustomView().findViewById(R.id.addNoteDialogNoteEditText);

        addTextChangedListener(nameEditText, noteEditText);
        addTextChangedListener(noteEditText, nameEditText);

        int widgetColor = ThemeSingleton.get().widgetColor;

        MDTintHelper.setTint(noteEditText,
                widgetColor == 0 ? ContextCompat.getColor(this, R.color.colorAccent) : widgetColor);

        MDTintHelper.setTint(nameEditText,
                widgetColor == 0 ? ContextCompat.getColor(this, R.color.colorAccent) : widgetColor);

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

    public void buildChangeNoteDialog(final Note note, final BaseAdapter adapter) {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.action_change_note_title)
                .customView(R.layout.add_note_dialog, true)
                .positiveText(R.string.action_change_positive)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ContentValues values = new ContentValues();
                        values.put(DataBaseContract.Notes.COLUMN_NAME_NAME, nameEditText.getText().toString());
                        values.put(DataBaseContract.Notes.COLUMN_NAME_TEXT, noteEditText.getText().toString());
                        values.put(DataBaseContract.Notes.COLUMN_NAME_COLOR, DEFAULT_COLOR);

                        String[] selectionArgs = {note.getId()};
                        getContentResolver().update(DataBaseContract.Notes.CONTENT_URI, values,
                                DataBaseContract.Notes._ID + " = ? ", selectionArgs);
                        note.setName(nameEditText.getText().toString());
                        note.setText(noteEditText.getText().toString());
                        note.setColor(DEFAULT_COLOR);
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
                widgetColor == 0 ? ContextCompat.getColor(this, R.color.colorAccent) : widgetColor);

        MDTintHelper.setTint(nameEditText,
                widgetColor == 0 ? ContextCompat.getColor(this, R.color.colorAccent) : widgetColor);

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }

    private void buildAddCategoryDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.action_add_category_title)
                .customView(R.layout.add_category_dialog, true)
                .positiveText(R.string.action_add_positive)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ContentValues values = new ContentValues();
                        values.put(DataBaseContract.Categories.COLUMN_NAME_NAME, nameEditText.getText().toString());
                        values.put(DataBaseContract.Notes.COLUMN_NAME_COLOR, DEFAULT_COLOR);

                        getContentResolver().insert(DataBaseContract.Categories.CONTENT_URI, values);
                    }
                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        nameEditText = (EditText) dialog.getCustomView().findViewById(R.id.addCategoryDialogNameEditText);

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        int widgetColor = ThemeSingleton.get().widgetColor;

        MDTintHelper.setTint(nameEditText,
                widgetColor == 0 ? ContextCompat.getColor(this, R.color.colorAccent) : widgetColor);

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri CONTACT_URI = null;
        switch (id) {
            case NOTES_LOADER:
                CONTACT_URI = DataBaseContract.Notes.CONTENT_URI;
                break;
            case CATEGORIES_LOADER:
                CONTACT_URI = DataBaseContract.Categories.CONTENT_URI;
                break;
            default:
                break;
        }
        return new CursorLoader(this, CONTACT_URI, null,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            switch (loader.getId()) {
                case NOTES_LOADER:
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
                        this.itemsCount = this.notesView.getAdapter().getCount();
                    }
                    break;
                case CATEGORIES_LOADER:
                    if (cursor.moveToFirst()) {
                        System.out.println("Categories cursor loaded!");
                        do {
                            System.out.println(cursor.getString(cursor.getColumnIndex(
                                    DataBaseContract.Categories.COLUMN_NAME_NAME
                            )));
                        } while (cursor.moveToNext());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
