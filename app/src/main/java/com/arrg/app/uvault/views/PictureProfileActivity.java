package com.arrg.app.uvault.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.appthemeengine.ATEActivity;
import com.afollestad.materialdialogs.MaterialDialog;
import com.arrg.app.uvault.Constants;
import com.arrg.app.uvault.R;
import com.arrg.app.uvault.util.FileUtils;
import com.arrg.app.uvault.util.SharedPreferencesUtil;
import com.arrg.app.uvault.util.Util;
import com.isseiaoki.simplecropview.CropImageView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PictureProfileActivity extends ATEActivity {

    private SharedPreferences settingsPreferences;
    private SharedPreferencesUtil preferencesUtil;
    private String chosenFile;

    @Bind(R.id.crop_image_view)
    CropImageView cropImageView;

    @Bind(R.id.iv_background)
    ImageView background;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @OnClick({R.id.fab})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                new SaveFileTask().execute();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        Util.hideActionBarUp(this, false);

        preferencesUtil = new SharedPreferencesUtil(this);
        settingsPreferences = getSharedPreferences(Constants.SETTINGS_PREFERENCES, Context.MODE_PRIVATE);

        Bundle bundle = getIntent().getExtras();

        chosenFile = bundle.getString(getString(R.string.background));

        Bitmap bitmap = BitmapFactory.decodeFile(chosenFile);

        background.setImageDrawable(new BitmapDrawable(getResources(), BlurEffectUtil.blur(this, bitmap, 25.f, 0.1f)));

        cropImageView.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
    }

    @Override
    protected void onDestroy() {
        System.gc();

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        FileUtils.deleteFile(chosenFile);

        Intent applicationListIntent = new Intent(this, MainActivity.class);

        Bundle bundle = new Bundle();
        bundle.putBoolean(getString(R.string.was_open_from_ublock_screen), true);

        applicationListIntent.putExtras(bundle);

        Util.openInverse(this, applicationListIntent, true);
    }

    class SaveFileTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            new MaterialDialog.Builder(PictureProfileActivity.this)
                    .content(R.string.saving_background)
                    .progress(true, 0)
                    .show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String fileName = getString(R.string.user_picture_name);

                    File file = new File(getExternalFilesDir(null), fileName);

                    FileUtils.deleteFile(chosenFile);

                    if (Util.saveWallpaper(cropImageView.getCroppedBitmap(), file)) {
                        preferencesUtil.putValue(settingsPreferences, R.string.user_picture, file.getAbsolutePath());
                    }
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Intent applicationListIntent = new Intent(PictureProfileActivity.this, MainActivity.class);

            Bundle bundle = new Bundle();
            bundle.putBoolean(getString(R.string.was_open_from_ublock_screen), true);

            applicationListIntent.putExtras(bundle);

            Util.openInverse(PictureProfileActivity.this, applicationListIntent, true);
        }
    }
}
