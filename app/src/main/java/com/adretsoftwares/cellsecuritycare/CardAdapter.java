package com.adretsoftwares.cellsecuritycare;


import static com.adretsoftwares.cellsecuritycare.util.LocationService.lattitude;
import static com.adretsoftwares.cellsecuritycare.util.LocationService.longitude;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.adretsoftwares.cellsecuritycare.util.DownloadImage;
import com.adretsoftwares.cellsecuritycare.util.UtilityAndConstant;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {


    public static Boolean isWrongPasswordEnterd = false;
    //Imageloader to load image
    private ImageLoader imageLoader,imageLoader2;
    private final Context context;
    private   String firstTimeEntry;

    //List to store all superheroes
    List<Notify> notify;

    //Constructor of this class
    public CardAdapter(List<Notify> superHeroes, Context context){
        super();
        //Getting all superheroes
        this.notify = superHeroes;
        this.context = context;
        firstTimeEntry =  UtilityAndConstant.getString("wrongPin");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.info_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        Log.d("cardAdapter","the var is "+isWrongPasswordEnterd);
        //Getting the particular item from the list
        Notify notify1 =  notify.get(position);

        //Loading image from url
        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(notify1.getImageUrl(), ImageLoader.getImageListener(holder.imageView, com.google.android.gms.base.R.drawable.common_google_signin_btn_icon_dark, android.R.drawable.ic_dialog_alert));
        imageLoader2 = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader2.get(notify1.getImageUr2(), ImageLoader.getImageListener(holder.imageView2, com.google.android.gms.base.R.drawable.common_google_signin_btn_icon_dark, android.R.drawable.ic_dialog_alert));

        //Showing data on the views
        holder.imageView.setImageUrl(notify1.getImageUrl(), imageLoader);
        holder.imageView2.setImageUrl(notify1.getImageUr2(), imageLoader2);
        holder.textViewName.setText(notify1.getMessage());
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        holder.tvDate.setText(date+"  "+currentTime);
        String cordinates = notify1.getCoordinates();
       String[] cord =cordinates.split(",");
        String tempUri;

        if (firstTimeEntry == "wrongPinEntry") {
            String url = "https://www.google.com/maps/search/?api=1&query="+lattitude+"%2C"+longitude;
            tempUri = url;
            holder.textViewPublisher.setText(url);
        }else {
                   String lattitude = cord[0];
                   String longitude = cord[1];
            String url = "https://www.google.com/maps/search/?api=1&query="+lattitude+"%2C"+longitude;
            tempUri = url;
            holder.textViewPublisher.setText(url);
        }



        holder.textViewPublisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = tempUri;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                context.startActivity(intent);
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.image_popup_dialog);
//                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);

                NetworkImageView expandedImageView = dialog.findViewById(R.id.expandedImageView);
                expandedImageView.setImageUrl(notify1.getImageUrl(), imageLoader);


                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Bitmap imageFile = DownloadImage.Companion.mLoad(notify1.getImageUrl(),context);
                            DownloadImage.Companion.mSaveMediaToStorage(imageFile,context);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();

                dialog.show();

            }
        });

        holder.imageView2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.image_popup_dialog);
//                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);

                NetworkImageView expandedImageView = dialog.findViewById(R.id.expandedImageView);
               expandedImageView.setImageUrl(notify1.getImageUr2(), imageLoader2);



                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {

                            Bitmap imageFile = DownloadImage.Companion.mLoad(notify1.getImageUr2(),context);
                            DownloadImage.Companion.mSaveMediaToStorage(imageFile,context);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();

                dialog.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return notify.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        //Views
        public NetworkImageView imageView,imageView2;
        public TextView textViewName;
        public TextView textViewPublisher;
        public TextView tvDate;

        //Initializing Views
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewHero);
            imageView2 = itemView.findViewById(R.id.imageViewHero2);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPublisher = itemView.findViewById(R.id.textViewPublisher);
            tvDate = itemView.findViewById(R.id.tvDate);

        }
    }


}
