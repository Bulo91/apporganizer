package com.example.apporganizer.logic;

public class CategoryResult {
    public final String category;
    public final int confidence;

    public CategoryResult(String category, int confidence) {
        this.category = category;
        this.confidence = confidence;
    }
}
