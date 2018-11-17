package com.sanxynet.bakingapp.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.sanxynet.bakingapp.R;
import com.sanxynet.bakingapp.utils.Utility;

public class SettingsFragment extends PreferenceFragmentCompat {

    private String mAppVersionName;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_general_settings);

        prefVersion();

    }


    private void prefVersion(){
        final Preference prefVersion = findPreference(getString(R.string.pref_app_version));

        try {
            mAppVersionName = new Utility(getActivity()).appVersionName();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (mAppVersionName != null) {
            prefVersion.setTitle("Version: " + mAppVersionName);
        }
    }
}