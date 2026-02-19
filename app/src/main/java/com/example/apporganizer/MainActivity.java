package com.example.apporganizer;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apporganizer.data.AppStateDao;
import com.example.apporganizer.logic.CategoryGuesser;
import com.example.apporganizer.logic.CategoryResult;
import com.example.apporganizer.model.AppInfo;
import com.example.apporganizer.model.CategoryItem;
import com.example.apporganizer.ui.CategoriesAdapter;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvApps;
    private SearchView searchView;

    private CategoriesAdapter categoriesAdapter;

    private AppStateDao appStateDao;

    private final List<AppInfo> allApps = new ArrayList<>();

    // catégories calculées (filtrables)
    private final List<CategoryItem> allCategories = new ArrayList<>();
    private final List<CategoryItem> filteredCategories = new ArrayList<>();

    // repo mémoire pour CategoryActivity
    private Map<String, List<AppInfo>> appsByCategory = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvApps = findViewById(R.id.rvApps);
        searchView = findViewById(R.id.searchView); // android.widget.SearchView

        appStateDao = new AppStateDao(this);

        rvApps.setLayoutManager(new LinearLayoutManager(this));

        categoriesAdapter = new CategoriesAdapter(category -> {
            Intent i = new Intent(MainActivity.this, CategoryActivity.class);
            i.putExtra(CategoryActivity.EXTRA_CATEGORY, category);
            startActivity(i);
        });

        rvApps.setAdapter(categoriesAdapter);

        loadInstalledAppsAndBuildCategories();
        setupSearch();
    }

    private void loadInstalledAppsAndBuildCategories() {
        allApps.clear();

        PackageManager pm = getPackageManager();

        // charge les catégories déjà sauvées
        Map<String, AppStateDao.StoredState> saved = appStateDao.getAllStates();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);

        for (ResolveInfo ri : resolveInfos) {
            String packageName = ri.activityInfo.packageName;
            String label = ri.loadLabel(pm).toString();

            AppInfo app = new AppInfo(packageName, label, ri.loadIcon(pm));

            // state SQLite ?
            AppStateDao.StoredState state = saved.get(packageName);
            if (state != null) {
                app.setCategory(state.category);
                app.setConfidence(state.confidence);
            } else {
                ApplicationInfo appInfo = null;
                try {
                    appInfo = pm.getApplicationInfo(packageName, 0);
                } catch (PackageManager.NameNotFoundException ignored) { }

                CategoryResult res = CategoryGuesser.guess(label, packageName, appInfo);
                app.setCategory(res.category);
                app.setConfidence(res.confidence);

                appStateDao.upsertState(packageName, res.category, res.confidence);
            }

            allApps.add(app);
        }

        // Tri alphabétique apps (utile pour l'écran catégorie)
        final Collator collator = Collator.getInstance(Locale.FRENCH);
        Collections.sort(allApps, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo a1, AppInfo a2) {
                return collator.compare(a1.getLabel(), a2.getLabel());
            }
        });

        // Regrouper par catégorie
        appsByCategory = new HashMap<>();
        for (AppInfo app : allApps) {
            String cat = app.getCategory();
            List<AppInfo> list = appsByCategory.get(cat);
            if (list == null) {
                list = new ArrayList<>();
                appsByCategory.put(cat, list);
            }
            list.add(app);
        }

        // Stocker dans le repo mémoire pour CategoryActivity
        AppRepository.getInstance().setAppsByCategory(appsByCategory);

        // Construire la liste des catégories
        allCategories.clear();
        for (Map.Entry<String, List<AppInfo>> e : appsByCategory.entrySet()) {
            allCategories.add(new CategoryItem(e.getKey(), e.getValue().size()));
        }

        // Trier les catégories
        Collections.sort(allCategories, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        // Affichage initial = toutes
        filteredCategories.clear();
        filteredCategories.addAll(allCategories);
        categoriesAdapter.setItems(filteredCategories);
    }

    private void setupSearch() {
        if (searchView == null) return;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCategories(newText);
                return true;
            }
        });
    }

    private void filterCategories(String query) {
        String q = (query == null) ? "" : query.trim().toLowerCase(Locale.FRENCH);

        filteredCategories.clear();

        if (q.isEmpty()) {
            filteredCategories.addAll(allCategories);
        } else {
            for (CategoryItem c : allCategories) {
                if (c.getName().toLowerCase(Locale.FRENCH).contains(q)) {
                    filteredCategories.add(c);
                }
            }
        }

        categoriesAdapter.setItems(filteredCategories);
    }
}
