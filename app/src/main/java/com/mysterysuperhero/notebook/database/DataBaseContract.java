package com.mysterysuperhero.notebook.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by dmitri on 20.06.16.
 */
public class DataBaseContract {
    private DataBaseContract() {}
    public static final String AUTHORITY = "com.mysterysuperhero.notebook.database.DBContentProvider";
    public static final String SCHEME = "content://";

    public static abstract class Notes implements BaseColumns {

        public static final String TABLE_NAME = "notes";
        private static final String PATH_NOTES = "/notes";
        private static final String PATH_NOTES_ID = "/notes/";
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_NOTES);
        public static final String DEFAULT_SORT_ORDER = "_ID ASC";

        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_TEXT = "text";
        public static final String COLUMN_NAME_COLOR = "color";
        public static final String COLUMN_NAME_CATEGORY = "category";

        public static final String[] DEFAULT_PROJECTION = new String[] {
                Notes._ID,
                Notes.COLUMN_NAME_NAME,
                Notes.COLUMN_NAME_TEXT,
                Notes.COLUMN_NAME_COLOR,
                Notes.COLUMN_NAME_CATEGORY
        };
    }

    public static abstract class Categories implements BaseColumns {

        public static final String TABLE_NAME = "categories";
        private static final String PATH_CATEGORIES = "/categories";
        private static final String PATH_CATEGORIES_ID = "/categories/";
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_CATEGORIES);
        public static final String DEFAULT_SORT_ORDER = "_ID ASC";

        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_COLOR = "color";


        public static final String[] DEFAULT_PROJECTION = new String[] {
                Categories._ID,
                Categories.COLUMN_NAME_NAME,
                Categories.COLUMN_NAME_COLOR,
        };
    }


    public static abstract class NotesCategories implements BaseColumns {

        public static final String TABLE_NAME = "notes_categories";
        private static final String PATH_NOTES_CATEGORIES = "/notes_categories";
        private static final String PATH_NOTES_CATEGORIES_ID = "/notes_categories/";
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_NOTES_CATEGORIES);
        public static final String DEFAULT_SORT_ORDER = "lastName ASC";

        public static final String COLUMN_NAME_NOTE_ID = "note_id";
        public static final String COLUMN_NAME_CATEGORY_ID = "category_id";

        public static final String[] DEFAULT_PROJECTION = new String[] {
                NotesCategories._ID,
                NotesCategories.COLUMN_NAME_NOTE_ID,
                NotesCategories.COLUMN_NAME_CATEGORY_ID,
        };
    }

}
