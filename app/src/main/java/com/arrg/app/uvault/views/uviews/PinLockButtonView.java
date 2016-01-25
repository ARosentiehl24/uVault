package com.arrg.app.uvault.views.uviews;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arrg.app.uvault.R;

/*
 * Created by albert on 26/12/2015.
 */
public class PinLockButtonView extends RelativeLayout {

    private Context mContext;

    public PinLockButtonView(Context context) {
        this(context, null);
    }

    public PinLockButtonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinLockButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        if (attrs != null && !isInEditMode()) {
            final TypedArray attributes = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.PinLockButtonView, defStyleAttr, 0);

            String text = attributes.getString(R.styleable.PinLockButtonView_keyboard_button_text);
            Drawable image = attributes.getDrawable(R.styleable.PinLockButtonView_keyboard_button_image);

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            PinLockButtonView view = (PinLockButtonView) inflater.inflate(R.layout.view_keyboard_button, this);

            if (text != null) {
                TextView textView = (TextView) view.findViewById(R.id.keyboard_button_textView);
                if (textView != null) {
                    textView.setText(text);
                    textView.setTextColor(PinLockView.color);
                }
            }

            if (image != null) {
                ImageView imageView = (ImageView) view.findViewById(R.id.keyboard_button_imageView);
                if (imageView != null) {
                    imageView.setImageDrawable(image);
                    imageView.getDrawable().setTintList(ColorStateList.valueOf(PinLockView.color));
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        onTouchEvent(event);

        return false;
    }
}
