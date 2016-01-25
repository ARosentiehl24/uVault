package com.arrg.app.uvault.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.appthemeengine.ATE;
import com.arrg.app.uvault.R;
import com.arrg.app.uvault.model.UFolder;
import com.arrg.app.uvault.views.GalleryActivityFragment;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.ButterKnife;

import static com.arrg.app.uvault.Constants.ARGS_FOLDER;

/*
 * Created by albert on 16/01/2016.
 */
public class UFolderAdapter extends RecyclerView.Adapter<UFolderAdapter.ViewHolder> {

    private FragmentActivity activity;
    private ArrayList<UFolder> folders;

    public UFolderAdapter(FragmentActivity activity, ArrayList<UFolder> folders) {
        this.activity = activity;
        this.folders = folders;
    }

    @Override
    public UFolderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        View folder = inflater.inflate(R.layout.folders, parent, false);

        return new ViewHolder(folder, activity, folders);
    }

    @Override
    public void onBindViewHolder(UFolderAdapter.ViewHolder holder, int position) {
        UFolder uFolder = folders.get(position);

        Glide.with(activity).load(uFolder.getPathOfImage()).thumbnail(0.1f).crossFade().into(holder.file);
        holder.folderName.setText(uFolder.getFolderName());
        holder.numberOfFiles.setText(String.format("(%d)", uFolder.getNumberOfFiles()));
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public void addFile(UFolder uFolder, Integer position) {
        folders.add(uFolder);
        notifyItemInserted(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private FragmentActivity activity;
        private ArrayList<UFolder> folders;

        private ImageView file;
        private TextView folderName;
        private TextView numberOfFiles;

        public ViewHolder(View itemView, FragmentActivity activity, ArrayList<UFolder> folders) {
            super(itemView);
            ATE.apply(itemView, null);

            file = ButterKnife.findById(itemView, R.id.file);
            folderName = ButterKnife.findById(itemView, R.id.folder_name);
            numberOfFiles = ButterKnife.findById(itemView, R.id.number_files);

            this.activity = activity;
            this.folders = folders;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            UFolder uFolder = folders.get(getLayoutPosition());

            Class fragmentClass;
            Fragment fragment = null;
            FragmentManager fragmentManager;

            fragmentClass = GalleryActivityFragment.class;

            try {
                fragment = (Fragment) fragmentClass.newInstance();

                Bundle bundle = new Bundle();
                bundle.putString("from", ARGS_FOLDER);
                bundle.putString("path", uFolder.getPath());

                fragment.setArguments(bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }

            fragmentManager = activity.getSupportFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.gallery_container, fragment, GalleryActivityFragment.class.getName())
                    .addToBackStack(GalleryActivityFragment.class.getName())
                    .commit();
        }
    }
}
