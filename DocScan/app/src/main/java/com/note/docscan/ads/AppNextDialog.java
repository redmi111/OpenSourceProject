package com.note.docscan.ads;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.appnext.appnextsdk.API.AppnextAPI;
import com.appnext.appnextsdk.API.AppnextAd;
import com.appnext.appnextsdk.API.AppnextAdRequest;
import com.note.docscan.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * Created by TienNVe on 11/1/2016.
 */
public class AppNextDialog extends DialogFragment {
    public AppnextAPI api;
    public ImageView ivIcon;
    public TextView tvTitle, tvDescription;
    public AppnextAd appNext = null;
    public List<AppnextAd> list = new ArrayList<>();
    public Dialog dialog;
    public boolean loaded = false;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return dialog;
    }

    /**
     * init dialog
     * load appNext
     * @param context
     */
    public void load(Context context){
        dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.dialog_native_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        // Close
        dialog.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ivIcon = (ImageView) dialog.findViewById(R.id.iconApp);
        tvTitle = (TextView) dialog.findViewById(R.id.title);
        tvDescription = (TextView) dialog.findViewById(R.id.description);

        dialog.findViewById(R.id.adsNative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                api.adClicked(appNext);
            }
        });
        //       api = new AppnextAPI(context, "07dba6fc-3bdd-4499-88e9-5ec5746a7187");
        api = new AppnextAPI(context, GetConfig.AppNextId);
        api.setAdListener(new AppnextAPI.AppnextAdListener() {
            @Override
            public void onError(String error) {
                Log.e("NativeAppNext",error);
            }

            @Override
            public void onAdsLoaded(ArrayList ads) {
                try {
                    list = ads;
                    loaded = true;
                    int indexRandom = new Random().nextInt(list.size());
                    appNext = list.get(indexRandom);
                    api.adImpression(appNext);
                    new DownloadImageTask(ivIcon).execute(appNext.getImageURL());
                    tvTitle.setText(appNext.getAdTitle());
                    String description = appNext.getAdDescription();
                    tvDescription.setText(description.length()>100?description.substring(0,100):description);
                }catch (Exception e){
                    Log.e("AppNextDialog",e.toString());
                }
            }
        });
        api.loadAds(new AppnextAdRequest().setCount(200));
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
            if(result!=null)
                bmImage.setImageBitmap(result);
        }
    }
}
