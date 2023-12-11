package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ReferalReward extends AppCompatActivity {
    ProgressDialog progressBar;
    int reward;
    Button whatsapp;
    TextView textView20;
    SharedPreferences preferences;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referal_reward);
        textView20 = findViewById(R.id.textView20);
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        progressBar = new ProgressDialog(this);
        whatsapp = findViewById(R.id.whatsapp);
        progressBar.setCancelable(true);
        progressBar.setMessage("Please wait ....");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        verify(preferences.getString("mobile_number",""));
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void verify(final String number) {
        class Login extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.hide();
                try {
                    //converting response to json object
                    Log.e("Error ",s);
                    JSONObject obj = new JSONObject(s);
                    if(obj.get("error")!=null&&!obj.getBoolean("error")){
                        JSONObject user = obj.getJSONObject("user");
                        Log.e("count",user.toString());
                        reward = Integer.parseInt(user.getString("count"));
                        int money = reward*500;
                        String msg = "You have earned Rs."+money;
                        textView20.setText(msg);
                    }else {

                    }
                    //if no error in response
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("number", number);


                //returing the response
                return requestHandler.sendPostRequest("https://cellsecuritycare.com/api/index2.php?apicall=referalreward", params);
            }
        }
        Login ul = new Login();
        ul.execute();
    }
}