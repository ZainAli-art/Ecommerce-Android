package com.example.android.ecommerce.adapters;

import android.net.Uri;

import androidx.room.TypeConverter;

import java.sql.Timestamp;
import java.util.Date;

public class TypeConverters {
    @TypeConverter
    public static Long fromTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.getTime();
    }

    @TypeConverter
    public static Long fromDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.getTime();
    }

    @TypeConverter
    public static String fromUri(Uri uri) {
        if (uri == null) {
            return null;
        }
        return uri.toString();
    }

    @TypeConverter
    public static Timestamp fromString(String time) {
        return Timestamp.valueOf(time);
    }

    @TypeConverter
    public static Timestamp toTimestamp(Long time) {
        if (time == null) {
            return null;
        }
        return new Timestamp(time);
    }

    @TypeConverter
    public static Date toDate(Long time) {
        if (time == null) {
            return null;
        }
        return new Date(time);
    }

    @TypeConverter
    public static Uri toUri(String uriStr) {
        if (uriStr == null) {
            return null;
        }
        return Uri.parse(uriStr);
    }

    @TypeConverter
    public static String toString(Timestamp timestamp) {
        return timestamp.toString();
    }
}
