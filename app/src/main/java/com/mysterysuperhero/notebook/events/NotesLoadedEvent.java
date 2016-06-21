package com.mysterysuperhero.notebook.events;

import android.database.Cursor;

/**
 * Created by dmitri on 21.06.16.
 */
public class NotesLoadedEvent {
    public Cursor cursor;

    public NotesLoadedEvent(Cursor cursor) {
        this.cursor = cursor;
    }
}
