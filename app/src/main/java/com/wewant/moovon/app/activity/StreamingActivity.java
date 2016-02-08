package com.wewant.moovon.app.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.wewant.moovon.app.R;
import com.wewant.moovon.fragment.StreamListFragment;
import com.wewant.moovon.interfaces.StreamingCallbackInterface;

public class StreamingActivity extends AppCompatActivity implements StreamingCallbackInterface {
    private static final String TAG = StreamingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new StreamListFragment()).commit();
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
    public void onStreamOpened(String streamName) {
        Log.d(TAG, "onStreamOpened: " + streamName);
    }

    @Override
    public void onStreamClosed(String streamName) {
        Log.d(TAG, "onStreamClosed: " + streamName);
    }

    @Override
    public void onStreamStarted(String streamName) {
        Log.d(TAG, "onStreamStarted: " + streamName);
    }

    @Override
    public void onStreamStopped(String streamName) {
        Log.d(TAG, "onStreamStopped: " + streamName);
    }
}
