package com.wewant.moovon.newsfbsdk.model;

import android.text.style.ClickableSpan;
import android.view.View;

public class CustomURLSpan extends ClickableSpan {
    private String url;
    private OnClickListener mListener;

    public CustomURLSpan(String url, OnClickListener mListener) {
        this.url = url;
        this.mListener = mListener;
    }

    @Override
    public void onClick(View widget) {
        if (mListener != null) mListener.onClick(url);
    }

    public interface OnClickListener {
        void onClick(String url);
    }
}