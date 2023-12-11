package com.adretsoftwares.cellsecuritycare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;

public class SignupActivity extends AppCompatActivity {

    TextView loginText;
    private String token;
    EditText firstName,lastName,emailIdInput,PasswordInput,edtMobile,referalCodeEdt;
    Button btnSignUp,btnVerify;
    public static final String CHANNEL_ID = "cellsecurity_id";
    static final String CHANNEL_NAME = "cellsecurity name";
    static final String CHANNEL_DESC = "cellsecurity desc";
    ProgressDialog progressBar;
    static String SHARED_PREF_NAME = "com.adretsoftwares.cellsecuritycare";
    SharedPreferences preferences;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Please wait ....");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        emailIdInput = findViewById(R.id.emailIdInput);
        PasswordInput = findViewById(R.id.PasswordInput);
        edtMobile = findViewById(R.id.edtMobile);
        loginText=findViewById(R.id.loginText);
        btnSignUp = findViewById(R.id.btnSignUp);
        referalCodeEdt = findViewById(R.id.referalCode);
        btnVerify = findViewById(R.id.btnVerify);
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(referalCodeEdt.getText().toString()!=null&&!referalCodeEdt.getText().toString().trim().isEmpty()) {
                    verify(referalCodeEdt.getText().toString());
                }else {
                    referalCodeEdt.setError("Enter valid referral code");
                }
            }
        });
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();
                        // Log and toast
                        //Toast.makeText(SignupActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firstName.getText().toString().trim().isEmpty()||firstName.getText() == null||
                        lastName.getText().toString().trim().isEmpty() ||lastName.getText() == null||
                        emailIdInput.getText().toString().trim().isEmpty() ||emailIdInput.getText() == null||
                        PasswordInput.getText().toString().trim().isEmpty() ||PasswordInput.getText() == null||
                        edtMobile.getText().toString().trim().isEmpty() ||edtMobile.getText() == null){
                    if(firstName.getText().toString().trim().isEmpty()||firstName.getText() == null){
                        firstName.setError("Enter first name");
                    }else if(lastName.getText().toString().trim().isEmpty() ||lastName.getText() == null){
                        lastName.setError("Enter last name");
                    }else if(emailIdInput.getText().toString().trim().isEmpty() ||emailIdInput.getText() == null){
                        emailIdInput.setError("Enter email id");
                    }else if(PasswordInput.getText().toString().trim().isEmpty() ||PasswordInput.getText() == null){
                        PasswordInput.setError("Enter Password");
                    }else if(edtMobile.getText().toString().trim().isEmpty() ||edtMobile.getText() == null){
                        edtMobile.setError("Enter Mobile Number");
                    }
                }else {
                    LoginRegister(firstName.getText().toString().trim(),lastName.getText().toString().trim(),
                            emailIdInput.getText().toString().trim(),token,PasswordInput.getText().toString().trim(),
                            edtMobile.getText().toString().trim(),referalCodeEdt.getText().toString().trim());
                }
            }
        });


    }
    private void LoginRegister(final String firstName, final String lastName, final String emailId,final String token,
                               final String password,final String mobileNumber,final String newReferalCode) {
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

                    //if no error in response
                    if (obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("emailId", emailId);
                        editor.putString("name", firstName+" "+lastName);
                        editor.putString("mobile_number", mobileNumber);
                        editor.putBoolean("loggedIn", true);
                        editor.apply();

                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
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
                params.put("email", emailId);
                params.put("name", firstName+" "+lastName);
                params.put("password", password);
                params.put("token", token);
                params.put("mobile_number", mobileNumber);
                params.put("user_refer", newReferalCode);
                params.put("new_referal_code", String.valueOf(gen()));

                //returing the response
                return requestHandler.sendPostRequest("https://cellsecuritycare.com/api/index2.php?apicall=signup", params);
            }
        }
        Login ul = new Login();
        ul.execute();
    }

    private void verify(final String referalCode) {
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
                    if(obj.get("verified")!=null&&obj.get("verified").equals("Verified")){
                        btnVerify.setText("Verified");
                        btnVerify.setTextColor(Color.GREEN);
                    }else {
                        referalCodeEdt.setText("");
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
                params.put("referalCode", referalCode);


                //returing the response
                return requestHandler.sendPostRequest("https://cellsecuritycare.com/api/index2.php?apicall=verify", params);
            }
        }
        Login ul = new Login();
        ul.execute();
    }

    public int gen()
    {
        Random r = new Random( System.currentTimeMillis() );
        return ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
    }
}