package com.example.apporganizer.model;

public class CategoryItem {
    private final String name;
    private final int count;

    public CategoryItem(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() { return name; }
    public int getCount() { return count; }
}
