package com.example.android.ecommerce.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.DisplayMetrics;

import java.io.ByteArrayOutputStream;

public class Utility {
    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] arr = stream.toByteArray();
        return Base64.encodeToString(arr, Base64.DEFAULT);
    }

    public static int getNoOfColumns(Context context, float columnWidthDP) {
        return (int) Math.floor(getScreenWidthDp(context) / columnWidthDP + 0.5); // 0.5 for correct rounding to int
    }

    public static float getScreenWidthDp(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels / metrics.density;
    }
}
