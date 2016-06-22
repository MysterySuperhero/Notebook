package com.mysterysuperhero.notebook.events;

/**
 * Created by dmitri on 22.06.16.
 */
public class FilterChosenEvent {

    public String categoryId;

    public FilterChosenEvent(String categoryId) {
        this.categoryId = categoryId;
    }
}
