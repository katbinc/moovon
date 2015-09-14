package com.wewant.moovon.newsfbsdk.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.wewant.moovon.newsfbsdk.R;

public class WebViewFragment extends Fragment {
    public static final String TAG = WebViewFragment.class.getSimpleName();

    private static final String KEY_URL = "url";

    public static WebViewFragment newInstance(String url) {
        WebViewFragment result = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(KEY_URL, url);
        result.setArguments(args);
        return result;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);

        String url = getArguments().getString(KEY_URL);
        WebView webview = (WebView) view.findViewById(R.id.webview);

//        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(url);
        return view;
    }

}
