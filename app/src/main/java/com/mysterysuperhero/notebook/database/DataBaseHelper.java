package com.mysterysuperhero.notebook.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dmitri on 20.06.16.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "notebook.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String UNIQUE_PROPERTY = " not null UNIQUE";


    public static final String SQL_CREATE_TABLE_NOTES =
            "CREATE TABLE IF NOT EXISTS " + DataBaseContract.Notes.TABLE_NAME + " (" +
                    DataBaseContract.Notes._ID + " INTEGER PRIMARY KEY," +
                    DataBaseContract.Notes.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    DataBaseContract.Notes.COLUMN_NAME_TEXT + TEXT_TYPE + COMMA_SEP +
                    DataBaseContract.Notes.COLUMN_NAME_COLOR + TEXT_TYPE +
                    " )";

    public static final String SQL_CREATE_TABLE_CATEGORIES =
            "CREATE TABLE IF NOT EXISTS " + DataBaseContract.Categories.TABLE_NAME + " (" +
                    DataBaseContract.Categories._ID + " INTEGER PRIMARY KEY," +
                    DataBaseContract.Categories.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    DataBaseContract.Categories.COLUMN_NAME_COLOR + TEXT_TYPE +
                    " )";

    public static final String SQL_CREATE_TABLE_NOTES_CATEGORIES =
            "CREATE TABLE IF NOT EXISTS " + DataBaseContract.NotesCategories.TABLE_NAME + " (" +
                    DataBaseContract.NotesCategories._ID + " INTEGER PRIMARY KEY," +
                    DataBaseContract.NotesCategories.COLUMN_NAME_NOTE_ID + TEXT_TYPE + COMMA_SEP +
                    DataBaseContract.NotesCategories.COLUMN_NAME_CATEGORY_ID + TEXT_TYPE +
                    " )";


    public static final String SQL_DELETE_TABLE_NOTES =
            "DROP TABLE IF EXISTS " + DataBaseContract.Notes.TABLE_NAME;

    public static final String SQL_DELETE_TABLE_CATEGORIES =
            "DROP TABLE IF EXISTS " + DataBaseContract.Categories.TABLE_NAME;

    public static final String SQL_DELETE_TABLE_NOTES_CATEGORIES =
            "DROP TABLE IF EXISTS " + DataBaseContract.NotesCategories.TABLE_NAME;


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_NOTES);
        db.execSQL(SQL_CREATE_TABLE_CATEGORIES);
        db.execSQL(SQL_CREATE_TABLE_NOTES_CATEGORIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE_NOTES);
        db.execSQL(SQL_DELETE_TABLE_CATEGORIES);
        db.execSQL(SQL_DELETE_TABLE_NOTES_CATEGORIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void dropDatabase() {
        this.getWritableDatabase().execSQL(SQL_DELETE_TABLE_NOTES);
        this.getWritableDatabase().execSQL(SQL_DELETE_TABLE_CATEGORIES);
        this.getWritableDatabase().execSQL(SQL_DELETE_TABLE_NOTES_CATEGORIES);

        this.getWritableDatabase().execSQL(SQL_CREATE_TABLE_NOTES);
        this.getWritableDatabase().execSQL(SQL_CREATE_TABLE_CATEGORIES);
        this.getWritableDatabase().execSQL(SQL_CREATE_TABLE_NOTES_CATEGORIES);
    }
}
