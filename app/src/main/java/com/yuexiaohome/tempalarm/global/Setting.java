package com.yuexiaohome.tempalarm.global;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Setting
{
    private static SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(
            YXApplication.getInstance());

    public static String getString(String key)
    {
        return prefs.getString(key,"");
    }

    public static void setString(String key,String value)
    {
        prefs.edit().putString(key,value).apply();
    }

    public static String getString(String key,String defValue)
    {
        return prefs.getString(key,defValue);
    }
}