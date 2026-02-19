package com.example.apporganizer.model;

import android.graphics.drawable.Drawable;

public class AppInfo {
    private final String packageName;
    private final String label;
    private final Drawable icon;

    private String category;
    private int confidence;

    public AppInfo(String packageName, String label, Drawable icon) {
        this.packageName = packageName;
        this.label = label;
        this.icon = icon;
        this.category = "Autres";
        this.confidence = 0;
    }

    public String getPackageName() { return packageName; }
    public String getLabel() { return label; }
    public Drawable getIcon() { return icon; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getConfidence() { return confidence; }
    public void setConfidence(int confidence) { this.confidence = confidence; }
}
