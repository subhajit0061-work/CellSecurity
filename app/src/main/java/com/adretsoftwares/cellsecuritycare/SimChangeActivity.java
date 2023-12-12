package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.adretsoftwares.cellsecuritycare.entities.DateEntity;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class SimChangeActivity extends AppCompatActivity {
    TextView textView21, textActive;
    SwitchMaterial num1_switch;
    SharedPreferences preferences;
    private RecyclerView.LayoutManager layoutManager;
    private List<Notify> listSuperHeroes;
    private List<DateEntity> dateEntities;
    private RecyclerView.Adapter adapter;
    private RecyclerView.Adapter dateAdapter;
    private RecyclerView dateRecyclerView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sim_change);
        initRecyclerView();
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        textView21 = findViewById(R.id.num1_switch);
        textActive = findViewById(R.id.textView2);
        num1_switch = findViewById(R.id.num1_switch);
        if (preferences.contains("simchange") && preferences.getBoolean("simchange", false)) {
            textActive.setTextColor(Color.parseColor("#4CAF50"));
            textActive.setText("Activated");
            num1_switch.setChecked(true);
        }
//        textActive.setVisibility(View.GONE);
//        textView21.setVisibility(View.GONE);
        num1_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("boolean ", String.valueOf(b));
                if (b) {
                    textActive.setTextColor(Color.parseColor("#4CAF50"));
                    textActive.setText("Activated");
                    editor.putBoolean("simchange", true).commit();
                    editor.putBoolean("simChange", true);
                } else {
                    textActive.setTextColor(Color.parseColor("#000000"));
                    textActive.setText("Activate");
                    editor.putBoolean("simchange", false).commit();
                    editor.putBoolean("simChange", false);
                }

            }
        });
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewSim);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        dateRecyclerView = findViewById(R.id.date_recyclerViewSim);
        dateRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        dateRecyclerView.setLayoutManager(layoutManager);
        dateEntities = new ArrayList<>();
        //Initializing our superheroes list
        listSuperHeroes = new ArrayList<>();
        adapter = new CardAdapter(listSuperHeroes, this);
        dateAdapter = new DateAdapter(dateEntities);
        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);
        dateRecyclerView.setAdapter(dateAdapter);
    }
}
