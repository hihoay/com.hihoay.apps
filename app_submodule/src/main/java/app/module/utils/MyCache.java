package app.module.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by d on 11/15/2017.
 */
public class MyCache {
    private static SharedPreferences.Editor editor;
    private static SharedPreferences preferences;

    public static void putLongValueByName(Context context, String name, long value) {
        openLog(context);
        editor.putLong(name, value);
        editor.commit();
    }

    public static long getLongValueByName(Context context, String name, long defaultValue) {
        openLog(context);
        long values = preferences.getLong(name, defaultValue);
        return values;
    }

    public static void setArrayStrings(String arrayName, ArrayList<String> array, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("Cache_Array", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName + "_size", array.size());
        for (int i = 0; i < array.size(); i++) editor.putString(arrayName + "_" + i, array.get(i));
        editor.apply();
    }

    public static ArrayList<String> getArrayStrings(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("Cache_Array", 0);
        int size = prefs.getInt(arrayName + "_size", 0);
        ArrayList<String> array = new ArrayList<>(size);
        for (int i = 0; i < size; i++) array.add(prefs.getString(arrayName + "_" + i, null));
        return array;
    }

    public static void setPrefs(String key, String value, Context context) {
        SharedPreferences sharedpreferences =
                context.getSharedPreferences("Cache_Array", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getPrefs(String key, Context context) {
        SharedPreferences sharedpreferences =
                context.getSharedPreferences("Cache_Array", Context.MODE_PRIVATE);
        return sharedpreferences.getString(key, "notfound");
    }

    private static void openLog(Context context) {
        preferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static void putStringValueByName(Context context, String name, String value) {
        openLog(context);
        editor.putString(name, value);
        editor.commit();
    }

    public static String getStringValueByName(Context context, String name, String def) {
        openLog(context);
        String values = preferences.getString(name, def);
        return values;
    }

    public static String getStringValueByName(Context context, String name) {
        openLog(context);
        String values = preferences.getString(name, "");
        return values;
    }

    public static void putIntValueByName(Context context, String name, int value) {
        openLog(context);
        editor.putInt(name, value);
        editor.commit();
    }

    public static int getIntValueByName(Context context, String name, int defaultValue) {
        openLog(context);
        int values = preferences.getInt(name, defaultValue);
        return values;
    }

    public static int getIntValueByName(Context context, String name) {
        openLog(context);
        int values = preferences.getInt(name, 0);
        return values;
    }

    public static void putBooleanValueByName(Context context, String name, boolean value) {
        openLog(context);
        editor.putBoolean(name, value);
        editor.commit();
    }

    public static boolean getBooleanValueByName(Context context, String name) {
        openLog(context);
        return preferences.getBoolean(name, false);
    }

    public static void removeAll() {
        editor.clear();
        editor.commit();
    }

    public static boolean getBooleanValueByName(Context context, String name, boolean b) {
        openLog(context);
        return preferences.getBoolean(name, b);
    }
}
