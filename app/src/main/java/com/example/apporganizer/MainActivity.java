// ui/MainActivity.java
package com.example.apporganizer;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvApps = findViewById(R.id.rvApps);
        rvApps.setLayoutManager(new LinearLayoutManager(this));

        // Étape 2 : on branchera l'adapter avec la liste d'apps
    }
}
