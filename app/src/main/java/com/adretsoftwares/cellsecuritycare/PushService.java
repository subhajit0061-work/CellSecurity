package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PushService extends Service {
    ProgressDialog progressBar;
    SharedPreferences preferences;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String number = intent.getStringExtra("number");
        String message =  intent.getStringExtra("message");
        String coordinates = intent.getStringExtra("coordinates");
        LoginRegister(this,number,message,coordinates);
        return super.onStartCommand(intent, flags, startId);
    }

    private void LoginRegister(final Context context, final String mobile_number, final String message, final String coordinates) {
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
                        editor.apply();

                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, MainActivity.class);
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

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("mobile", mobile_number);
                params.put("coordinates", coordinates);
                params.put("message", message);
                // params.put("token", token);

                //returning the response
                return requestHandler.sendPostRequest("https://cellsecuritycare.com/index.php", params);
            }
        }
        Login ul = new Login();
        ul.execute();
    }
}
