package com.mobdeve.s21.mco.schedule_maker;

public class Themes {
    private String name;
    private int image;

    // Constructor
    public Themes(String name, int image) {
        this.name = name;
        this.image = image;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for image
    public int getImage() {
        return image;
    }

    // Setter for image
    public void setImage(int image) {
        this.image = image;
    }
}

