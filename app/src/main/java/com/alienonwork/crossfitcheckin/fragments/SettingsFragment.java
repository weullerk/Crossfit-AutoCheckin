package com.alienonwork.crossfitcheckin.fragments;

import android.os.Bundle;

import com.alienonwork.crossfitcheckin.R;

import androidx.preference.PreferenceFragmentCompat;


public class SettingsFragment extends PreferenceFragmentCompat {

    public static String PREF_TOKEN = "pref_token";
    public static String PREF_USER_ID = "pref_user_id";
    public static String PREF_MODIFIED_CLASS = "pref_modified_class";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.userpreferences, null);
    }
}
