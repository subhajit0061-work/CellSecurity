package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.adretsoftwares.cellsecuritycare.helper.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReferralActivity extends AppCompatActivity {
    RecyclerView recycler;
    private ItemAdapter itemAdapter;
    List<Item> itemList = new ArrayList<>();
    ProgressDialog progressBar;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Please wait ....");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
//        verify("728834");
        FetchItemsTask fetchItemsTask = new FetchItemsTask(preferences.getString("new_referal_code","")); // Replace with the actual user ID
        fetchItemsTask.execute();
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
                    if(!obj.getBoolean("error")){
                        Log.e("inside1","");
                        JSONArray list = obj.getJSONArray("list");
                        Log.e("inside2","");
                        for (int i = 0; i < list.length(); i++) {
                            Log.e("inside3","");
                            JSONObject jsonObject = list.getJSONObject(i);
                            Log.e("pbject",jsonObject.toString());
                            String name = jsonObject.getString("message");
                            String description = "hello";
                            Item item = new Item(name, description);
                            itemList.add(item);
                        }
                        itemAdapter = new ItemAdapter(itemList, ReferralActivity.this);
                        recycler.setAdapter(itemAdapter);
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
                params.put("referal", referalCode);

                //returing the response
                return requestHandler.sendPostRequest("https://cellsecuritycare.com/api/index2.php?apicall=referallist", params);
            }
        }
        Login ul = new Login();
        ul.execute();
    }


    private class FetchItemsTask extends AsyncTask<Void, Void, List<Item>> {

        private String userId;

        public FetchItemsTask(String userId) {
            this.userId = userId;
        }

        @Override
        protected List<Item> doInBackground(Void... params) {
            List<Item> itemList = new ArrayList<>();

            try {
                URL url = new URL("https://cellsecuritycare.com/api/index2.php?apicall=referallist");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set the request method to POST
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Set parameters
                String postData = "referal=" + userId;
                byte[] postDataBytes = postData.getBytes("UTF-8");

                // Write the parameters to the connection's output stream
                connection.getOutputStream().write(postDataBytes);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                JSONObject obj = new JSONObject(response.toString());
                Log.e("objjj",obj.toString());
                JSONArray jsonArray = obj.getJSONArray("list");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Log.e("list",jsonObject.toString());
                    String name = jsonObject.getString("name");
                    String description = "";
                    Item item = new Item(name, description);
                    itemList.add(item);
                }

                reader.close();
                connection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return itemList;
        }

        @Override
        protected void onPostExecute(List<Item> itemList) {
            itemAdapter = new ItemAdapter(itemList, ReferralActivity.this);
            recycler.setAdapter(itemAdapter);
        }
    }

}