package com.arrg.app.uvault.views;

import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.ATEActivity;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.arrg.app.uvault.Constants;
import com.arrg.app.uvault.R;
import com.arrg.app.uvault.util.AppUtils;
import com.arrg.app.uvault.util.SharedPreferencesUtil;
import com.arrg.app.uvault.util.Util;
import com.sw926.imagefileselector.ImageFileSelector;
import com.turhanoz.android.reactivedirectorychooser.event.OnDirectoryCancelEvent;
import com.turhanoz.android.reactivedirectorychooser.event.OnDirectoryChosenEvent;
import com.turhanoz.android.reactivedirectorychooser.ui.OnDirectoryChooserFragmentInteraction;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.arrg.app.uvault.Constants.EXTRA_CURRENT_ITEM_POSITION;
import static com.arrg.app.uvault.Constants.EXTRA_STARTING_ITEM_POSITION;

public class MainActivity extends ATEActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static MainActivity mainActivity;

    private Boolean isSelectable = false;
    private Bundle mTmpReenterState;
    private ImageFileSelector imageFileSelector;
    private SharedPreferences settingsPreferences;
    private SharedPreferencesUtil preferencesUtil;

    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mTmpReenterState != null) {
                int startingPosition = mTmpReenterState.getInt(EXTRA_STARTING_ITEM_POSITION);
                int currentPosition = mTmpReenterState.getInt(EXTRA_CURRENT_ITEM_POSITION);
                if (startingPosition != currentPosition) {
                    GalleryActivityFragment galleryActivityFragment = (GalleryActivityFragment) getSupportFragmentManager().findFragmentByTag(GalleryActivityFragment.class.getName());

                    if (galleryActivityFragment != null) {
                        String newTransitionName = galleryActivityFragment.getTransitionName(currentPosition);
                        View newSharedElement = galleryActivityFragment.getRecyclerView().findViewWithTag(newTransitionName);
                        if (newSharedElement != null) {
                            names.clear();
                            names.add(newTransitionName);
                            sharedElements.clear();
                            sharedElements.put(newTransitionName, newSharedElement);
                        }
                    }
                }
                mTmpReenterState = null;
            } else {
                View navigationBar = findViewById(android.R.id.navigationBarBackground);
                View statusBar = findViewById(android.R.id.statusBarBackground);
                if (navigationBar != null) {
                    names.add(navigationBar.getTransitionName());
                    sharedElements.put(navigationBar.getTransitionName(), navigationBar);
                }
                if (statusBar != null) {
                    names.add(statusBar.getTransitionName());
                    sharedElements.put(statusBar.getTransitionName(), statusBar);
                }
            }
        }
    };

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

    @Bind(R.id.nav_view)
    NavigationView navigationView;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainActivity = this;

        applyTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setExitSharedElementCallback(mCallback);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        setupSharedPreferences();
        setupNavigationDrawer(navigationView);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        Log.d("Log", "onActivityReenter");

        mTmpReenterState = new Bundle(data.getExtras());

        GalleryActivityFragment galleryActivityFragment = (GalleryActivityFragment) getSupportFragmentManager().findFragmentByTag(GalleryActivityFragment.class.getName());

        if (galleryActivityFragment != null) {
            galleryActivityFragment.setupRecyclerView(mTmpReenterState);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageFileSelector.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (isSelectable) {
                isSelectable = false;

                GalleryActivityFragment galleryActivityFragment = (GalleryActivityFragment) getSupportFragmentManager().findFragmentByTag(GalleryActivityFragment.class.getName());
                galleryActivityFragment.setCheckBoxVisibilityTo(View.INVISIBLE);
                galleryActivityFragment.setIsSelectable(isSelectable);
            } else {
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawers();

        final MenuItem menuItem = item;

        drawer.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (menuItem.getItemId()) {
                    case R.id.nav_gallery:
                        Util.launchFragment(MainActivity.this, FolderActivityFragment.class, R.id.gallery_container, false, true);

                        menuItem.setChecked(true);
                        setTitle(menuItem.getTitle());
                        break;
                    case R.id.nav_vault:
                        //startService(new Intent(this, UVaultLockService.class));

                        Util.launchFragment(MainActivity.this, UVaultLockActivityFragment.class, R.id.gallery_container, false, true);

                        menuItem.setChecked(true);
                        setTitle(menuItem.getTitle());
                        break;
                    case R.id.nav_update:

                        break;
                    case R.id.nav_send:

                        break;
                    case R.id.nav_lic_open_source:

                        break;
                    case R.id.nav_developer:

                        break;
                    case R.id.nav_exit:
                        Util.closeInverse(MainActivity.this, true);
                        break;
                }
            }
        }, Constants.DURATIONS_OF_ANIMATIONS);

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imageFileSelector.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageFileSelector.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        imageFileSelector.onSaveInstanceState(outState);
    }

    public void applyTheme() {
        if (!isThemeEnabled()) {
            ATE.config(this, null)
                    .activityTheme(R.style.AppTheme)
                    .primaryColorRes(R.color.blue_grey_900)
                    .primaryColorDarkRes(R.color.blue_grey_950)
                    .accentColorRes(R.color.teal_500)
                    .navigationBarColorRes(R.color.blue_grey_950)
                    .coloredStatusBar(true)
                    .coloredNavigationBar(true)
                    .navigationViewThemed(true)
                    .navigationViewNormalIconRes(R.color.teal_500)
                    .textColorPrimaryRes(R.color.primary_text_default_light)
                    .textColorSecondaryRes(R.color.secondary_text_light)
                    .commit();
        }
    }

    public boolean isThemeEnabled() {
        return ATE.config(this, null).isConfigured();
    }

    public void setupSharedPreferences() {
        preferencesUtil = new SharedPreferencesUtil(this);
        settingsPreferences = getSharedPreferences(Constants.SETTINGS_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setupNavigationDrawer(NavigationView navigationView) {
        View header = navigationView.getHeaderView(0);

        FrameLayout container = ButterKnife.findById(header, R.id.container);

        TextView addPhoto = ButterKnife.findById(header, R.id.add_photo);

        TextView appVersion = ButterKnife.findById(header, R.id.app_version);
        appVersion.setText(String.format(getString(R.string.current_version), AppUtils.getVerName(this)));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getBoolean(getString(R.string.was_open_from_ublock_screen), false)) {
                drawer.openDrawer(GravityCompat.START);
            }
        }

        if (isPictureSelected()) {
            addPhoto.setVisibility(View.INVISIBLE);

            String chosenPicture = preferencesUtil.getString(settingsPreferences, R.string.user_picture, null);

            Bitmap bitmap = BitmapFactory.decodeFile(chosenPicture);
            container.setBackground(new BitmapDrawable(getResources(), bitmap));
        }

        imageFileSelector = new ImageFileSelector(mainActivity);

        imageFileSelector.setCallback(new ImageFileSelector.Callback() {
            @Override
            public void onSuccess(String chosenFile) {
                Intent editImageIntent = new Intent(mainActivity, PictureProfileActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.background), chosenFile);

                editImageIntent.putExtras(bundle);

                Util.openInverse(mainActivity, editImageIntent, true);
            }

            @Override
            public void onError() {

            }
        });

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Display display = getWindowManager().getDefaultDisplay();

                Point size = new Point();

                display.getSize(size);

                int width = size.x;
                int height = size.y;

                imageFileSelector.setQuality(80);

                imageFileSelector.setOutPutImageSize(width, height);

                new MaterialDialog.Builder(mainActivity).content(R.string.picture_source).positiveText(R.string.from_gallery).negativeText(R.string.from_camera)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                imageFileSelector.selectImage(mainActivity);
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                imageFileSelector.takePhoto(mainActivity);
                            }
                        })
                        .build().show();
            }
        });

        setupContent(navigationView);
    }

    public boolean isPictureSelected() {
        return (preferencesUtil.getString(settingsPreferences, R.string.user_picture, null) != null);
    }

    public void setupContent(NavigationView navigationView) {
        Util.launchFragment(this, FolderActivityFragment.class, R.id.gallery_container, false, false);

        navigationView.getMenu().getItem(0).setChecked(true);
        setTitle(navigationView.getMenu().getItem(0).getTitle());
    }

    public void setIsSelectable(Boolean isSelectable) {
        this.isSelectable = isSelectable;
    }
}