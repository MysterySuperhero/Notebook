package com.mysterysuperhero.notebook.utils;

import android.content.Context;
import android.database.Cursor;

import com.mysterysuperhero.notebook.database.DataBaseContract;

import java.util.ArrayList;

/**
 * Created by dmitri on 21.06.16.
 */
public class CategoriesGetter {
    public static void getCategories(Context context, ArrayList<String> names,
                                     ArrayList<Category> categories) {
        Cursor cursor = context.getContentResolver().query(DataBaseContract.Categories.CONTENT_URI, DataBaseContract.Categories.DEFAULT_PROJECTION,
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(cursor.getColumnIndex(DataBaseContract.Categories.COLUMN_NAME_NAME)));
                categories.add(new Category(
                        cursor.getString(cursor.getColumnIndex(DataBaseContract.Categories._ID)),
                        cursor.getString(cursor.getColumnIndex(DataBaseContract.Categories.COLUMN_NAME_NAME)),
                        cursor.getString(cursor.getColumnIndex(DataBaseContract.Categories.COLUMN_NAME_COLOR))
                ));
            } while (cursor.moveToNext());
        }
    }
}
