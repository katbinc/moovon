package com.wewant.moovon.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarkFontTextView extends TextView {
    private static final String fontPath = "fonts/MarkLight.otf";
    private static final String fontBoldPath = "fonts/MarkBold.otf";

    public MarkFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MarkFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarkFontTextView(Context context) {
        super(context);
    }

    public void setTypeface(Typeface tf, int style) {
        if (style == Typeface.BOLD) {
            super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), fontBoldPath));
        } else {
            super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), fontPath));
        }
    }
}
