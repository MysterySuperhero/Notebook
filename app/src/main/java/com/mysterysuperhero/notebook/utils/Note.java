package com.mysterysuperhero.notebook.utils;

/**
 * Created by dmitri on 21.06.16.
 */
public class Note {
    private String name;
    private String text;
    private String color;

    public Note(String name, String text, String color) {
        this.name = name;
        this.text = text;
        this.color = color;
    }

    public String getName() {
        return this.name;
    }

    public String getText() {
        return this.text;
    }

    public String getColor() {
        return this.color;
    }
}
