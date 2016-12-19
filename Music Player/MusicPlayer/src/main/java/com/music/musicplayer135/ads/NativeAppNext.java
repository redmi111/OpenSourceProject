package com.music.musicplayer135.ads;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appnext.appnextsdk.API.AppnextAPI;
import com.appnext.appnextsdk.API.AppnextAd;
import com.appnext.appnextsdk.API.AppnextAdRequest;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import com.music.musicplayer135.R;

/**
 * Created by TienNVe on 11/1/2016.
 */

public class NativeAppNext extends LinearLayout {
    public AppnextAPI api;
    public ImageView ivIcon;
    public TextView tvTitle, tvDescription;
    public AppnextAd appNext = null;
    public Handler handler = new Handler();
    ArrayList<AppnextAd> list;
    public NativeAppNext(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.native_layout, this);
            ivIcon = (ImageView) findViewById(R.id.iconApp);
            tvTitle = (TextView) findViewById(R.id.title);
            tvDescription = (TextView) findViewById(R.id.description);
        }
        setVisibility(GONE);
//        api = new AppnextAPI(context, "07dba6fc-3bdd-4499-88e9-5ec5746a7187");
        api = new AppnextAPI(context, GetConfig.AppNextId);
        api.setAdListener(new AppnextAPI.AppnextAdListener() {
            @Override
            public void onError(String error) {
                Log.e("NativeAppNext",error);
            }

            @Override
            public void onAdsLoaded(ArrayList ads) {
                setVisibility(VISIBLE);
                list = ads;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        int indexRandom = new Random().nextInt(list.size());
                        appNext = list.get(indexRandom);
                        api.adImpression(appNext);

                        new DownloadImageTask(ivIcon).execute(appNext.getImageURL());
                        tvTitle.setText(appNext.getAdTitle());
                        String description = appNext.getAdDescription();
                        tvDescription.setText(description.length()>100?description.substring(0,100):description);
                        handler.postDelayed(this,10000);
                    }
                });
            }
        });
        api.loadAds(new AppnextAdRequest().setCount(200));

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(appNext!=null)
                    api.adClicked(appNext);
            }
        });
    }

    /**
     * create bitmap from Url.
     * set Icon bitmap
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
