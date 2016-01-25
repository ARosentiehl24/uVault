package com.arrg.app.uvault.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.arrg.app.uvault.R;
import com.arrg.app.uvault.model.UFile;
import com.arrg.app.uvault.util.FileUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ooo.oxo.library.widget.TouchImageView;

/*
 * Created by albert on 20/01/2016.
 */
public class ViewPagerAdapter extends PagerAdapter {

    private ArrayList<UFile> imageViews;
    private Activity activity;

    public ViewPagerAdapter(ArrayList<UFile> imageViews, Activity activity) {
        this.imageViews = imageViews;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return imageViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.file, container, false);

        TouchImageView imageView = (TouchImageView) itemView.findViewById(R.id.picture);

        imageView.setTransitionName(FileUtils.getFileNameWithoutExtension(imageViews.get(position).getPath()));

        Glide.with(activity).load(imageViews.get(position).getPath()).into(imageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    public ImageView getItem(int position) {
        ImageView imageView = new ImageView(activity);

        Glide.with(activity).load(imageViews.get(position).getPath()).crossFade().into(imageView);

        return imageView;
    }
}
