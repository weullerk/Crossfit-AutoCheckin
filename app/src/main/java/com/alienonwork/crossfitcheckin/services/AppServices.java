package com.alienonwork.crossfitcheckin.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.alienonwork.crossfitcheckin.R;
import com.alienonwork.crossfitcheckin.constants.PreferencesConstants;

public class AppServices {
    public static boolean isSettingsValid(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean autoCheckinEnabled = sharedPref.getBoolean(context.getString(R.string.pref_auto_checkin_enabled), false);
        Integer appEngageId = sharedPref.getInt(PreferencesConstants.PREF_APPENGAGE_ID, 0);
        Integer boxId = sharedPref.getInt(PreferencesConstants.PREF_BOX_ID, 0);
        Integer userId = sharedPref.getInt(PreferencesConstants.PREF_USER_ID, 0);
        String token = sharedPref.getString(PreferencesConstants.PREF_TOKEN, "");

        if (!autoCheckinEnabled)
            return false;

        if (appEngageId == 0)
            return false;

        if (boxId == 0)
            return false;

        if (userId == 0)
            return false;

        if (token.isEmpty())
            return false;

        return true;
    }
}
