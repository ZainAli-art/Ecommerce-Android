package com.example.android.ecommerce.utils;

import android.util.Log;

import androidx.navigation.NavController;

public class NavigationUtils {
    private static final String TAG = "NavigationUtils";

    public static boolean isValidInContext(NavController navController, int rid) {
        if (navController == null) {
            Log.d(TAG, "isValidInContext: navController is null");
            return false;
        }
        if (navController.getCurrentDestination() == null) {
            Log.d(TAG, "isValidInContext: current destination is null");
            return false;
        }
        if (navController.getCurrentDestination().getId() != rid) {
            Log.d(TAG, "isValidInContext: ids are not same");
            return false;
        }
        Log.d(TAG, "isValidInContext: valid");
        return true;
//        return navController != null && navController.getCurrentDestination() != null
//                && navController.getCurrentDestination().getId() == rid;
    }
}
