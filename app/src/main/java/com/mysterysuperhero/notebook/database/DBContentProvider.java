package com.mysterysuperhero.notebook.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.HashMap;

/**
 * Created by dmitri on 20.06.16.
 */
public class DBContentProvider extends ContentProvider {

    private DataBaseHelper dbHelper;
    private Context context;

    private static HashMap<String, String> notesProjection;
    private static HashMap<String, String> categoriesProjection;
    private static HashMap<String, String> notesCategoriesProjection;

    private static final Integer NOTES = 1;
    private static final Integer CATEGORIES = 2;
    private static final Integer NOTES_CATEGORIES = 3;

    private static final HashMap<String, Integer> sUriMatcher;

    static {
        sUriMatcher = new HashMap<>();
        sUriMatcher.put(DataBaseContract.SCHEME + DataBaseContract.AUTHORITY + "/notes", NOTES);
        sUriMatcher.put(DataBaseContract.SCHEME + DataBaseContract.AUTHORITY + "/categories", CATEGORIES);
        sUriMatcher.put(DataBaseContract.SCHEME + DataBaseContract.AUTHORITY + "/notes_categories", NOTES_CATEGORIES);

        notesProjection = new HashMap<>();
        for(int i=0; i < DataBaseContract.Notes.DEFAULT_PROJECTION.length; i++) {
            notesProjection.put(
                    DataBaseContract.Notes.DEFAULT_PROJECTION[i],
                    DataBaseContract.Notes.DEFAULT_PROJECTION[i]);
        }

        categoriesProjection = new HashMap<>();
        for(int i = 0; i < DataBaseContract.Categories.DEFAULT_PROJECTION.length; i++) {
            categoriesProjection.put(
                    DataBaseContract.Categories.DEFAULT_PROJECTION[i],
                    DataBaseContract.Categories.DEFAULT_PROJECTION[i]);
        }

        notesCategoriesProjection = new HashMap<>();
        for(int i = 0; i < DataBaseContract.NotesCategories.DEFAULT_PROJECTION.length; i++) {
            notesCategoriesProjection.put(
                    DataBaseContract.NotesCategories.DEFAULT_PROJECTION[i],
                    DataBaseContract.NotesCategories.DEFAULT_PROJECTION[i]);
        }
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (!uriChecker(uri)) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        String u = uri.getScheme() + "://" + uri.getHost() + uri.getPath();

        dbHelper = new DataBaseHelper(getContext());

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String orderBy = null;
        switch (sUriMatcher.get(u)) {
            case 1: // NOTES
                qb.setTables(DataBaseContract.Notes.TABLE_NAME);
                qb.setProjectionMap(notesProjection);
                orderBy = DataBaseContract.Notes._ID + " ASC LIMIT 100";
                if (selectionArgs != null) {
                    qb.appendWhere(DataBaseContract.Notes._ID + "= ?");
                }
                break;

            case 2: // CATEGORIES
                qb.setTables(DataBaseContract.Categories.TABLE_NAME);
                qb.setProjectionMap(categoriesProjection);
                orderBy = DataBaseContract.Categories.COLUMN_NAME_NAME + " ASC";
                if (selectionArgs != null)
                    qb.appendWhere(DataBaseContract.Categories.COLUMN_NAME_NAME + "= ?");
                break;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        String u = uri.getScheme() + "://" + uri.getHost() + uri.getPath();

        if (!uriChecker(uri)) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        dbHelper = new DataBaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        }
        else {
            values = new ContentValues();
        }

        long rowId = -1;
        Uri rowUri = Uri.EMPTY;
        switch (sUriMatcher.get(u)) {
            case 1: // NOTES
                valuePutter(values, DataBaseContract.Notes.COLUMN_NAME_NAME);
                valuePutter(values, DataBaseContract.Notes.COLUMN_NAME_TEXT);
                valuePutter(values, DataBaseContract.Notes.COLUMN_NAME_COLOR);

                rowId = db.insertOrThrow(DataBaseContract.Notes.TABLE_NAME, null, values);
                if (rowId > 0) {
                    getContext().getContentResolver().notifyChange(DataBaseContract.Notes.CONTENT_URI, null);
                }
                break;

            case 2: // CATEGORIES
                valuePutter(values, DataBaseContract.Categories.COLUMN_NAME_NAME);
                valuePutter(values, DataBaseContract.Categories.COLUMN_NAME_COLOR);

                rowId = db.insertOrThrow(DataBaseContract.Categories.TABLE_NAME, null, values);
                if (rowId > 0) {
                    getContext().getContentResolver().notifyChange(DataBaseContract.Categories.CONTENT_URI, null);
                }
                break;
            case 3: // NOTES_CATEGORIES
                valuePutter(values, DataBaseContract.NotesCategories.COLUMN_NAME_NOTE_ID);
                valuePutter(values, DataBaseContract.NotesCategories.COLUMN_NAME_CATEGORY_ID);

                rowId = db.insertOrThrow(DataBaseContract.NotesCategories.TABLE_NAME, null, values);
                if (rowId > 0) {
                    getContext().getContentResolver().notifyChange(DataBaseContract.NotesCategories.CONTENT_URI, null);
                }
                break;
        }
        return rowUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String u = uri.getScheme() + "://" + uri.getHost() + uri.getPath();
        if (!uriChecker(uri)) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 0;
        String finalWhere = "";
        switch (sUriMatcher.get(u)) {
            case 1: // NOTES
                if (selection != null) {
                    finalWhere = selection;
                }
                count = db.update(DataBaseContract.Notes.TABLE_NAME, values, finalWhere, selectionArgs);
                break;
        }
        return count;
    }

    private void valuePutter(ContentValues values, String columnName) {
        if (!values.containsKey(columnName)) {
            values.put(columnName, "");
        }
    }

    private boolean uriChecker(Uri uri) {
        String u = uri.getScheme() + "://" + uri.getHost() + uri.getPath();
        return !(sUriMatcher.get(u) != NOTES && sUriMatcher.get(u) != CATEGORIES &&
                sUriMatcher.get(u) != NOTES_CATEGORIES);
    }
}
