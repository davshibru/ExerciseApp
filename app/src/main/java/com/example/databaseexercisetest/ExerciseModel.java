package com.example.databaseexercisetest;

public class ExerciseModel {

    private int id;
    private String name;
    private String description;
    private byte[] image;

    public ExerciseModel(int id, String name, String description, byte[] image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public byte[] getImage() {
        return image;
    }

    public int getId() {
        return id;
    }
}
