package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
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

import java.util.Arrays;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    Button loginBtn;
    TextView signupTxt;
    SharedPreferences preferences;
    EditText userMobileNumber,userPassword;
    ProgressDialog progressBar;

    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loginBtn=findViewById(R.id.loginBtn);
        signupTxt=findViewById(R.id.signupText);
        userMobileNumber = findViewById(R.id.userMobileNumber);
        userPassword = findViewById(R.id.userPassword);
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Please wait ....");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        if(preferences.getBoolean("loggedIn",false)){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                Log.e("token ",token);
                                if(userMobileNumber.getText().toString().trim()!=null || userPassword.getText().toString() != ""){
                                    LoginRegister(userMobileNumber.getText().toString().trim(),userPassword.getText().toString(),token);
                                }
                                // Log and toast
                               // Toast.makeText(LoginActivity.this, token, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        signupTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();
            }
        });
    }

    private void LoginRegister(final String mobile_number,final String password,final String token) {
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
                        JSONObject user = obj.getJSONObject("user");
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("emailId", user.getString("email"));
                        editor.putString("name", user.getString("name"));
                        editor.putString("emergencyMobile1", user.getString("emergencyMobile1"));
                        editor.putString("emergencyMobile2", user.getString("emergencyMobile2"));
                        editor.putString("emergencyMobile3", user.getString("emergencyMobile3"));
                        editor.putString("token", user.getString("token"));
                        editor.putString("mobile_number", user.getString("mobile_number"));
                        editor.putString("new_referal_code", user.getString("new_referal_code"));
                        editor.putInt("subscribed", user.getInt("subscribed"));
                        String encodedImage= user.getString("imageData");
                        Log.e("encoded ",encodedImage);
                        editor.putString("IMAGE_KEY", encodedImage);
                        editor.putBoolean("issLoggedIn",true);
                        editor.apply();

                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
                Log.e("mobile_number",mobile_number);
                Log.e("password",password);
                Log.e("token",token);
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("mobile_number", mobile_number);
                params.put("password", password);
                params.put("token", token);

                //returing the response
                return requestHandler.sendPostRequest("https://cellsecuritycare.com/api/index2.php?apicall=login", params);
            }
        }
        Login ul = new Login();
        ul.execute();
    }
}