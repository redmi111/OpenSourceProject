package com.browser.flashbrowser.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.browser.flashbrowser.R;
import com.anthonycr.bonsai.Action;
import com.anthonycr.bonsai.Observable;
import com.anthonycr.bonsai.Subscriber;
import com.browser.flashbrowser.ads.*;

@SuppressWarnings("deprecation")
public class MainActivity extends BrowserActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new GetConfig(this,getPackageName());
        Handler handler = new Handler();
        final AppNextDialog appNextDialog = new AppNextDialog();
        //appnext
        appNextDialog.load(this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(appNextDialog.loaded){
                    appNextDialog.show(getFragmentManager(),"");
                }
            }
        },5000);
        super.onCreate(savedInstanceState);
    }

    @Override
    public Observable<Void> updateCookiePreference() {
        return Observable.create(new Action<Void>() {
            @Override
            public void onSubscribe(@NonNull Subscriber<Void> subscriber) {
                CookieManager cookieManager = CookieManager.getInstance();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    CookieSyncManager.createInstance(MainActivity.this);
                }
                cookieManager.setAcceptCookie(mPreferences.getCookiesEnabled());
                subscriber.onComplete();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (isPanicTrigger(intent)) {
            panicClean();
        } else {
            handleNewIntent(intent);
            super.onNewIntent(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveOpenTabs();
    }

    @Override
    public void updateHistory(@Nullable String title, @NonNull String url) {
        addItemToHistory(title, url);
    }

    @Override
    public boolean isIncognito() {
        return false;
    }

    @Override
    public void closeActivity() {
        closeDrawers(new Runnable() {
            @Override
            public void run() {
                performExitCleanUp();
                moveTaskToBack(true);
            }
        });
    }


}
