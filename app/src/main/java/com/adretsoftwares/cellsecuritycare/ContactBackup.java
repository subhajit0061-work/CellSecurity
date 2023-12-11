package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ContactBackup extends AppCompatActivity {
    private static final int REQUEST_READ_CONTACTS = 1;
    private Button btnBackupContacts;
    SharedPreferences sharedPreferences;
    private static final String API_URL = "https://cellsecuritycare.com/api/index2.php?apicall=synchcontact";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_backup);
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        btnBackupContacts = findViewById(R.id.backup);
        btnBackupContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    requestReadContactsPermission();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    private void requestReadContactsPermission() throws IOException {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_CONTACTS);
        } else {
            backupContacts();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    backupContacts();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void backupContacts() throws IOException {

        ArrayList<String> contactsList = new ArrayList<>();
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contactsList.add(name + "," + phoneNumber);
            } while (cursor.moveToNext());
            cursor.close();

            // Save contacts to a file (you can customize the file format)
            saveContactsToFile(contactsList);
        } else {
            Toast.makeText(this, "No contacts found", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveContactsToFile(ArrayList<String> contactsList) throws IOException {
//        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"con", "ContactBackup");
//        if (!dir.exists()) {
//            dir.mkdir();
//        }
//        File file = new File(dir, "contacts_backup.csv");
        String mobile = sharedPreferences.getString("mobile_number","");
        Gson gson = new Gson();
        String contactsJson = gson.toJson(contactsList);
        JSONObject data = new JSONObject();
        try {
            data.put("contacts",contactsJson);
            data.put("mobile",mobile);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        LoginUpdate(contactsJson,mobile);
//        File appSpecificInternalStorageDirectory = this.getFilesDir();
//        File file = new File(appSpecificInternalStorageDirectory, "contact.csv");
//        file.createNewFile();
//
//        try {
//            FileWriter writer = new FileWriter(file);
//            for (String contact : contactsList) {
//                writer.write(contact + "\n");
//            }
//            writer.flush();
//            writer.close();
//            Toast.makeText(this, "Contacts backup saved", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e("contact ",e.getLocalizedMessage());
//            Toast.makeText(this, "Error saving contacts backup", Toast.LENGTH_SHORT).show();
//        }
    }

    private void LoginUpdate(final String contacts,final String mobile) {
        class Login extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    //converting response to json object
                    Log.e("Error ",s);
                    JSONObject obj = new JSONObject(s);
                    Toast.makeText(getApplicationContext(),s    , Toast.LENGTH_SHORT).show();
                    //if no error in response
                    if (obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject user = obj.getJSONObject("user");


                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ContactBackup.this, MainActivity.class);
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
                params.put("mobile", mobile);
                params.put("contacts", contacts);
                // params.put("token", token);

                //returing the response
                return requestHandler.sendPostRequest(API_URL, params);
            }
        }
        Login ul = new Login();
        ul.execute();
    }
}