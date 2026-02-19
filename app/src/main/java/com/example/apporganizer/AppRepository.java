package com.example.apporganizer;

import com.example.apporganizer.model.AppInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class AppRepository {

    private static AppRepository instance;

    private final Map<String, List<AppInfo>> byCategory = new HashMap<>();

    public static AppRepository getInstance() {
        if (instance == null) instance = new AppRepository();
        return instance;
    }

    public void setAppsByCategory(Map<String, List<AppInfo>> map) {
        byCategory.clear();
        byCategory.putAll(map);
    }

    public List<AppInfo> getAppsByCategory(String category) {
        List<AppInfo> list = byCategory.get(category);
        return list == null ? new ArrayList<>() : list;
    }
}
