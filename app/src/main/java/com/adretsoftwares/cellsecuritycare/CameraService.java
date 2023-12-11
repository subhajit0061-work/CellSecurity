package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.CHANNEL_ID;
import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.adretsoftwares.cellsecuritycare.daos.DateDao;
import com.adretsoftwares.cellsecuritycare.database.CellSecurityDatabase;
import com.adretsoftwares.cellsecuritycare.entities.DateEntity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.Date;
import java.util.concurrent.Executors;

public class CameraService extends Service implements
        SurfaceHolder.Callback {

    // Camera variables
    // a surface holder
    // a variable to control the camera
    private Camera mCamera;
    // the camera parameters
    private android.hardware.Camera.Parameters parameters;
    private Bitmap bmp;
    FileOutputStream fo;
    String base64front="",base64second= "";
    private String FLASH_MODE;
    private int QUALITY_MODE = 0;
    private boolean isFrontCamRequest = false;
    private Camera.Size pictureSize;
    SharedPreferences preferences;
    SurfaceView sv;
    Context context;
    private SurfaceHolder sHolder;
    private WindowManager windowManager;
    WindowManager.LayoutParams params;
    public Intent cameraIntent;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int width = 0, height = 0;
    String mobile,message;
    byte[] frontBytee,secondBytee;
    boolean frontTake,backTake = false;
    /** Called when the activity is first created. */
    @Override
    public void onCreate() {
        super.onCreate();

    }

    private Camera openFrontFacingCameraGingerbread() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e("Camera",
                            "Camera failed to open: " + e.getLocalizedMessage());
                    /*
                     * Toast.makeText(getApplicationContext(),
                     * "Front Camera failed to open", Toast.LENGTH_LONG)
                     * .show();
                     */
                }
            }
        }
        return cam;
    }

    private void setBesttPictureResolution() {
        // get biggest picture size
        width = pref.getInt("Picture_Width", 0);
        height = pref.getInt("Picture_height", 0);

        if (width == 0 | height == 0) {
            pictureSize = getBiggesttPictureSize(parameters);
            if (pictureSize != null)
                parameters
                        .setPictureSize(pictureSize.width, pictureSize.height);
            // save width and height in sharedprefrences
            width = pictureSize.width;
            height = pictureSize.height;
            editor.putInt("Picture_Width", width);
            editor.putInt("Picture_height", height);
            editor.commit();

        } else {
            // if (pictureSize != null)
            parameters.setPictureSize(width, height);
        }
    }

    private Camera.Size getBiggesttPictureSize(Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPictureSizes()) {
            if (result == null) {
                result = size;
            } else {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;

                if (newArea > resultArea) {
                    result = size;
                }
            }
        }

        return (result);
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        // this device has a camera
        // no camera on this device
        return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    /** Check if this device has front camera */
    private boolean checkFrontCamera(Context context) {
        // this device has front camera
        // no front camera on this device
        return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FRONT);
    }

    Handler handler = new Handler();

    private class TakeImage extends AsyncTask<Intent, Void, Void> {

        @Override
        protected Void doInBackground(Intent... params) {
            takeImage(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
//            AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//            mgr.setStreamMute(AudioManager.STREAM_SYSTEM, false);
        }
    }

    private synchronized void takeImage(Intent intent) {

        if (checkCameraHardware(getApplicationContext())) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String flash_mode = extras.getString("FLASH");
                FLASH_MODE = flash_mode;

                boolean front_cam_req = extras.getBoolean("Front_Request");
                isFrontCamRequest = front_cam_req;

                int quality_mode = extras.getInt("Quality_Mode");
                QUALITY_MODE = quality_mode;
            }

            if (isFrontCamRequest) {

                // set flash 0ff
                FLASH_MODE = "off";
                // only for gingerbread and newer versions
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {

                    mCamera = openFrontFacingCameraGingerbread();
                    if (mCamera != null) {

                        try {
                            mCamera.setPreviewDisplay(sv.getHolder());
                        } catch (IOException e) {
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "API dosen't support front camera",
                                            Toast.LENGTH_LONG).show();
                                }
                            });

                            stopSelf();
                        }
                        Camera.Parameters parameters = mCamera.getParameters();
                        pictureSize = getBiggesttPictureSize(parameters);
                        if (pictureSize != null)
                            parameters
                                    .setPictureSize(pictureSize.width, pictureSize.height);

                        // set camera parameters
                        mCamera.setParameters(parameters);
                        mCamera.startPreview();
                        mCamera.enableShutterSound(false);
                        mCamera.takePicture(null, null, mCall);
                        AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                        mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);
                        frontTake = true;
                        // return 4;

                    } else {
                        mCamera = null;
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Your Device dosen't have Front Camera !",
                                        Toast.LENGTH_LONG).show();
                            }
                        });

                        stopSelf();
                    }
                    /*
                     * sHolder = sv.getHolder(); // tells Android that this
                     * surface will have its data // constantly // replaced if
                     * (Build.VERSION.SDK_INT < 11)
                     *
                     * sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
                     */
                } else {
                    if (checkFrontCamera(getApplicationContext())) {
                        mCamera = openFrontFacingCameraGingerbread();

                        if (mCamera != null) {

                            try {
                                mCamera.setPreviewDisplay(sv.getHolder());
                            } catch (IOException e) {
                                handler.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "API dosen't support front camera",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });

                                stopSelf();
                            }
                            Camera.Parameters parameters = mCamera.getParameters();
                            pictureSize = getBiggesttPictureSize(parameters);
                            if (pictureSize != null)
                                parameters
                                        .setPictureSize(pictureSize.width, pictureSize.height);

                            // set camera parameters
                            mCamera.setParameters(parameters);
                            mCamera.startPreview();
                            mCamera.enableShutterSound(false);
                            mCamera.takePicture(null, null, mCall);
                            AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                            mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);
                            frontTake = true;
                            // return 4;

                        } else {
                            mCamera = null;
                            /*
                             * Toast.makeText(getApplicationContext(),
                             * "API dosen't support front camera",
                             * Toast.LENGTH_LONG).show();
                             */
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Your Device dosen't have Front Camera !",
                                            Toast.LENGTH_LONG).show();

                                }
                            });

                            stopSelf();

                        }
                        // Get a surface
                        /*
                         * sHolder = sv.getHolder(); // tells Android that this
                         * surface will have its data // constantly // replaced
                         * if (Build.VERSION.SDK_INT < 11)
                         *
                         * sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS
                         * );
                         */
                    }

                }
            }
        } else {
            // display in long period of time
            /*
             * Toast.makeText(getApplicationContext(),
             * "Your Device dosen't have a Camera !", Toast.LENGTH_LONG)
             * .show();
             */
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Your Device dosen't have a Camera !",
                            Toast.LENGTH_LONG).show();
                }
            });
            stopSelf();
        }

        // return super.onStartCommand(intent, flags, startId);

    }

    private void callSecondary(){
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = Camera.open();
            } else
                mCamera = getCameraInstance();

            try {
                if (mCamera != null) {
                    mCamera.setPreviewDisplay(sv.getHolder());
                    parameters = mCamera.getParameters();
                    if (FLASH_MODE == null || FLASH_MODE.isEmpty()) {
                        FLASH_MODE = "auto";
                    }
                    parameters.setFlashMode(FLASH_MODE);
                    // set biggest picture
                    setBesttPictureResolution();
                    // log quality and image format
                    Log.d("Qaulity", parameters.getJpegQuality() + "");
                    Log.d("Format", parameters.getPictureFormat() + "");

                    // set camera parameters
                    mCamera.setParameters(parameters);
                    mCamera.startPreview();
                    Log.d("ImageTakin", "OnTake()");
                    mCamera.takePicture(null, null, mCall);
                    mCamera.enableShutterSound(false);
                    AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                    mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);
                    backTake = true;
                } else {
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Camera is unavailable !",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    stopSelf();

                }
                // return 4;

            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("TAG", "CmaraHeadService()::takePicture", e);
            }
            // Get a surface
            /*
             * sHolder = sv.getHolder(); // tells Android that this surface
             * will have its data constantly // replaced if
             * (Build.VERSION.SDK_INT < 11)
             *
             * sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
             */

    }

    @SuppressWarnings("deprecation")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // sv = new SurfaceView(getApplicationContext());
        cameraIntent = intent;
        mobile = cameraIntent.getStringExtra("mobile");
        message = cameraIntent.getStringExtra("message");
        context = this;
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("Camera Service")
                .setSmallIcon(R.drawable.applogo)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        Log.d("ImageTakin", "StartCommand()");
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

//        params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_PHONE,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSLUCENT);
//
//        params.gravity = Gravity.TOP | Gravity.LEFT;
//        params.width = 1;
//        params.height = 1;
//        params.x = 0;
//        params.y = 0;

        ViewGroup.LayoutParams layoutParams = new WindowManager.LayoutParams(1, 1,
                Build.VERSION.SDK_INT < Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY :
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
//        windowManager.addView(surfaceView, layoutParams);
        sv = new SurfaceView(getApplicationContext());

        windowManager.addView(sv, layoutParams);
        sHolder = sv.getHolder();
        sHolder.addCallback(this);

        // tells Android that this surface will have its data constantly
        // replaced
        if (Build.VERSION.SDK_INT < 11)
            sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        dateTimeDbInsertion();

        return Service.START_STICKY;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_NONE
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void dateTimeDbInsertion(){
        DateEntity entity = new DateEntity();

        entity.setYourDate(new Date(System.currentTimeMillis())); // Set the date
        entity.setYourTime(new Time(System.currentTimeMillis())); // Set the time
        CellSecurityDatabase cellSecurityDatabase = CellSecurityDatabase.getInstance(context.getApplicationContext());
        DateDao dateDao = cellSecurityDatabase.dateDao();
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                // Perform your insert operation here
                dateDao.insert(entity);
            }
        });

    }

    Camera.PictureCallback mCall = new Camera.PictureCallback() {


        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // decode the data obtained by the camera into a Bitmap
            Log.d("ImageTakin", "Done");


            if (bmp != null)
                bmp.recycle();
            System.gc();
            bmp = decodeBitmap(data);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            if (bmp != null && QUALITY_MODE == 0)
                bmp.compress(Bitmap.CompressFormat.JPEG, 30, bytes);
            else if (bmp != null && QUALITY_MODE != 0)
                bmp.compress(Bitmap.CompressFormat.JPEG, 30, bytes);
            if(frontTake&&!backTake){
                frontBytee = bytes .toByteArray();
                base64front = Base64.encodeToString(frontBytee, Base64.DEFAULT);
                callSecondary();
                return;
            }else if(backTake){
                secondBytee = bytes .toByteArray();
                base64second = Base64.encodeToString(secondBytee, Base64.DEFAULT);
            }
        //    byte[] byteArray =
            if(frontTake&&backTake) {
                Log.d("ImageTakin", base64front);
                Log.d("ImageTakin2", base64second);
                NotifyandSave.Companion.LoginRegister(context, mobile, message, base64front, base64second);
                if (preferences.getString("emergencyMobile1", "") != null && preferences.getBoolean("is1Enable", false)) {
                    NotifyandSave.Companion.LoginRegister(context, preferences.getString("emergencyMobile1", ""), message, base64front, base64second);
                }

                if (preferences.getString("emergencyMobile2", "") != null && preferences.getBoolean("is2Enable", false)) {
                    NotifyandSave.Companion.LoginRegister(context, preferences.getString("emergencyMobile2", ""), message, base64front, base64second);
                }

                if (preferences.getString("emergencyMobile3", "") != null && preferences.getBoolean("is3Enable", false)) {
                    NotifyandSave.Companion.LoginRegister(context, preferences.getString("emergencyMobile3", ""), message, base64front, base64second);
                }
            }
//            File imagesFolder = new File(
//                    Environment.getExternalStorageDirectory(), "MYGALLERY");
//            if (!imagesFolder.exists())
//                imagesFolder.mkdirs(); // <----
//            File image = new File(imagesFolder, System.currentTimeMillis()
//                    + ".jpg");
//
//            // write the bytes in file
//            try {
//                fo = new FileOutputStream(image);
//            } catch (FileNotFoundException e) {
//                Log.e("TAG", "FileNotFoundException", e);
//                // TODO Auto-generated catch block
//            }
//            try {
//                fo.write(bytes.toByteArray());
//            } catch (IOException e) {
//                Log.e("TAG", "fo.write::PictureTaken", e);
//                // TODO Auto-generated catch block
//            }
//
//            // remember close de FileOutput
//            try {
//                fo.close();
//                if (Build.VERSION.SDK_INT < 19)
//                    sendBroadcast(new Intent(
//                            Intent.ACTION_MEDIA_MOUNTED,
//                            Uri.parse("file://"
//                                    + Environment.getExternalStorageDirectory())));
//                else {
//                    MediaScannerConnection
//                            .scanFile(
//                                    getApplicationContext(),
//                                    new String[] { image.toString() },
//                                    null,
//                                    new MediaScannerConnection.OnScanCompletedListener() {
//                                        public void onScanCompleted(
//                                                String path, Uri uri) {
//                                            Log.i("ExternalStorage", "Scanned "
//                                                    + path + ":");
//                                            Log.i("ExternalStorage", "-> uri="
//                                                    + uri);
//                                        }
//                                    });
//                }
//
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
            /*
             * Toast.makeText(getApplicationContext(),
             * "Your Picture has been taken !", Toast.LENGTH_LONG).show();
             */
            //  com.integreight.onesheeld.Log.d("Camera", "Image Taken !");
            if (bmp != null) {
                bmp.recycle();
                bmp = null;
                System.gc();
            }
            mCamera = null;
            handler.post(new Runnable() {

                @Override
                public void run() {
//                    Toast.makeText(getApplicationContext(),
//                                    "Your Picture has been taken !", Toast.LENGTH_SHORT)
//                            .show();
                }
            });
            if(frontTake&&backTake) {
                stopSelf();
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onDestroy() {
        AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mgr.setStreamMute(AudioManager.STREAM_SYSTEM, false);
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (sv != null)
            windowManager.removeView(sv);
        Intent intent = new Intent("custom-event-name");
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        super.onDestroy();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (cameraIntent != null)
            new TakeImage().execute(cameraIntent);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public static Bitmap decodeBitmap(byte[] data) {

        Bitmap bitmap = null;
        BitmapFactory.Options bfOptions = new BitmapFactory.Options();
        bfOptions.inDither = false; // Disable Dithering mode
        bfOptions.inPurgeable = true; // Tell to gc that whether it needs free
        // memory, the Bitmap can be cleared
        bfOptions.inInputShareable = true; // Which kind of reference will be
        // used to recover the Bitmap data
        // after being clear, when it will
        // be used in the future
        bfOptions.inTempStorage = new byte[32 * 1024];

        if (data != null)
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                    bfOptions);

        return bitmap;
    }

}