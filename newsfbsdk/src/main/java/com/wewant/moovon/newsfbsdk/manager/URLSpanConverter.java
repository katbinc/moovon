package com.wewant.moovon.newsfbsdk.manager;

import android.text.style.URLSpan;

import com.wewant.moovon.newsfbsdk.model.CustomURLSpan;

public class URLSpanConverter
        implements
        TextLinkUtils.SpanConverter<URLSpan, CustomURLSpan> {
    @Override
    public CustomURLSpan convert(URLSpan span, CustomURLSpan.OnClickListener listener) {
        return (new CustomURLSpan(span.getURL(), listener));
    }
}