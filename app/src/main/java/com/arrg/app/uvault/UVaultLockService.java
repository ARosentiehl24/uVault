package com.arrg.app.uvault;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.color.CircleView;
import com.arrg.app.uvault.util.Util;
import com.arrg.app.uvault.views.MainActivity;

public class UVaultLockService extends Service implements View.OnKeyListener, View.OnClickListener {

    private LinearLayout oView;
    private View mRootView;
    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;


    public UVaultLockService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        beforeInflate();

        mRootView = inflateRootView();

        mWindowManager.addView(mRootView, mLayoutParams);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                return true;
            case KeyEvent.KEYCODE_HOME:
                finish();
                return true;
            case KeyEvent.KEYCODE_MENU:
                finish();
                return true;
        }

        return true;
    }

    public void beforeInflate() {
        mLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        mLayoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    public View inflateRootView() {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater li = LayoutInflater.from(this);

        setTheme(R.style.AppTheme);

        View root = li.inflate(R.layout.fragment_uvault_lock_activity, null);
        root.setBackgroundColor(CircleView.shiftColorDown(ContextCompat.getColor(this, R.color.background_light)));
        root.setOnKeyListener(this);

        root.setFocusable(true);
        root.setFocusableInTouchMode(true);

        return root;
    }

    public void finish() {
        Util.startHomeScreenActivity(this);

        mWindowManager.removeView(mRootView);

        MainActivity.mainActivity.finish();

        stopSelf();
    }

    private enum ViewState {
        /**
         * The view is visible but not yet completely shown
         */
        SHOWING,
        /**
         * The view has been completely animated and the user is ready to
         * interact with it
         */
        SHOWN,
        /**
         * The user has unlocked / pressed back, and the view is animating
         */
        HIDING,
        /**
         * The view is not visible to the user
         */
        HIDDEN
    }
}
