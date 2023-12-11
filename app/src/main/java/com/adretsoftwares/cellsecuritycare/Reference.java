package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Reference extends AppCompatActivity {
    String link = "https://www.cellsecurity.com/download/sdjdjf88";
    TextView refCode;
    String message;
    SharedPreferences preferences;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reference);
        refCode = findViewById(R.id.refCode);
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        RelativeLayout shareButton = findViewById(R.id.relativeLayout);
        refCode.setText(preferences.getString("new_referal_code",""));
        message = "Check out this link to download cellsecurity application,and use my code :"+preferences.getString("new_referal_code","")+" "+"link "+ link;
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, link);
                sendIntent.setType("text/plain");
                sendIntent.putExtra("message",message);
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });

        Button whatsapp = findViewById(R.id.whatsapp);
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                sendIntent.setPackage("com.whatsapp"); // Specify WhatsApp package name

                try {
                    startActivity(sendIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    // Handle case where WhatsApp is not installed
                    // You can display a message to the user or redirect them to the Play Store
                }
            }
        });
    }
}