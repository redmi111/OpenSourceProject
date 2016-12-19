package com.music.musicplayer135.ads;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.startapp.android.publish.Ad;
import com.startapp.android.publish.AdEventListener;
import com.startapp.android.publish.StartAppSDK;
import com.startapp.android.publish.nativead.NativeAdDetails;
import com.startapp.android.publish.nativead.NativeAdPreferences;
import com.startapp.android.publish.nativead.StartAppNativeAd;

import java.util.ArrayList;
import java.util.Random;

import com.music.musicplayer135.R;

/**
 * Created by TienNVe on 11/1/2016.
 */

public class NativeStartApp extends LinearLayout {
    public ImageView ivIcon;
    public TextView tvTitle, tvDescription;
    public StartAppNativeAd startAppNativeAd;
    public NativeAdDetails appNative = null;
    public Handler handler = new Handler();
    ArrayList<NativeAdDetails> list;
    Context context1;
    public NativeStartApp(Context context, AttributeSet attrs) {
        super(context, attrs);
        context1 = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.native_layout, this);
            ivIcon = (ImageView) findViewById(R.id.iconApp);
            tvTitle = (TextView) findViewById(R.id.title);
            tvDescription = (TextView) findViewById(R.id.description);
        }
        setVisibility(GONE);
//        StartAppSDK.init((Activity) context, "208354514", true);
        StartAppSDK.init((Activity) context, GetConfig.StartAppId, true);
        startAppNativeAd = new StartAppNativeAd(context);
        startAppNativeAd.loadAd(new NativeAdPreferences()
                .setAdsNumber(200)
                .setAutoBitmapDownload(true)
                .setImageSize(NativeAdPreferences.NativeAdBitmapSize.SIZE150X150), new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                setVisibility(VISIBLE);
                list = startAppNativeAd.getNativeAds();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int indexRandom = new Random().nextInt(list.size());
                            appNative = list.get(indexRandom);
                            appNative.sendImpression(context1);
                            ivIcon.setImageBitmap(appNative.getImageBitmap());
                            tvTitle.setText(appNative.getTitle());
                            String description = appNative.getDescription();
                            tvDescription.setText(description.length() > 100 ? description.substring(0, 100) : description);
                            handler.postDelayed(this, 10000);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {

            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                appNative.sendClick(context1);
            }
        });
    }
}
