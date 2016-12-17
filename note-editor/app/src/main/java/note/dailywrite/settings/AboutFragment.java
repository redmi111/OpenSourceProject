package note.dailywrite.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import note.dailywrite.R;

/**
 * Created by Minty123 on 2015-01-15.
 */
public class AboutFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about_screen);
    }

}
