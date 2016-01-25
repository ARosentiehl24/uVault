package com.arrg.app.uvault.views;

import android.app.SharedElementCallback;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.arrg.app.uvault.R;
import com.arrg.app.uvault.adapter.ViewPagerAdapter;
import com.arrg.app.uvault.model.UFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.arrg.app.uvault.Constants.*;

public class ViewPagerActivity extends AppCompatActivity {

    private boolean mIsReturning;
    private int currentPosition;
    private int startPosition;

    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mIsReturning) {
                ImageView sharedElement = ((ViewPagerAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
                if (sharedElement == null) {
                    names.clear();
                    sharedElements.clear();
                } else if (startPosition != currentPosition) {
                    names.clear();
                    names.add(sharedElement.getTransitionName());
                    sharedElements.clear();
                    sharedElements.put(sharedElement.getTransitionName(), sharedElement);
                }
            }
        }
    };

    @Bind(R.id.view_pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        ButterKnife.bind(this);

        setEnterSharedElementCallback(mCallback);

        Bundle bundle = getIntent().getExtras();

        startPosition = bundle.getInt(EXTRA_STARTING_ITEM_POSITION);
        if (savedInstanceState == null) {
            currentPosition = startPosition;
        } else {
            currentPosition = savedInstanceState.getInt(STATE_CURRENT_PAGE_POSITION);
        }

        ArrayList<UFile> imageViews = (ArrayList<UFile>) bundle.getSerializable(IMAGES);

        ViewPagerAdapter adapter = new ViewPagerAdapter(imageViews, this);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPosition);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_PAGE_POSITION, currentPosition);
    }

    @Override
    public void finishAfterTransition() {
        mIsReturning = true;

        Intent intentData = new Intent();

        intentData.putExtra(EXTRA_STARTING_ITEM_POSITION, startPosition);
        intentData.putExtra(EXTRA_CURRENT_ITEM_POSITION, currentPosition);

        setResult(RESULT_OK, intentData);

        super.finishAfterTransition();
    }
}
