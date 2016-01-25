package com.arrg.app.uvault.util;

/*
 * Created by albert on 21/12/2015.
 */

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {

    private Context mContext;

    public SharedPreferencesUtil(Context mContext) {
        this.mContext = mContext;
    }

    /*- Boolean ------------------------------------------------------------------------------------*/

    public Boolean getBoolean(SharedPreferences preferences, int key, int value) {
        return preferences.getBoolean(mContext.getString(key), mContext.getResources().getBoolean(value));
    }

    public Boolean getBoolean(SharedPreferences preferences, String key, int value) {
        return preferences.getBoolean(key, mContext.getResources().getBoolean(value));
    }

    public Boolean getBoolean(SharedPreferences preferences, int key, Boolean value) {
        return preferences.getBoolean(mContext.getString(key), value);
    }

    public Boolean getBoolean(SharedPreferences preferences, String key, Boolean value) {
        return preferences.getBoolean(key, value);
    }

    /*- Float --------------------------------------------------------------------------------------*/

    public Float getFloat(SharedPreferences preferences, int key, int value) {
        return preferences.getFloat(mContext.getString(key), mContext.getResources().getDimension(value));
    }

    public Float getFloat(SharedPreferences preferences, String key, int value) {
        return preferences.getFloat(key, mContext.getResources().getDimension(value));
    }

    public Float getFloat(SharedPreferences preferences, int key, Float value) {
        return preferences.getFloat(mContext.getString(key), value);
    }

    public Float getFloat(SharedPreferences preferences, String key, Float value) {
        return preferences.getFloat(key, value);
    }

    /*- Integer ------------------------------------------------------------------------------------*/

    public Integer getInt(SharedPreferences preferences, int key, int value) {
        return preferences.getInt(mContext.getString(key), value);
    }

    public Integer getInt(SharedPreferences preferences, String key, int value) {
        return preferences.getInt(key, value);
    }

    public Integer getInt(SharedPreferences preferences, int key, Integer value) {
        return preferences.getInt(mContext.getString(key), value);
    }

    public Integer getInt(SharedPreferences preferences, String key, Integer value) {
        return preferences.getInt(key, value);
    }

    /*- String -------------------------------------------------------------------------------------*/

    public String getString(SharedPreferences preferences, int key, int value) {
        return preferences.getString(mContext.getString(key), mContext.getString(value));
    }

    public String getString(SharedPreferences preferences, String key, int value) {
        return preferences.getString(key, mContext.getString(value));
    }

    public String getString(SharedPreferences preferences, int key, String value) {
        return preferences.getString(mContext.getString(key), value);
    }

    public String getString(SharedPreferences preferences, String key, String value) {
        return preferences.getString(key, value);
    }

    /*- Put values ---------------------------------------------------------------------------------*/

    public void putValue(SharedPreferences preferences, int key, Object value) {
        SharedPreferences.Editor editor = preferences.edit();

        if (value instanceof Boolean) {
            editor.putBoolean(mContext.getString(key), (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(mContext.getString(key), (Float) value);
        } else if (value instanceof Integer) {
            editor.putInt(mContext.getString(key), (Integer) value);
        } else if (value instanceof String) {
            editor.putString(mContext.getString(key), (String) value);
        }

        editor.apply();
    }

    public void putValue(SharedPreferences preferences, String key, Object value) {
        SharedPreferences.Editor editor = preferences.edit();

        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        }

        editor.apply();
    }

    /* Delete values ------------------------------------------------------------------------------*/

    public void deleteValue(SharedPreferences preferences, Integer key) {
        if (preferences.contains(mContext.getString(key))) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(mContext.getString(key));
            editor.apply();
        }
    }

    public void deleteValue(SharedPreferences preferences, String key) {
        if (preferences.contains(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(key);
            editor.apply();
        }
    }

    /* Check for values ----------------------------------------------------------------------------*/

    public boolean exists (SharedPreferences preferences, Integer key) {
        return preferences.contains(mContext.getString(key));
    }

    public boolean exists (SharedPreferences preferences, String key) {
        return preferences.contains(key);
    }
}