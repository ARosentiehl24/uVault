package com.arrg.app.uvault.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.afollestad.appthemeengine.ATE;
import com.arrg.app.uvault.R;
import com.arrg.app.uvault.model.UFile;
import com.arrg.app.uvault.util.FileUtils;
import com.arrg.app.uvault.views.GalleryActivityFragment;
import com.arrg.app.uvault.views.MainActivity;
import com.arrg.app.uvault.views.ViewPagerActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.ButterKnife;

import static com.arrg.app.uvault.Constants.*;

/*
 * Created by albert on 14/01/2016.
 */
public class UFileAdapter extends RecyclerView.Adapter<UFileAdapter.ViewHolder> {

    private ArrayList<String> transitionNames;
    private ArrayList<UFile> files;
    private Boolean isSelectable = false;
    private FragmentActivity activity;

    public UFileAdapter(FragmentActivity activity, ArrayList<UFile> files) {
        this.activity = activity;
        this.files = files;

        transitionNames = new ArrayList<>();

        for (UFile uFile : files) {
            transitionNames.add(FileUtils.getFileNameWithoutExtension(uFile.getPath()));
        }
    }

    @Override
    public UFileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        View files = inflater.inflate(R.layout.files, parent, false);

        return new ViewHolder(files);
    }

    @Override
    public void onBindViewHolder(UFileAdapter.ViewHolder holder, int position) {
        UFile uFile = files.get(position);

        Glide.with(activity).load(uFile.getPath()).thumbnail(0.5f).crossFade().into(holder.picture);

        holder.checkBox.setChecked(uFile.getIsSelected());
        holder.checkBox.setVisibility(isSelectable ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void addFile(UFile uFile, int position) {
        files.add(uFile);
        notifyItemInserted(position);
    }

    public void removeFile(int position) {
        files.remove(position);
        notifyItemRemoved(position);
    }

    public void setIsSelectable(Boolean isSelectable) {
        this.isSelectable = isSelectable;
    }

    public void unCheckFiles(int position) {
        files.get(position).setIsSelected(false);
    }

    public UFile getItem(int position) {
        return files.get(position);
    }

    public String getTransitionName(int position) {
        return transitionNames.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public CheckBox checkBox;
        public ImageView picture;
        //private VideoView video;

        public ViewHolder(View itemView) {
            super(itemView);
            ATE.apply(itemView, null);

            checkBox = ButterKnife.findById(itemView, R.id.checkbox);
            picture = ButterKnife.findById(itemView, R.id.picture);
            //video = ButterKnife.findById(itemView, R.id.video);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            UFile uFile = files.get(getLayoutPosition());

            if (isSelectable) {
                uFile.setIsSelected(!checkBox.isChecked());

                checkBox.setChecked(!checkBox.isChecked());
            } else {
                Intent viewPager = new Intent(activity, ViewPagerActivity.class);

                Bundle bundle = new Bundle();

                bundle.putInt(EXTRA_STARTING_ITEM_POSITION, getLayoutPosition());
                bundle.putSerializable(IMAGES, files);

                viewPager.putExtras(bundle);

                picture.setTransitionName(FileUtils.getFileNameWithoutExtension(uFile.getPath()));

                activity.startActivity(viewPager, ActivityOptions.makeSceneTransitionAnimation(activity, picture, picture.getTransitionName()).toBundle());
            }
        }


        @Override
        public boolean onLongClick(View v) {
            isSelectable = true;

            GalleryActivityFragment galleryActivityFragment = (GalleryActivityFragment) activity.getSupportFragmentManager().findFragmentByTag(GalleryActivityFragment.class.getName());
            galleryActivityFragment.setCheckBoxVisibilityTo(View.VISIBLE);

            MainActivity mainActivity = (MainActivity) activity;
            mainActivity.setIsSelectable(isSelectable);

            return false;
        }
    }
}
