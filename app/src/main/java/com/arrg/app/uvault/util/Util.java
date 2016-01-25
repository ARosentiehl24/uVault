package com.arrg.app.uvault.util;

/*
 * Created by albert on 23/12/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.arrg.app.uvault.R;
import com.arrg.app.uvault.views.MainActivity;
import com.jaredrummler.android.device.DeviceName;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pass.Spass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Util {

    protected final static String TAG = "Util";

    public static void close(AppCompatActivity activity, Boolean finish) {
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        if (finish) {
            activity.finish();
        }
    }

    public static void closeInverse(AppCompatActivity activity, Boolean finish) {
        if (finish) {
            activity.finish();
        }
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void launchFragment(AppCompatActivity activity, Class fragmentLaunch, Integer resLayoutId, Boolean addToBackStack, Boolean popBackStack) {
        Class fragmentClass;
        Fragment fragment = null;
        FragmentManager fragmentManager;

        fragmentClass = fragmentLaunch;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        fragmentManager = activity.getSupportFragmentManager();

        if (popBackStack) {
            fragmentManager.popBackStack();
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (addToBackStack) {
            transaction.addToBackStack(fragmentLaunch.getName());
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        transaction.replace(resLayoutId, fragment, fragmentLaunch.getName());
        transaction.commit();
    }

    public static void open(AppCompatActivity activity, Class aClass, Boolean finish) {
        activity.startActivity(new Intent(activity, aClass));
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        if (finish) {
            activity.finish();
        }
    }

    public static void open(AppCompatActivity activity, Intent intent, Boolean finish) {
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        if (finish) {
            activity.finish();
        }
    }

    public static void openInverse(AppCompatActivity activity, Intent intent, Boolean finish) {
        activity.startActivity(intent);
        if (finish) {
            activity.finish();
        }
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void hideActionBar(AppCompatActivity activity) {
        ActionBar actionBar = activity.getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public static void hideActionBarUp(AppCompatActivity activity, Boolean value) {
        ActionBar actionBar = activity.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(value);
        }
    }

    public static void restartApp(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void startHomeScreenActivity(Activity activity) {
        Intent startHomeScreen = new Intent(Intent.ACTION_MAIN);
        startHomeScreen.addCategory(Intent.CATEGORY_HOME);
        activity.startActivity(startHomeScreen);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        activity.finish();
    }

    public static void startHomeScreenActivity(Context context) {
        Intent startHomeScreen = new Intent(Intent.ACTION_MAIN);
        startHomeScreen.addCategory(Intent.CATEGORY_HOME);
        startHomeScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startHomeScreen);
    }

    public static boolean hasNavBar(Resources resources) {
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }

    public static boolean isSamsungDevice(Context context) {
        return DeviceName.getDeviceInfo(context).manufacturer.toUpperCase().contains("Samsung".toUpperCase());
    }

    public static boolean isFingerprintEnabled(Activity activity) {
        Spass spass = new Spass();

        try {
            spass.initialize(activity);
        } catch (SsdkUnsupportedException e) {
            Log.d("Finger", "Exception: " + e);
        } catch (UnsupportedOperationException e) {
            Log.d("Finger", activity.getString(R.string.fingerprint_service_is_not_supported));
        }

        Boolean isFeatureEnabled = spass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT);

        if (isFeatureEnabled) {
            Log.d("Finger", "Fingerprint Service is supported in the device.");
            Log.d("Finger", "SDK version : " + spass.getVersionName());

            return true;
        } else {
            Log.d("Finger", "Fingerprint Service is not supported in the device.");

            return false;
        }
    }

    public static boolean isFingerprintEnabled(Activity activity, Spass spass) {
        Boolean isFeatureEnabled = spass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT);

        if (isFeatureEnabled) {
            Log.d("Finger", "Fingerprint Service is supported in the device.");
            Log.d("Finger", "SDK version : " + spass.getVersionName());

            return true;
        } else {
            Log.d("Finger", "Fingerprint Service is not supported in the device.");

            return false;
        }
    }

    public static boolean isSimSupport(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return (tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT);
    }

    public static boolean saveWallpaper(Bitmap bitmap, File picture) {
        // TODO Auto-generated method stub

        try {
            OutputStream output = new FileOutputStream(picture);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();

            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            return false;
        }
    }

    public static int getAverageColor(Drawable drawable) {
        //Setup initial variables
        int hSamples = 40;            //Number of pixels to sample on horizontal axis
        int vSamples = 40;            //Number of pixels to sample on vertical axis
        int sampleSize = hSamples * vSamples; //Total number of pixels to sample
        float[] sampleTotals = {0, 0, 0};   //Holds temporary sum of HSV values

        //If white pixels are included in sample, the average color will
        //  often have an unexpected shade. For this reason, we set a
        //  minimum saturation for pixels to be included in the sample set.
        //  Saturation < 0.1 is very close to white (see http://mkweb.bcgsc.ca/color_summarizer/?faq)
        float minimumSaturation = 0.1f;     //Saturation range is 0...1

        //By the same token, we should ignore transparent pixels
        //  (pixels with low alpha value)
        int minimumAlpha = 200;         //Alpha range is 0...255

        //Get bitmap
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();

        int width = b.getWidth();
        int height = b.getHeight();

        //Loop through pixels horizontally
        float[] hsv = new float[3];

        int sample;

        for (int i = 0; i < width; i += (width / hSamples)) {
            //Loop through pixels vertically
            for (int j = 0; j < height; j += (height / vSamples)) {
                //Get pixel & convert to HSV format
                sample = b.getPixel(i, j);
                Color.colorToHSV(sample, hsv);

                //Check pixel matches criteria to be included in sample
                if ((Color.alpha(sample) > minimumAlpha) && (hsv[1] >= minimumSaturation)) {
                    //Add sample values to total
                    sampleTotals[0] += hsv[0];  //H
                    sampleTotals[1] += hsv[1];  //S
                    sampleTotals[2] += hsv[2];  //V
                } else {
                    Log.v(TAG, "Pixel rejected: Alpha " + Color.alpha(sample) + ", H: " + hsv[0] + ", S:" + hsv[1] + ", V:" + hsv[1]);
                }
            }
        }

        //Divide total by number of samples to get average HSV values
        float[] average = new float[3];

        average[0] = sampleTotals[0] / sampleSize;
        average[1] = sampleTotals[1] / sampleSize;
        average[2] = sampleTotals[2] / sampleSize;

        return Color.HSVToColor(average);
    }
}
