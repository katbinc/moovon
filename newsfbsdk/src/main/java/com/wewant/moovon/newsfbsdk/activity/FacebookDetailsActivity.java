package com.wewant.moovon.newsfbsdk.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.wewant.moovon.newsfbsdk.R;
import com.wewant.moovon.newsfbsdk.fragment.ImageFragment;

/**
 * Created on 14.09.2015.
 * (c) All rights reserved
 */
public class FacebookDetailsActivity extends AppCompatActivity {
    private static final String TAG = FacebookDetailsActivity.class.getSimpleName();

    private static final String ACTION_IMAGE = "action_image";
    private static final String ACTION_PAGE = "action_page";

    private static final String KEY_ACTION = "key_action";
    private static final String KEY_URL = "key_URL";

    public static void openImage(Context context, String url) {
        Intent intent = new Intent(context, FacebookDetailsActivity.class);
        intent.putExtra(KEY_ACTION, ACTION_IMAGE);
        intent.putExtra(KEY_URL, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void openPage(Context context, String url) {
        Intent intent = new Intent(context, FacebookDetailsActivity.class);
        intent.putExtra(KEY_ACTION, ACTION_PAGE);
        intent.putExtra(KEY_URL, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        if (intent == null) {
            throw new RuntimeException("intent is null");
        }
        String action = intent.getStringExtra(KEY_ACTION);
        String url = intent.getStringExtra(KEY_URL);
        switch (action) {
            case ACTION_IMAGE:
                showImage(url);
                break;
            case ACTION_PAGE:
                showPage(url);
                break;
        }
    }

    private void showImage(String url) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.sdk_container, ImageFragment.newInstance(url)).commit();
    }

    private void showPage(String url) {
    }
}
