package com.alienonwork.crossfitcheckin.fragments;

import android.os.Bundle;

import com.alienonwork.crossfitcheckin.R;

import androidx.preference.PreferenceFragmentCompat;


public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.userpreferences, null);
    }
}
