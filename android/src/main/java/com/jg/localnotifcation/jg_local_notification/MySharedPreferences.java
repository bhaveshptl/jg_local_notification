package com.jg.localnotifcation.jg_local_notification;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    public static void setIntValue(Context context, String key, int value )
    {
        SharedPreferences sharedPref = context.getSharedPreferences(
                "MY_SHARED_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getIntValue(Context context,String key )
    {
        SharedPreferences sharedPref = context.getSharedPreferences(
                "MY_SHARED_PREF", Context.MODE_PRIVATE);
        int defaultValue = 0;
        int value = sharedPref.getInt(key, defaultValue);
        return value;
    }

    public static void setBoolValue(Context context, String key, boolean value )
    {
        SharedPreferences sharedPref = context.getSharedPreferences(
                "MY_SHARED_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolValue(Context context,String key )
    {
        SharedPreferences sharedPref = context.getSharedPreferences(
                "MY_SHARED_PREF", Context.MODE_PRIVATE);
        boolean value = sharedPref.getBoolean(key, false);
        return value;
    }
}
