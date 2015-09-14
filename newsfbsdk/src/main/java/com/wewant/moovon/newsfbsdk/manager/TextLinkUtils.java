package com.wewant.moovon.newsfbsdk.manager;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.CharacterStyle;

import com.wewant.moovon.newsfbsdk.model.CustomURLSpan;

public class TextLinkUtils {
    public static <A extends CharacterStyle, B extends CharacterStyle> Spannable replaceAll
            (Spanned original, Class<A> sourceType, SpanConverter<A, B> converter,
             final CustomURLSpan.OnClickListener listener) {
        SpannableString result = new SpannableString(original);
        A[] spans = result.getSpans(0, result.length(), sourceType);

        for (A span : spans) {
            int start = result.getSpanStart(span);
            int end = result.getSpanEnd(span);
            int flags = result.getSpanFlags(span);

            result.removeSpan(span);
            result.setSpan(converter.convert(span, listener), start, end, flags);
        }

        return (result);
    }

    public interface SpanConverter<A extends CharacterStyle, B extends CharacterStyle> {
        B convert(A span, CustomURLSpan.OnClickListener listener);
    }
}