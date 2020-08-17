package com.example.android.ecommerce.utils;

import androidx.navigation.NavController;

public class NavigationUtils {
    public static boolean isValidInContext(NavController navController, int rid) {
        return navController != null && navController.getCurrentDestination() != null
                && navController.getCurrentDestination().getId() == rid;
    }
}
