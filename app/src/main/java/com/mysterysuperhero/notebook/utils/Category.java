package com.mysterysuperhero.notebook.utils;

/**
 * Created by dmitri on 21.06.16.
 */
public class Category {
    private String id;
    private String name;
    private String color;

    public Category(String id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
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

    public void setColor(String color) {
        this.color = color;
    }

}
