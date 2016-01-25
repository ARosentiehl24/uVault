package com.arrg.app.uvault.views;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.appthemeengine.ATE;
import com.arrg.app.uvault.Constants;
import com.arrg.app.uvault.R;
import com.arrg.app.uvault.adapter.UFolderAdapter;
import com.arrg.app.uvault.model.UFolder;
import com.arrg.app.uvault.util.FileUtils;
import com.arrg.app.uvault.util.SharedPreferencesUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FolderActivityFragment extends Fragment {

    private ArrayList<UFolder> folders;
    private ArrayList<File> fileArrayList;
    private SharedPreferences settingsPreferences;
    private SharedPreferencesUtil preferencesUtil;

    @Bind(R.id.rv_folders)
    RecyclerView foldersRecyclerView;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    public FolderActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("Fragment", "onCreateView");

        View root = inflater.inflate(R.layout.fragment_folder_activity, container, false);
        ButterKnife.bind(this, root);

        preferencesUtil = new SharedPreferencesUtil(getActivity());
        settingsPreferences = getActivity().getSharedPreferences(Constants.SETTINGS_PREFERENCES, Context.MODE_PRIVATE);

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d("Fragment", "onViewCreated");

        super.onViewCreated(view, savedInstanceState);
        ATE.apply(this, null);

        fileArrayList = new ArrayList<>();
        folders = new ArrayList<>();

        String selectedPath = preferencesUtil.getString(settingsPreferences, R.string.selected_path, FileUtils.getInternalStorage());
        File internalStorage = new File(selectedPath);

        if (internalStorage.listFiles() != null) {
            fileArrayList.add(internalStorage);
        }

        if (FileUtils.externalStorageExists()) {
            fileArrayList.add(new File(FileUtils.getExternalStorage()));
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fileArrayList.clear();
                folders.clear();

                String selectedPath = preferencesUtil.getString(settingsPreferences, R.string.selected_path, FileUtils.getInternalStorage());
                File internalStorage = new File(selectedPath);

                if (internalStorage.listFiles() != null) {
                    fileArrayList.add(internalStorage);
                }

                if (FileUtils.externalStorageExists()) {
                    fileArrayList.add(new File(FileUtils.getExternalStorage()));
                }

                new SearchFoldersTask().execute();
            }
        });

        new SearchFoldersTask().execute();
    }

    class SearchFoldersTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            long startTime = System.currentTimeMillis();

            for (File directory : fileArrayList) {
                if (directory.listFiles() != null) {
                    Log.d("Folder", directory.getAbsolutePath());
                    loadFolders(directory);
                }
            }

            File whatsAppImagesSent = new File(FileUtils.getInternalStorage() + "/WhatsApp/Media/WhatsApp Images/Sent");

            if (FileUtils.isFolderExist(whatsAppImagesSent.getAbsolutePath()) && havePictures(whatsAppImagesSent)) {
                folders.add(setNewFolder(whatsAppImagesSent));
            }

            long endTime = System.currentTimeMillis();

            long duration = (endTime - startTime);

            Log.d("Folder", "Duration: " + TimeUnit.MILLISECONDS.toSeconds(duration) + " seconds" + " - " + duration + " milliseconds");

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            UFolderAdapter uFolderAdapter = new UFolderAdapter(getActivity(), folders);

            foldersRecyclerView.setAdapter(uFolderAdapter);
            foldersRecyclerView.setHasFixedSize(true);
            foldersRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }

        public void loadFolders(File directory) {
            for (File folder : directory.listFiles()) {
                if (folder.isDirectory() && !folder.isHidden() && isNotAnExcludedFolder(folder) && folder.listFiles() != null) {
                    if (havePictures(folder)) {
                        folders.add(setNewFolder(folder));
                    } else {
                        loadFolders(folder);
                    }
                }
            }
        }

        public boolean isNotAnExcludedFolder(File folder) {
            return !folder.getName().equals("Android");
        }

        public boolean havePictures(File folder) {
            boolean sw = false;

            for (File file : folder.listFiles()) {
                if (file.getAbsolutePath().matches(FileUtils.JPG_REG)) {
                    sw = true;
                    break;
                } else {
                    sw = false;
                }
            }

            return sw;
        }

        public UFolder setNewFolder(File folder) {
            UFolder uFolder = new UFolder();

            uFolder.setPath(folder.getAbsolutePath());

            int numberOfFiles = 0;

            for (File file : folder.listFiles()) {
                if (file.getAbsolutePath().matches(FileUtils.JPG_REG)) {
                    numberOfFiles++;

                    if (numberOfFiles == 1) {
                        uFolder.setFolderName(FileUtils.getParentName(file.getAbsolutePath()));
                        uFolder.setPathOfImage(file.getAbsolutePath());
                    }
                }
            }

            uFolder.setNumberOfFiles(numberOfFiles);

            return uFolder;
        }
    }
}
