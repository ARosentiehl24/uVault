package com.arrg.app.uvault.views;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.afollestad.appthemeengine.ATE;
import com.arrg.app.uvault.Constants;
import com.arrg.app.uvault.R;
import com.arrg.app.uvault.adapter.UFileAdapter;
import com.arrg.app.uvault.model.UFile;
import com.arrg.app.uvault.util.FileUtils;
import com.arrg.app.uvault.util.RandomUtils;
import com.arrg.app.uvault.util.SharedPreferencesUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.arrg.app.uvault.Constants.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryActivityFragment extends Fragment {

    private ArrayList<UFile> files;
    private File directory;
    private SharedPreferences settingsPreferences;
    private SharedPreferencesUtil preferencesUtil;
    private String from;
    private UFileAdapter uFileAdapter;

    @Bind(R.id.fab)
    FloatingActionButton floatingActionButton;

    @Bind(R.id.rv_files)
    RecyclerView filesRecyclerView;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @OnClick({R.id.fab})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                if (from.equals(ARGS_FOLDER)) {
                    new MoveFilesToVaultTask().execute();
                } else {
                    new MoveFilesToGalleryTask().execute();
                }
                break;
        }
    }

    public GalleryActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("FragmentGallery", "onCreateView");

        View root = inflater.inflate(R.layout.fragment_gallery_activity, container, false);
        ButterKnife.bind(this, root);

        preferencesUtil = new SharedPreferencesUtil(getActivity());
        settingsPreferences = getActivity().getSharedPreferences(Constants.SETTINGS_PREFERENCES, Context.MODE_PRIVATE);

        Bundle bundle = getArguments();

        if (bundle != null) {
            String path = bundle.getString("path");

            if (path != null) {
                directory = new File(path);
            }

            from = bundle.getString("from");

            if (from != null) {
                if (from.equals(ARGS_FOLDER)) {
                    floatingActionButton.setImageResource(R.drawable.ic_archive);
                } else {
                    floatingActionButton.setImageResource(R.drawable.ic_unarchive);
                }
            }
        }

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d("FragmentGallery", "onViewCreated");

        super.onViewCreated(view, savedInstanceState);
        ATE.apply(this, null);

        if (uFileAdapter == null) {

            files = new ArrayList<>();

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    files.clear();

                    if (from.equals(ARGS_FOLDER)) {
                        new LoadFolderFiles().execute();
                    } else {
                        new LoadVaultFiles().execute();
                    }

                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.setIsSelectable(false);
                }
            });

            if (from.equals(ARGS_FOLDER)) {
                new LoadFolderFiles().execute();
            } else {
                new LoadVaultFiles().execute();
            }
        }
    }

    public void setCheckBoxVisibilityTo(int visibility) {
        for (int i = 0; i < filesRecyclerView.getAdapter().getItemCount(); i++) {
            Log.d("Loop", "Value: " + i);

            RecyclerView.ViewHolder holder = filesRecyclerView.findViewHolderForAdapterPosition(i);

            if (holder != null) {
                CheckBox checkBox = ((UFileAdapter.ViewHolder) holder).checkBox;

                if (visibility == View.INVISIBLE) {
                    checkBox.setChecked(false);
                    uFileAdapter.unCheckFiles(i);
                }

                checkBox.setVisibility(visibility);
            } else {
                if (visibility == View.INVISIBLE) {
                    uFileAdapter.unCheckFiles(i);
                }
            }
        }
    }

    public void setIsSelectable(Boolean isSelectable) {
        uFileAdapter.setIsSelectable(isSelectable);
    }

    public void setupRecyclerView(Bundle mTmpReenterState) {
        int startingPosition = mTmpReenterState.getInt(EXTRA_STARTING_ITEM_POSITION);
        int currentPosition = mTmpReenterState.getInt(EXTRA_CURRENT_ITEM_POSITION);

        if (startingPosition != currentPosition) {
            filesRecyclerView.scrollToPosition(currentPosition);
        }
    }

    public RecyclerView getRecyclerView() {
        return filesRecyclerView;
    }

    public String getTransitionName(int position) {
        return uFileAdapter.getTransitionName(position);
    }

    class LoadFolderFiles extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            long startTime = System.currentTimeMillis();

            readFiles(directory);

            long endTime = System.currentTimeMillis();

            long duration = (endTime - startTime);

            Log.d("Folder", "Duration: " + TimeUnit.MILLISECONDS.toSeconds(duration) + " seconds" + " - " + duration + " milliseconds");

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            uFileAdapter = new UFileAdapter(getActivity(), files);

            filesRecyclerView.setAdapter(uFileAdapter);
            filesRecyclerView.setHasFixedSize(true);
            filesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }

        public void readFiles(File folder) {
            for (File file : folder.listFiles()) {
                String path = file.getAbsolutePath();

                if (!file.isDirectory() && isPicture(path)) {
                    files.add(new UFile(file.getAbsolutePath()));
                }
            }
        }

        public boolean isPicture(String path) {
            return path.toLowerCase().matches(FileUtils.JPG_REG);
        }

        public boolean isVideo(String path) {
            return path.toLowerCase().matches(FileUtils.MTV_REG);
        }
    }

    class LoadVaultFiles extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            long startTime = System.currentTimeMillis();

            readFiles(directory);

            long endTime = System.currentTimeMillis();

            long duration = (endTime - startTime);

            Log.d("Folder", "Duration: " + TimeUnit.MILLISECONDS.toSeconds(duration) + " seconds" + " - " + duration + " milliseconds");

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            uFileAdapter = new UFileAdapter(getActivity(), files);

            filesRecyclerView.setAdapter(uFileAdapter);
            filesRecyclerView.setHasFixedSize(true);
            filesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }

        public void readFiles(File folder) {
            for (File file : folder.listFiles()) {
                String key = file.getName();

                if (!file.isDirectory() && isFileHidden(key)) {
                    files.add(new UFile(file.getAbsolutePath()));
                }
            }
        }

        public boolean isFileHidden(String key) {
            return preferencesUtil.exists(settingsPreferences, key);
        }
    }

    class MoveFilesToGalleryTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    moveToGallery(FIRST_ELEMENT);
                }
            });
            return null;
        }

        public void moveToGallery(Integer position) {
            int i = position;

            if (i < uFileAdapter.getItemCount()) {
                if (uFileAdapter.getItem(i).getIsSelected()) {
                    UFile uFile = uFileAdapter.getItem(i);

                    File file = new File(uFile.getPath());

                    String key = file.getName();

                    FileUtils.moveFile(uFile.getPath(), preferencesUtil.getString(settingsPreferences, key, null));

                    preferencesUtil.deleteValue(settingsPreferences, key);

                    uFileAdapter.removeFile(i);
                } else {
                    i += 1;
                }
                moveToGallery(i);
            }
        }
    }

    class MoveFilesToVaultTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    moveToVault(FIRST_ELEMENT);
                }
            });
            return null;
        }

        public String setValidKey(String key) {
            if (preferencesUtil.exists(settingsPreferences, key)) {
                setValidKey(RandomUtils.getRandomNumbersAndLetters(25));
            }

            return key;
        }

        public void moveToVault(Integer position) {
            int i = position;

            if (i < uFileAdapter.getItemCount()) {
                if (uFileAdapter.getItem(i).getIsSelected()) {
                    UFile uFile = uFileAdapter.getItem(i);

                    String key = setValidKey(RandomUtils.getRandomNumbersAndLetters(25));
                    String value = uFile.getPath();

                    /*File from = new File(value);

                    File to = new File(value.replaceAll(FileUtils.getFileName(value), ".nomedia_" + key));

                    if (from.exists()) {
                        from.renameTo(to);
                    }

                    Log.d("Rename", value + " to " + from.getAbsolutePath());*/

                    FileUtils.moveFile(value, new File(getActivity().getFilesDir().getAbsolutePath(), key).getAbsolutePath());

                    preferencesUtil.putValue(settingsPreferences, key, value);

                    uFileAdapter.removeFile(i);
                } else {
                    i += 1;
                }
                moveToVault(i);
            }
        }
    }
}
