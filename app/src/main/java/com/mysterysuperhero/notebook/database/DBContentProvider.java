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
//        for(int i=0; i < DataBaseContract.Notes.DEFAULT_PROJECTION.length; i++) {
//            notesProjection.put(
//                    DataBaseContract.Notes.DEFAULT_PROJECTION[i],
//                    DataBaseContract.Notes.DEFAULT_PROJECTION[i]);
//        }

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

                StringBuilder sb = new StringBuilder();
                sb.append(DataBaseContract.Notes.TABLE_NAME);
                sb.append(" LEFT JOIN ");
                sb.append(DataBaseContract.Categories.TABLE_NAME);
                sb.append(" ON (");
                sb.append("notes.category");
                sb.append(" = ");
                sb.append("categories._id");
                sb.append(")");
                String table = sb.toString();

                qb.setTables(table);
                notesProjection.put("notes._id", "notes._id AS _id");
                notesProjection.put("notes.name", "notes.name AS name");
                notesProjection.put("notes.text", "notes.text AS text");
                notesProjection.put("notes.category", "notes.category AS category");
                notesProjection.put("categories.color", "categories.color AS color");
                qb.setProjectionMap(notesProjection);
                orderBy = DataBaseContract.Notes._ID + " ASC LIMIT 100";
                if (selectionArgs != null) {
                    qb.appendWhere(DataBaseContract.Notes._ID + "= ?");
                }
                break;

            case 2: // CATEGORIES
                qb.setTables(DataBaseContract.Categories.TABLE_NAME);
                qb.setProjectionMap(categoriesProjection);
                orderBy = DataBaseContract.Categories._ID + " ASC LIMIT 100";
                if (selectionArgs != null)
                    qb.appendWhere(DataBaseContract.Categories._ID + "= ?");
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
        }
        return rowUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String finalWhere;
        int count;
        String u = uri.getScheme() + "://" + uri.getHost() + uri.getPath();
        switch (sUriMatcher.get(u)) {
            case 1: // NOTES
                finalWhere = selection;
                count = db.delete(DataBaseContract.Notes.TABLE_NAME, finalWhere, selectionArgs);
                break;
            case 2: // CATEGORIES
                finalWhere = selection;
                count = db.delete(DataBaseContract.Categories.TABLE_NAME, finalWhere, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return count;
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
            case 2: // CATEGORIES
                if (selection != null) {
                    finalWhere = selection;
                }
                count = db.update(DataBaseContract.Categories.TABLE_NAME, values, finalWhere, selectionArgs);
                getContext().getContentResolver().notifyChange(
                        Uri.parse(DataBaseContract.SCHEME + DataBaseContract.AUTHORITY + "/notes"),
                        null
                );
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
