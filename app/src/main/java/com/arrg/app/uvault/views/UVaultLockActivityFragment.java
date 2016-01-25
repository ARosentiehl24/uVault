package com.arrg.app.uvault.views;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ViewFlipper;

import com.afollestad.appthemeengine.ATE;
import com.arrg.app.uvault.Constants;
import com.arrg.app.uvault.R;
import com.arrg.app.uvault.controller.PinButtonClickedListener;
import com.arrg.app.uvault.controller.PinButtonEnum;
import com.arrg.app.uvault.util.SharedPreferencesUtil;
import com.arrg.app.uvault.util.Util;
import com.arrg.app.uvault.views.uviews.PatternLockView;
import com.arrg.app.uvault.views.uviews.PinLockView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pass.Spass;
import com.samsung.android.sdk.pass.SpassFingerprint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static com.arrg.app.uvault.Constants.ARGS_VAULT;

/**
 * A simple {@link Fragment} subclass.
 */
public class UVaultLockActivityFragment extends Fragment {

    private static final String INPUT_METHOD_HIDE = "hide";
    private static final String INPUT_METHOD_SHOW = "show";
    private static final String TAG = "UVaultFragment";

    private Boolean enableFingerPrintRecognizer;
    private Boolean isInStealthMode;
    private Boolean isNecessaryShowInput = true;
    private Boolean isSwipeEnabled;
    private Boolean onReadyIdentify = false;
    private GestureDetector gestureDetector;
    private SharedPreferences settingsPreferences;
    private SharedPreferencesUtil preferencesUtil;
    private Spass spass;
    private SpassFingerprint spassFingerprint;
    private SpassFingerprint.IdentifyListener listener = new SpassFingerprint.IdentifyListener() {

        @Override
        public void onFinished(int eventStatus) {
            Log.d("Finger", "identify finished : reason=" + getEventStatusName(eventStatus));

            onReadyIdentify = false;

            int FingerprintIndex = 0;

            try {
                FingerprintIndex = spassFingerprint.getIdentifiedFingerprintIndex();
            } catch (IllegalStateException ise) {
                Log.d("Finger", ise.getMessage());
            }

            if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS) {
                Log.d("Finger", "onFinished() : Identify authentification Success with FingerprintIndex : " + FingerprintIndex);
                playUnlockSound();

                preferencesUtil.putValue(settingsPreferences, R.string.last_unlock_method, vfUnlockMethods.getDisplayedChild());

                lunchVaultFragment();
            } else if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS) {
                Log.d("Finger", "onFinished() : Password authentification Success");
            } else {
                Log.d("Finger", "onFinished() : Authentification Fail for identify");
            }
        }

        @Override
        public void onReady() {
            Log.d("Finger", "identify state is ready");
        }

        @Override
        public void onStarted() {
            Log.d("Finger", "User touched fingerprint sensor!");
        }
    };
    private static String getEventStatusName(int eventStatus) {
        switch (eventStatus) {
            case SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS:
                return "STATUS_AUTHENTIFICATION_SUCCESS";
            case SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS:
                return "STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS";
            case SpassFingerprint.STATUS_TIMEOUT_FAILED:
                return "STATUS_TIMEOUT";
            case SpassFingerprint.STATUS_SENSOR_FAILED:
                return "STATUS_SENSOR_ERROR";
            case SpassFingerprint.STATUS_USER_CANCELLED:
                return "STATUS_USER_CANCELLED";
            case SpassFingerprint.STATUS_QUALITY_FAILED:
                return "STATUS_QUALITY_FAILED";
            case SpassFingerprint.STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE:
                return "STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE";
            case SpassFingerprint.STATUS_AUTHENTIFICATION_FAILED:
            default:
                return "STATUS_AUTHENTIFICATION_FAILED";
        }
    }
    private String storedPattern;
    private String storedPassword;
    private String storedPin;

    @Bind(R.id.et_password)
    EditText etPassword;

    @Bind(R.id.et_pin)
    EditText etPin;

    @Bind(R.id.pattern)
    PatternLockView patternView;

    @Bind(R.id.pin)
    PinLockView pinLockView;

    @Bind(R.id.vf_unlock_methods)
    ViewFlipper vfUnlockMethods;

    @OnClick({R.id.fab_fingerprint})
    public void OnClick(View id) {
        switch (id.getId()) {
            case R.id.fab_fingerprint:
                if (preferencesUtil.getInt(settingsPreferences, R.string.designated_finger, 0) == 0) {
                    displayFingerPrintRecognizer();
                } else {
                    displayFingerPrintRecognizerWithIndex();
                }
                break;
        }
    }

    @OnTextChanged(R.id.et_password)
    public void onPasswordChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().equals(storedPassword)) {
            playUnlockSound();

            preferencesUtil.putValue(settingsPreferences, R.string.last_unlock_method, vfUnlockMethods.getDisplayedChild());

            lunchVaultFragment();
        }
    }

    @OnTextChanged(R.id.et_pin)
    public void OnPinChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().equals(storedPin)) {
            playUnlockSound();

            preferencesUtil.putValue(settingsPreferences, R.string.last_unlock_method, vfUnlockMethods.getDisplayedChild());

            lunchVaultFragment();
        }
    }

    public UVaultLockActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("FragmentLock", "onCreate");

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d("FragmentLock", "onActivityCreated");

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("FragmentLock", "onCreateView");

        View root = inflater.inflate(R.layout.fragment_uvault_lock_activity, container, false);
        ButterKnife.bind(this, root);

        preferencesUtil = new SharedPreferencesUtil(getActivity());
        settingsPreferences = getActivity().getSharedPreferences(Constants.SETTINGS_PREFERENCES, Context.MODE_PRIVATE);

        setupInitialSettings();
        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onDestroyView() {
        Log.d("FragmentLock", "onDestroyView");

        inputMethodManager(INPUT_METHOD_HIDE, etPassword);

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d("FragmentLock", "onDestroy");

        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d("FragmentLock", "onDetach");

        super.onDetach();
    }

    @Override
    public void onResume() {
        Log.d("FragmentLock", "onResume");

        super.onResume();

        etPassword.setText("");
        etPin.setText("");
        patternView.clearPattern();

        if (isNecessaryShowInput) {
            isNecessaryShowInput = false;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Integer lastMethodUsed = preferencesUtil.getInt(settingsPreferences, R.string.last_unlock_method, vfUnlockMethods.getDisplayedChild());
                            vfUnlockMethods.setDisplayedChild(lastMethodUsed);
                            displayInput(vfUnlockMethods.getCurrentView().getId());
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    public void onStop() {
        Log.d("FragmentLock", "onStop");

        super.onStop();

        isNecessaryShowInput = true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ATE.apply(this, null);

        enableFingerPrintIfNecessary();
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        Log.d("FragmentLock", "onViewStateRestored");

        super.onViewStateRestored(savedInstanceState);
    }

    public void setupInitialSettings() {
        enableFingerPrintRecognizer = preferencesUtil.getBoolean(settingsPreferences, R.string.user_fingerprint, R.bool.user_fingerprint);
        isInStealthMode = preferencesUtil.getBoolean(settingsPreferences, R.string.is_pattern_visible, R.bool.is_pattern_visible);
        isSwipeEnabled = preferencesUtil.getBoolean(settingsPreferences, R.string.enable_swipe, R.bool.enable_swipe);
        storedPattern = preferencesUtil.getString(settingsPreferences, R.string.user_pattern, R.string.default_pattern);
        storedPassword = preferencesUtil.getString(settingsPreferences, R.string.user_password, R.string.default_code);
        storedPin = preferencesUtil.getString(settingsPreferences, R.string.user_pin, R.string.default_code);

        gestureDetector = new GestureDetector(getActivity(), new CustomGestureDetector());
        spass = new Spass();
    }

    public void enableFingerPrintIfNecessary() {
        try {
            spass.initialize(getActivity());
        } catch (SsdkUnsupportedException e) {
            Log.d("Finger", "Exception: " + e);
        } catch (UnsupportedOperationException e) {
            Log.d("Finger", "Fingerprint Service is not supported in the device");
        }

        if (!Util.isSamsungDevice(getActivity()) || !Util.isFingerprintEnabled(getActivity(), spass)) {
            for (int i = 0; i < vfUnlockMethods.getChildCount(); i++) {
                if (vfUnlockMethods.getChildAt(i).getId() == R.id.cv_fingerprint) {
                    vfUnlockMethods.removeViewAt(i);
                    break;
                }
            }
        } else {
            spassFingerprint = new SpassFingerprint(getActivity());
        }

        setListeners();
    }

    public void setListeners() {
        patternView.setInStealthMode(!isInStealthMode);
        patternView.setOnPatternListener(new PatternLockView.OnPatternListener() {
            @Override
            public void onPatternDetected(List<PatternLockView.Cell> pattern, String SimplePattern) {
                if (SimplePattern.equals(storedPattern)) {
                    playUnlockSound();

                    preferencesUtil.putValue(settingsPreferences, R.string.last_unlock_method, vfUnlockMethods.getDisplayedChild());

                    lunchVaultFragment();
                } else {
                    patternView.setDisplayMode(PatternLockView.DisplayMode.Wrong);

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    patternView.clearPattern();
                                }
                            });
                        }
                    }, Constants.DURATIONS_OF_ANIMATIONS);
                }
            }
        });

        pinLockView.setPinButtonClickedListener(new PinButtonClickedListener() {
            @Override
            public void onButtonClick(PinButtonEnum pinButtonEnum) {
                if (pinButtonEnum == PinButtonEnum.BUTTON_BACK) {
                    if (etPin.getText().length() != 0) {
                        etPin.setText(etPin.getText().toString().substring(0, etPin.getText().length() - 1));
                        etPin.setSelection(etPin.length());
                    }
                } else if (pinButtonEnum == PinButtonEnum.BUTTON_DONE) {
                    if (etPin.getText().toString().equals(storedPin)) {
                        playUnlockSound();

                        preferencesUtil.putValue(settingsPreferences, R.string.last_unlock_method, vfUnlockMethods.getDisplayedChild());

                        lunchVaultFragment();
                    } else {
                        YoYo.with(Techniques.Tada).duration(Constants.DURATIONS_OF_ANIMATIONS).playOn(etPin);
                    }
                } else {
                    String pinValue = String.valueOf(pinButtonEnum.getButtonValue());
                    etPin.setText(etPin.getText().toString().concat(pinValue));
                }
            }
        });

        vfUnlockMethods.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return !gestureDetector.onTouchEvent(event);
            }
        });
    }

    public void playUnlockSound() {
        try {
            AssetFileDescriptor assetFileDescriptor = getActivity().getAssets().openFd("sounds/unlock.ogg");
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void lunchVaultFragment() {
        Class fragmentClass;
        Fragment fragment = null;
        FragmentManager fragmentManager;

        fragmentClass = GalleryActivityFragment.class;

        try {
            fragment = (Fragment) fragmentClass.newInstance();

            Bundle bundle = new Bundle();
            bundle.putString("from", ARGS_VAULT);
            bundle.putString("path", getActivity().getFilesDir().getAbsolutePath());

            fragment.setArguments(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }

        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager
                .popBackStack();
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.gallery_container, fragment, GalleryActivityFragment.class.getName())
                .commit();
    }

    public void displayInput(int id) {
        switch (id) {
            case R.id.ll_pin:
                inputMethodManager(INPUT_METHOD_HIDE, etPassword);
                break;
            case R.id.cv_password:
                etPassword.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        etPassword.requestFocus();
                        inputMethodManager(INPUT_METHOD_SHOW, etPassword);
                    }
                }, 100);
                break;
            case R.id.cv_pattern:
                inputMethodManager(INPUT_METHOD_HIDE, etPassword);
                break;
            case R.id.cv_fingerprint:
                inputMethodManager(INPUT_METHOD_HIDE, etPassword);
                if (enableFingerPrintRecognizer) {
                    if (preferencesUtil.getInt(settingsPreferences, R.string.designated_finger, 0) == 0) {
                        displayFingerPrintRecognizer();
                    } else {
                        displayFingerPrintRecognizerWithIndex();
                    }
                }
                break;
        }
    }

    public void inputMethodManager(String type, EditText editText) {
        InputMethodManager methodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        switch (type) {
            case INPUT_METHOD_SHOW:
                methodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                break;
            case INPUT_METHOD_HIDE:
                methodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                break;
        }
    }

    public void displayFingerPrintRecognizer() {
        try {
            if (!spassFingerprint.hasRegisteredFinger()) {
                log("Please register finger first");
            } else {
                if (!onReadyIdentify) {
                    onReadyIdentify = true;
                    try {
                        spassFingerprint.startIdentifyWithDialog(getActivity(), listener, false);
                        log("Please identify finger to verify you");
                    } catch (IllegalStateException e) {
                        onReadyIdentify = false;
                        log("Exception: " + e);
                    }
                } else {
                    log("Please cancel Identify first");
                }
            }
        } catch (UnsupportedOperationException e) {
            log("Fingerprint Service is not supported in the device");
        }
    }

    public void displayFingerPrintRecognizerWithIndex() {
        try {
            if (!spassFingerprint.hasRegisteredFinger()) {
                log("Please register finger first");
            } else {
                if (!onReadyIdentify) {
                    onReadyIdentify = true;
                    if (spass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT_FINGER_INDEX)) {
                        ArrayList<Integer> designatedFingers = new ArrayList<>();
                        designatedFingers.add(preferencesUtil.getInt(settingsPreferences, R.string.designated_finger, 1));
                        try {
                            spassFingerprint.setIntendedFingerprintIndex(designatedFingers);
                        } catch (IllegalStateException ise) {
                            log(ise.getMessage());
                        }
                    }
                    try {
                        spassFingerprint.startIdentifyWithDialog(getActivity(), listener, false);
                        log("Please identify fingerprint index " + preferencesUtil.getInt(settingsPreferences, R.string.designated_finger, 1) + " to verify you");
                    } catch (IllegalStateException e) {
                        onReadyIdentify = false;
                        log("Exception: " + e);
                    }
                } else {
                    log("Please cancel Identify first");
                }
            }
        } catch (UnsupportedOperationException e) {
            log("Fingerprint Service is not supported in the device");
        }
    }

    public void log(String log) {
        Log.d(TAG, log);
    }

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_MIN_DISTANCE = 100;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            System.out.println(" in onFling() :: ");

            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                return false;
            }

            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY && isSwipeEnabled) {
                vfUnlockMethods.setInAnimation(getActivity(), R.anim.left_in);
                vfUnlockMethods.setOutAnimation(getActivity(), R.anim.left_out);
                vfUnlockMethods.showNext();

                displayInput(vfUnlockMethods.getCurrentView().getId());
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY && isSwipeEnabled) {
                vfUnlockMethods.setInAnimation(getActivity(), R.anim.right_in);
                vfUnlockMethods.setOutAnimation(getActivity(), R.anim.right_out);
                vfUnlockMethods.showPrevious();

                displayInput(vfUnlockMethods.getCurrentView().getId());
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
