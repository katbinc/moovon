package com.wewant.moovon.newsfbsdk.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.wewant.moovon.newsfbsdk.R;

public class WebViewFragment extends Fragment {
    public static final String TAG = WebViewFragment.class.getSimpleName();

    private static final String KEY_URL = "url";

    private ProgressBar preloader;

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

        preloader = (ProgressBar) view.findViewById(R.id.preloader);

        String url = getArguments().getString(KEY_URL);
        WebView webview = (WebView) view.findViewById(R.id.webview);
        webview.setWebViewClient(new SdkWebViewClient());
        webview.setWebChromeClient(new SdkWebChromeClient());
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(url);
        return view;
    }

    public class SdkWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }


    public class SdkWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int progress) {
            if (progress < 100) {
                preloader.setVisibility(View.VISIBLE);
                preloader.setProgress(progress);
            }

            if (progress == 100) {
                preloader.setVisibility(View.GONE);
            }
        }
    }

}
