package com.adretsoftwares.cellsecuritycare;

import static android.content.Context.MODE_PRIVATE;
import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PushNotification {
    SharedPreferences sharedPreferences ;
    public void push(String mobile,String message,Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        if(sharedPreferences.getString("emergencyMobile1","") !="" && sharedPreferences.getString("emergencyMobile1","") != null){
            LoginRegister(sharedPreferences.getString("emergencyMobile1",""),message,context);
        }
    }

    private void LoginRegister(final String mobile_number, final String message, Context context) {
        ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(context);
        progressDoalog.setMax(100);
        progressDoalog.setMessage("Its loading....");
        progressDoalog.setTitle("ProgressDialog bar example");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDoalog.show();

        class Login extends AsyncTask<Void, Void, String> {
            int count = 0;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDoalog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressDoalog.hide();
                try {
                    //converting response to json object
                    Log.e("Error ",s);
                    JSONObject obj = new JSONObject(s);
                    count ++;
                    if(count>0){
                        if(sharedPreferences.getString("emergencyMobile2","") !="" && sharedPreferences.getString("emergencyMobile2","") != null){
                            LoginRegister(sharedPreferences.getString("emergencyMobile2",""),message,context);
                        }
                    }else if(count>1){
                        if(sharedPreferences.getString("emergencyMobile3","") !="" && sharedPreferences.getString("emergencyMobile3","") != null){
                            LoginRegister(sharedPreferences.getString("emergencyMobile3",""),message,context);
                        }
                    }
                    //if no error in response
                    if (obj.getBoolean("error")) {
                        Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject user = obj.getJSONObject("user");
//                        SharedPreferences.Editor editor = preferences.edit();
//                        editor.putString("emailId", user.getString("email"));
//                        editor.putString("name", user.getString("name"));
//                        editor.putString("emergencyEmail1", user.getString("emergencyMobile1"));
//                        editor.putString("emergencyEmail2", user.getString("emergencyMobile2"));
//                        editor.putString("emergencyEmail3", user.getString("emergencyMobile3"));
//                        editor.putString("token", user.getString("token"));
//                        editor.putString("mobile_number", user.getString("mobile_number"));
//                        editor.apply();
//
//                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
//                Log.e("mobile_number",mobile_number);
//                Log.e("password",password);
//                Log.e("token",token);
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("mobile", mobile_number);
                params.put("message", message);

                // params.put("token", token);

                //returing the response
                return requestHandler.sendPostRequest("https://cellsecuritycare.com/api/index.php", params);
            }
        }
        Login ul = new Login();
        ul.execute();
    }
}
