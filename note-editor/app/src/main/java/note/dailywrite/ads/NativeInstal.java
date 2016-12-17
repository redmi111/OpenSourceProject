package note.dailywrite.ads;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.instal.nativeads.InstalNativeAd;
import com.instal.nativeads.InstalNativeAdListener;
import com.instal.nativeads.NativeErrorCode;
import com.instal.nativeads.NativeResponse;
import note.dailywrite.R;

import java.io.InputStream;

/**
 * Created by TienNVe on 11/24/2016.
 */
public class NativeInstal extends LinearLayout {
    public ImageView ivIcon;
    public TextView tvTitle, tvDescription;
    public LinearLayout rootView;
    private InstalNativeAd instalNativeAd;
    public Handler handler = new Handler();
    public Context context1;
    public NativeInstal(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context1 = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.native_layout, this);
            rootView = (LinearLayout) findViewById(R.id.rootView);
            ivIcon = (ImageView) findViewById(R.id.iconApp);
            tvTitle = (TextView) findViewById(R.id.title);
            tvDescription = (TextView) findViewById(R.id.description);
        }
        setVisibility(GONE);
        handler.post(new Runnable() {
            @Override
            public void run() {
                instalNativeAd = new InstalNativeAd(context1, "3719");
//                instalNativeAd = new InstalNativeAd(context1, GetConfig.instalId);
                instalNativeAd.makeRequest(new InstalNativeAdListener() {
                    @Override
                    public void onLoad(NativeResponse nativeResponse) {
                        setVisibility(VISIBLE);
                        new DownloadImageTask(ivIcon).execute(nativeResponse.getIconImageUrl());
                        tvTitle.setText(nativeResponse.getTitle());
                        String description = nativeResponse.getPromoText();
                        tvDescription.setText(description.length()>100?description.substring(0,100):description);
                        nativeResponse.registerView(rootView);
                    }

                    @Override
                    public void onFail(NativeErrorCode errorCode) {
                        setVisibility(GONE);
                    }
                });
                handler.postDelayed(this,10000);
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
