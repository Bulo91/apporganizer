package com.example.apporganizer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apporganizer.model.AppInfo;
import com.example.apporganizer.ui.AppsAdapter;
import com.example.apporganizer.ui.OnAppClickListener;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import android.content.pm.ApplicationInfo;
import com.example.apporganizer.data.AppStateDao;
import com.example.apporganizer.logic.CategoryGuesser;
import com.example.apporganizer.logic.CategoryResult;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnAppClickListener {

    private RecyclerView rvApps;
    private SearchView searchView;

    private AppsAdapter adapter;
    private AppStateDao appStateDao;
    private final List<AppInfo> allApps = new ArrayList<>();
    private final List<AppInfo> filteredApps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appStateDao = new AppStateDao(this);
        rvApps = findViewById(R.id.rvApps);
        searchView = findViewById(R.id.searchView); // OK si ton XML utilise androidx.appcompat.widget.SearchView

        rvApps.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppsAdapter(this);
        rvApps.setAdapter(adapter);

        loadInstalledApps();
        setupSearch();
    }

    private void loadInstalledApps() {
        allApps.clear();

        PackageManager pm = getPackageManager();

        // On charge les catégories sauvegardées
        Map<String, AppStateDao.StoredState> saved = appStateDao.getAllStates();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);

        for (ResolveInfo ri : resolveInfos) {
            String packageName = ri.activityInfo.packageName;
            String label = ri.loadLabel(pm).toString();

            AppInfo app = new AppInfo(packageName, label, ri.loadIcon(pm));

            // Si déjà en base : on réutilise
            AppStateDao.StoredState state = saved.get(packageName);
            if (state != null) {
                app.setCategory(state.category);
                app.setConfidence(state.confidence);
            } else {
                // Sinon : tri intelligent + sauvegarde
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

        // Tri alphabétique FR
        final Collator collator = Collator.getInstance(Locale.FRENCH);
        Collections.sort(allApps, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo a1, AppInfo a2) {
                return collator.compare(a1.getLabel(), a2.getLabel());
            }
        });

        filteredApps.clear();
        filteredApps.addAll(allApps);
        adapter.setItems(filteredApps);
    }


    private void setupSearch() {
        if (searchView == null) return;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterApps(newText);
                return true;
            }
        });
    }

    private void filterApps(String query) {
        String q = (query == null) ? "" : query.trim().toLowerCase(Locale.FRENCH);

        filteredApps.clear();

        if (q.isEmpty()) {
            filteredApps.addAll(allApps);
        } else {
            for (AppInfo app : allApps) {
                if (app.getLabel().toLowerCase(Locale.FRENCH).contains(q)) {
                    filteredApps.add(app);
                }
            }
        }

        adapter.setItems(filteredApps);
    }

    @Override
    public void onAppClick(AppInfo app) {
        PackageManager pm = getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(app.getPackageName());

        if (launchIntent != null) {
            startActivity(launchIntent);
        }
    }
}
