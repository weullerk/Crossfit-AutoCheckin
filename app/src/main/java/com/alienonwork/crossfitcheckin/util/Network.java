package com.alienonwork.crossfitcheckin.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;

public class Network {


    public static boolean hasConnectionEnabled(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return connectivityManager.getActiveNetwork() != null;
        } else {
            return connectivityManager.getActiveNetworkInfo() != null;
        }
    }


}
