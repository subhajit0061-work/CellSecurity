package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ContactMenu extends AppCompatActivity {
    SharedPreferences preferences;
    Toolbar toolbar; //Creating toolbar instance
    EditText emNu1,emNu2,emNu3,emNu4,emNu5,emNa1,emNa2,emNa3,emNa4,emNa5;
    Button btnSave;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_menu); //This activity linked to "activity_contacts.xml" - design of activity. ("AndroidManifest.xml" updated with second activity")
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        btnSave = (Button)findViewById(R.id.btnSave);
        emNu1 = (EditText)findViewById(R.id.emNu1);
        emNu2 = (EditText)findViewById(R.id.emNu2);
        emNu3 = (EditText)findViewById(R.id.emNu3);
        emNu4 = (EditText)findViewById(R.id.emNu4);
        emNu5 = (EditText)findViewById(R.id.emNu5);
        emNa1 = (EditText)findViewById(R.id.emNa1);
        emNa2 = (EditText)findViewById(R.id.emNa2);
        emNa3 = (EditText)findViewById(R.id.emNa3);
        emNa4 = (EditText)findViewById(R.id.emNa4);
        emNa5 = (EditText)findViewById(R.id.emNa5);

        emNa1.setText(preferences.getString("emNa1",""));
        emNa2.setText(preferences.getString("emNa2",""));
        emNa3.setText(preferences.getString("emNa3",""));
        emNa4.setText(preferences.getString("emNa4",""));
        emNa5.setText(preferences.getString("emNa5",""));
        emNu1.setText(preferences.getString("emNu1",""));
        emNu2.setText(preferences.getString("emNu2",""));
        emNu3.setText(preferences.getString("emNu3",""));
        emNu4.setText(preferences.getString("emNu4",""));
        emNu5.setText(preferences.getString("emNu5",""));
        //Toolbar
        toolbar = findViewById(R.id.toolbar); //Linking variable to toolbar object in "activity_main.xml" i.e. app
        setSupportActionBar(toolbar); //Setting it as preferred action bar
        getSupportActionBar().setTitle("SOS Emergency Safety System"); //Setting the title
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("emNa1",emNa1.getText().toString()).commit();
                editor.putString("emNa2",emNa2.getText().toString()).commit();
                editor.putString("emNa3",emNa3.getText().toString()).commit();
                editor.putString("emNa4",emNa4.getText().toString()).commit();
                editor.putString("emNa5",emNa5.getText().toString()).commit();
                editor.putString("emNu1",emNu1.getText().toString()).commit();
                editor.putString("emNu2",emNu2.getText().toString()).commit();
                editor.putString("emNu3",emNu3.getText().toString()).commit();
                editor.putString("emNu4",emNu4.getText().toString()).commit();
                editor.putString("emNu5",emNu5.getText().toString()).commit();
                Toast.makeText(view.getContext(),"Emergency Contacts Updated!",Toast.LENGTH_LONG).show();
            }
        });

    }

}