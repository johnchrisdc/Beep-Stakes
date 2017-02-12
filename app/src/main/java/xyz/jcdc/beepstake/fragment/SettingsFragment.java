package xyz.jcdc.beepstake.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import xyz.jcdc.beepstake.R;

/**
 * Created by jcdc on 2/12/17.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    private Preference preference_potato;
    private Preference preference_dotc;

    private final String URL_POTATO = "https://www.facebook.com/RadioActivePotatoProject/";
    private final String URL_DOTC = "https://dotcmrt3.gov.ph/";

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.about);

        preference_potato = findPreference("developer");
        preference_dotc = findPreference("dotcmrt");

        preference_potato.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String url = URL_POTATO;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            }
        });

        preference_dotc.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String url = URL_DOTC;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            }
        });
    }
}
