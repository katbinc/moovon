package com.wewant.moovon.app.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.wewant.moovon.app.R;
import com.wewant.moovon.newsfbsdk.fragment.NewsListFragment;
import com.wewant.moovon.newsfbsdk.interfaces.FacebookCallbackInterface;

public class FacebookActivity extends AppCompatActivity implements FacebookCallbackInterface {
    private static final String TAG = FacebookActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new NewsListFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onStartCommenting(String entryName) {
        Log.d(TAG, "onStartCommenting: " + entryName);
    }

    @Override
    public void onShare(String entryName) {
        Log.d(TAG, "onShare: " + entryName);
    }
}
