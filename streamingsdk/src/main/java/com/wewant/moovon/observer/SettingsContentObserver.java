package com.wewant.moovon.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;

abstract public class SettingsContentObserver extends ContentObserver {
    int previousVolume;
    Context mContext;

    public SettingsContentObserver(Context context, Handler handler) {
        super(handler);
        mContext = context;

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        previousVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

}