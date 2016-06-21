package com.mysterysuperhero.notebook.utils;

import android.content.Context;
import android.database.Cursor;

import com.mysterysuperhero.notebook.database.DataBaseContract;

import java.util.ArrayList;

/**
 * Created by dmitri on 21.06.16.
 */
public class CategoriesGetter {
    public static ArrayList<String> getCategories(Context context) {
        ArrayList<String> categories = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(DataBaseContract.Categories.CONTENT_URI, DataBaseContract.Categories.DEFAULT_PROJECTION,
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(cursor.getColumnIndex(DataBaseContract.Categories.COLUMN_NAME_NAME)));
            } while (cursor.moveToNext());
        }

        return categories;
    }
}
