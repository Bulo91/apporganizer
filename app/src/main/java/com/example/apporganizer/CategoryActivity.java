package com.example.apporganizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apporganizer.model.AppInfo;
import com.example.apporganizer.ui.AppsAdapter;
import com.example.apporganizer.ui.OnAppClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoryActivity extends AppCompatActivity implements OnAppClickListener {

    public static final String EXTRA_CATEGORY = "category";

    private TextView txtTitle;
    private SearchView searchViewCategory;
    private RecyclerView rvCategoryApps;

    private AppsAdapter adapter;

    private final List<AppInfo> all = new ArrayList<>();
    private final List<AppInfo> filtered = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        txtTitle = findViewById(R.id.txtTitle);
        searchViewCategory = findViewById(R.id.searchViewCategory);
        rvCategoryApps = findViewById(R.id.rvCategoryApps);

        String category = getIntent().getStringExtra(EXTRA_CATEGORY);
        if (category == null) category = "Autres";

        txtTitle.setText("Catégorie : " + category);

        rvCategoryApps.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppsAdapter(this);
        rvCategoryApps.setAdapter(adapter);

        // On récupère la liste envoyée depuis MainActivity (via Singleton simple)
        List<AppInfo> source = AppRepository.getInstance().getAppsByCategory(category);
        all.clear();
        all.addAll(source);

        filtered.clear();
        filtered.addAll(all);
        adapter.setItems(filtered);

        setupSearch();
    }

    private void setupSearch() {
        searchViewCategory.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String query) {
        String q = (query == null) ? "" : query.trim().toLowerCase(Locale.FRENCH);

        filtered.clear();
        if (q.isEmpty()) {
            filtered.addAll(all);
        } else {
            for (AppInfo app : all) {
                if (app.getLabel().toLowerCase(Locale.FRENCH).contains(q)) {
                    filtered.add(app);
                }
            }
        }
        adapter.setItems(filtered);
    }

    @Override
    public void onAppClick(AppInfo app) {
        // on réutilise la logique de MainActivity (tu as déjà cette méthode)
        startActivity(getPackageManager().getLaunchIntentForPackage(app.getPackageName()));
    }
}
