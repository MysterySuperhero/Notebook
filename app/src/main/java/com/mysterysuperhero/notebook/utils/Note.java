package com.mysterysuperhero.notebook.utils;

/**
 * Created by dmitri on 21.06.16.
 */
public class Note {

    private String id;
    private String name;
    private String text;
    private String color;

    public Note(String id, String name, String text, String color) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.color = color;
    }

    public String getId() {
        return this.id;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
