package com.arrg.app.uvault.views.uviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.afollestad.appthemeengine.Config;
import com.arrg.app.uvault.R;
import com.arrg.app.uvault.controller.PinButtonClickedListener;
import com.arrg.app.uvault.controller.PinButtonEnum;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by albert on 28/12/2015.
 */
public class PinLockView extends RelativeLayout implements View.OnClickListener {

    private Context mContext;
    public static Integer color;
    private List<PinLockButtonView> mButtons;
    private PinButtonClickedListener mPinButtonClickedListener;

    public PinLockView(Context context) {
        this(context, null);
    }

    public PinLockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs,  int defStyleAttr) {
        if (attrs != null && !isInEditMode()) {
            final TypedArray attributes = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.PinLockView, defStyleAttr, 0);

            color = attributes.getColor(R.styleable.PinLockView_keyboard_button_color, Config.primaryColorDark(mContext, null));

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            PinLockView view = (PinLockView) inflater.inflate(R.layout.view_keyboard, this);

            initButtons(view);
        }
    }

    private void initButtons(PinLockView view) {
        mButtons = new ArrayList<>();

        mButtons.add((PinLockButtonView) view.findViewById(R.id.pin_code_button_0));
        mButtons.add((PinLockButtonView) view.findViewById(R.id.pin_code_button_1));
        mButtons.add((PinLockButtonView) view.findViewById(R.id.pin_code_button_2));
        mButtons.add((PinLockButtonView) view.findViewById(R.id.pin_code_button_3));
        mButtons.add((PinLockButtonView) view.findViewById(R.id.pin_code_button_4));
        mButtons.add((PinLockButtonView) view.findViewById(R.id.pin_code_button_5));
        mButtons.add((PinLockButtonView) view.findViewById(R.id.pin_code_button_6));
        mButtons.add((PinLockButtonView) view.findViewById(R.id.pin_code_button_7));
        mButtons.add((PinLockButtonView) view.findViewById(R.id.pin_code_button_8));
        mButtons.add((PinLockButtonView) view.findViewById(R.id.pin_code_button_9));
        mButtons.add((PinLockButtonView) view.findViewById(R.id.pin_code_button_back));
        mButtons.add((PinLockButtonView) view.findViewById(R.id.pin_code_button_done));

        for (PinLockButtonView button : mButtons) {
            button.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (mPinButtonClickedListener == null) {
            return;
        }

        int id = v.getId();

        if (id == R.id.pin_code_button_0) {
            mPinButtonClickedListener.onButtonClick(PinButtonEnum.BUTTON_0);
        } else if (id == R.id.pin_code_button_1) {
            mPinButtonClickedListener.onButtonClick(PinButtonEnum.BUTTON_1);
        } else if (id == R.id.pin_code_button_2) {
            mPinButtonClickedListener.onButtonClick(PinButtonEnum.BUTTON_2);
        } else if (id == R.id.pin_code_button_3) {
            mPinButtonClickedListener.onButtonClick(PinButtonEnum.BUTTON_3);
        } else if (id == R.id.pin_code_button_4) {
            mPinButtonClickedListener.onButtonClick(PinButtonEnum.BUTTON_4);
        } else if (id == R.id.pin_code_button_5) {
            mPinButtonClickedListener.onButtonClick(PinButtonEnum.BUTTON_5);
        } else if (id == R.id.pin_code_button_6) {
            mPinButtonClickedListener.onButtonClick(PinButtonEnum.BUTTON_6);
        } else if (id == R.id.pin_code_button_7) {
            mPinButtonClickedListener.onButtonClick(PinButtonEnum.BUTTON_7);
        } else if (id == R.id.pin_code_button_8) {
            mPinButtonClickedListener.onButtonClick(PinButtonEnum.BUTTON_8);
        } else if (id == R.id.pin_code_button_9) {
            mPinButtonClickedListener.onButtonClick(PinButtonEnum.BUTTON_9);
        } else if (id == R.id.pin_code_button_back) {
            mPinButtonClickedListener.onButtonClick(PinButtonEnum.BUTTON_BACK);
        } else if (id == R.id.pin_code_button_done) {
            mPinButtonClickedListener.onButtonClick(PinButtonEnum.BUTTON_DONE);
        }
    }

    public Integer getColor() {
        return color;
    }

    public void setPinButtonClickedListener(PinButtonClickedListener pinButtonClickedListener) {
        this.mPinButtonClickedListener = pinButtonClickedListener;
    }
}
