package com.example.android.ecommerce;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ByteUtil {
    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 1, stream);
        byte[] arr = stream.toByteArray();
        return Base64.encodeToString(arr, Base64.DEFAULT);
    }
}
