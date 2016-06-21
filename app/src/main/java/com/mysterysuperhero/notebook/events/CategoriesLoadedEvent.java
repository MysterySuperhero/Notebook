package com.mysterysuperhero.notebook.events;

import android.database.Cursor;

/**
 * Created by dmitri on 21.06.16.
 */
public class CategoriesLoadedEvent {
    public Cursor cursor;

    public CategoriesLoadedEvent(Cursor cursor) {
        this.cursor = cursor;
    }
}
