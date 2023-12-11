package com.adretsoftwares.cellsecuritycare.util;

import android.content.Context;
import android.content.SharedPreferences;

public  class UtilityAndConstant {
    private static SharedPreferences preferences;
    private static String APP_PREFERENCE =  "com.adretsoftwares.cellsecuritycare";

    public  static void init(Context context) {
        preferences = context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
    }

    public  static void saveString( String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public  static String getString( String key) {
        return preferences.getString(key, "");
    }







}
