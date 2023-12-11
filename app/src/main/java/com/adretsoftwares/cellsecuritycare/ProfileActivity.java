package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.MainActivity.loadImage;
import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    TextView userName;
    private Bitmap selectedImageBitmap;
    EditText name,mobile,email,num1,num2,num3;
    Button btnSave;
    SharedPreferences sharedPreferences;
    ProgressDialog progressBar;
    SharedPreferences preferences;
    private ActivityResultLauncher<Intent> imageCaptureLauncher;
    private ActivityResultLauncher<Intent> imageGalleryLauncher;
    ImageView image;
    byte[] imageByte;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name = findViewById(R.id.name);
        image = findViewById(R.id.image);
        mobile = findViewById(R.id.mobile);
        email = findViewById(R.id.email);
        num1 = findViewById(R.id.num1);
        num2 = findViewById(R.id.num2);
        num3 = findViewById(R.id.num3);
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Updating profile ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        userName = findViewById(R.id.userName);
        btnSave = findViewById(R.id.btnSave);
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        userName.setText(sharedPreferences.getString("name", ""));
        name.setText(sharedPreferences.getString("name", ""));
        mobile.setText(sharedPreferences.getString("mobile_number", ""));
        email.setText(sharedPreferences.getString("emailId", ""));
        num1.setText(sharedPreferences.getString("emergencyEmail1", ""));
        num2.setText(sharedPreferences.getString("emergencyEmail2", ""));
        num3.setText(sharedPreferences.getString("emergencyEmail3", ""));

        byte[] imageData = loadImage(this);

        if (imageData != null) {
            // Convert the byte array to a Bitmap
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

            // Set the Bitmap to the ImageView
            if(imageBitmap!=null){
                image.setImageBitmap(imageBitmap);
            }
        } else {
            // Handle the case where no image data is found in SharedPreferences
            // You can set a default image or display an error message.
        }


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUpdate(mobile.getText().toString().trim(), name.getText().toString().trim(),
                        email.getText().toString().trim(), num1.getText().toString().trim(),
                        num2.getText().toString().trim(), num3.getText().toString().trim());
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageOptions(view);
            }
        });

        imageGalleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        handleImageActivityResult(result);
                    }
                });

        imageCaptureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        handleImageActivityResult(result);
                    }
                });
    }

    private void handleImageActivityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            // Image is selected or captured
            Intent data = result.getData();
            if (data != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                if (data.getExtras() != null && data.getExtras().containsKey("data")) {
                    selectedImageBitmap = (Bitmap) data.getExtras().get("data");
                    image.setImageBitmap(selectedImageBitmap);

                    // Convert the selected image to a byte array
                    imageByte = convertBitmapToByteArray(selectedImageBitmap);
                } else {
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        try {
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            image.setImageBitmap(selectedImageBitmap);
                            imageByte = convertBitmapToByteArray(selectedImageBitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void showImageOptions(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source")
                .setItems(new CharSequence[]{"Capture from Camera", "Choose from Gallery"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        captureImageFromCamera();
                                        break;
                                    case 1:
                                        selectImageFromGallery();
                                        break;
                                }
                            }
                        });
        builder.create().show();
    }
    private void captureImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageCaptureLauncher.launch(intent);
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imageGalleryLauncher.launch(intent);
    }
    private void LoginUpdate(final String mobile_number,final String name,final String emailId,
            final String emergencyMobile1,final String emergencyMobile2,final String emergencyMobile3) {
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
                        editor.putString("emailId",emailId);
                        editor.putString("name", name);
                        editor.putString("emergencyMobile1",emergencyMobile1);
                        editor.putString("emergencyMobile2", emergencyMobile2);
                        editor.putString("emergencyMobile3", emergencyMobile3);
                        editor.putString("mobile_number", mobile_number);
                        String encodedImage = Base64.encodeToString(imageByte, Base64.DEFAULT);
                        editor.putString("IMAGE_KEY", encodedImage);
                        editor.apply();

                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
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
                Log.e("mobile_number",mobile_number);
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("mobile_number", mobile_number);
                params.put("name", name);
                params.put("emailId", emailId);
                params.put("emergencyMobile1", emergencyMobile1);
                params.put("emergencyMobile2", emergencyMobile2);
                params.put("emergencyMobile3", emergencyMobile3);
                String encodedImage = Base64.encodeToString(imageByte, Base64.DEFAULT);
                params.put("imageData", encodedImage);
                // params.put("token", token);

                //returing the response
                return requestHandler.sendPostRequest("https://cellsecuritycare.com/api/index2.php?apicall=update", params);
            }
        }
        Login ul = new Login();
        ul.execute();
    }

//    private void LoginRegister(final String firstName, final String lastName, final String emailId,final String token,final String password) {
//        class Login extends AsyncTask<Void, Void, String> {
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                progressBar.show();
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                progressBar.hide();
//                try {
//                    //converting response to json object
//                    Log.e("Error ",s);
//                    JSONObject obj = new JSONObject(s);
//
//                    //if no error in response
//                    if (obj.getBoolean("error")) {
//                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
//                    } else {
//
//                        SharedPreferences.Editor editor = preferences.edit();
//                        editor.putString("name", firstName+" "+lastName);
////                        editor.putString("emergenyEmail1", emergenyEmail1);
////                        editor.putString("emergenyEmail12", emergenyEmail2);
////                        editor.putString("emergenyEmail1", emergenyEmail3);
//                        editor.apply();
//
//                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(SignupActivity.this, ProfileActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            protected String doInBackground(Void... voids) {
//                //creating request handler object
//                RequestHandler requestHandler = new RequestHandler();
//
//                //creating request parameters
//                HashMap<String, String> params = new HashMap<>();
//                params.put("email", emailId);
//                params.put("name", firstName+" "+lastName);
//                params.put("password", password);
//                params.put("token", token);
//
//                //returing the response
//                return requestHandler.sendPostRequest("http://192.168.1.108:80/cellapi/index.php", params);
//            }
//        }
//        Login ul = new Login();
//        ul.execute();
//    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}