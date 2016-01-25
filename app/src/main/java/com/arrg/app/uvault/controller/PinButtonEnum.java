package com.arrg.app.uvault.controller;

/*
 * Created by albert on 28/12/2015.
 */
public enum PinButtonEnum {

    BUTTON_0(0),
    BUTTON_1(1),
    BUTTON_2(2),
    BUTTON_3(3),
    BUTTON_4(4),
    BUTTON_5(5),
    BUTTON_6(6),
    BUTTON_7(7),
    BUTTON_8(8),
    BUTTON_9(9),
    BUTTON_BACK(-1),
    BUTTON_DONE(-2);

    private int mButtonValue;

    PinButtonEnum(int value) {
        this.mButtonValue = value;
    }

    public int getButtonValue() {
        return mButtonValue;
    }
}
