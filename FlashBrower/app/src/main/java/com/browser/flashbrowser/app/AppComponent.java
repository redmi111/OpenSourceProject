package com.browser.flashbrowser.app;

import javax.inject.Singleton;

import com.browser.flashbrowser.activity.BrowserActivity;
import com.browser.flashbrowser.activity.ReadingActivity;
import com.browser.flashbrowser.activity.TabsManager;
import com.browser.flashbrowser.activity.ThemableBrowserActivity;
import com.browser.flashbrowser.activity.ThemableSettingsActivity;
import com.browser.flashbrowser.browser.BrowserPresenter;
import com.browser.flashbrowser.constant.StartPage;
import com.browser.flashbrowser.dialog.LightningDialogBuilder;
import com.browser.flashbrowser.download.LightningDownloadListener;
import com.browser.flashbrowser.fragment.BookmarkSettingsFragment;
import com.browser.flashbrowser.fragment.BookmarksFragment;
import com.browser.flashbrowser.fragment.DebugSettingsFragment;
import com.browser.flashbrowser.fragment.LightningPreferenceFragment;
import com.browser.flashbrowser.fragment.PrivacySettingsFragment;
import com.browser.flashbrowser.fragment.TabsFragment;
import com.browser.flashbrowser.search.SuggestionsAdapter;
import com.browser.flashbrowser.utils.AdBlock;
import com.browser.flashbrowser.utils.ProxyUtils;
import com.browser.flashbrowser.view.LightningView;
import com.browser.flashbrowser.view.LightningWebClient;
import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(BrowserActivity activity);

    void inject(BookmarksFragment fragment);

    void inject(BookmarkSettingsFragment fragment);

    void inject(LightningDialogBuilder builder);

    void inject(TabsFragment fragment);

    void inject(LightningView lightningView);

    void inject(ThemableBrowserActivity activity);

    void inject(LightningPreferenceFragment fragment);

    void inject(BrowserApp app);

    void inject(ProxyUtils proxyUtils);

    void inject(ReadingActivity activity);

    void inject(LightningWebClient webClient);

    void inject(ThemableSettingsActivity activity);

    void inject(AdBlock adBlock);

    void inject(LightningDownloadListener listener);

    void inject(PrivacySettingsFragment fragment);

    void inject(StartPage startPage);

    void inject(BrowserPresenter presenter);

    void inject(TabsManager manager);

    void inject(DebugSettingsFragment fragment);

    void inject(SuggestionsAdapter suggestionsAdapter);

}
