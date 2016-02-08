package com.wewant.moovon.fragment;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wewant.moovon.R;
import com.wewant.moovon.interfaces.StreamingCallbackInterface;
import com.wewant.moovon.manager.StreamManager;
import com.wewant.moovon.model.PlayerStreamModel;
import com.wewant.moovon.model.TrackInfoModel;
import com.wewant.moovon.observer.SettingsContentObserver;
import com.wewant.moovon.transport.HttpTransport;

public class StreamFragment extends Fragment {
    public static final String TAG = StreamFragment.class.getSimpleName();

    private static final String ARG_STREAM_URL = "stream_url";
    private static final String ARG_STREAM_COVER = "stream_cover";
    private static final String ARG_STREAM_DESCRIPTION = "stream_description";
    private static final String ARG_STREAM_NAME = "stream_name";

    private Context mContext;

    //    private TextView streamTitle;
    private TextView artist;
    private TextView songTitle;
    private ImageView coverView;
    private ImageView btnPlay;
    private ImageView btnStop;
    private SeekBar timeline;
    private SeekBar volume;

    private String urlTrackSrc;

    private String streamUrl;
    private String streamCover;
    private String streamDescription;
    private String streamName;

    private SettingsContentObserver mSettingsContentObserver;

    private StreamManager streamManager;
    private AudioManager audioManager;

    private StreamingCallbackInterface callbackInterface;

    public static Bundle buildArguments(PlayerStreamModel stream) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_STREAM_URL, stream.getStreamUrl());
        bundle.putString(ARG_STREAM_COVER, stream.getCover().getSource());
        bundle.putString(ARG_STREAM_DESCRIPTION, stream.getDescription());
        bundle.putString(ARG_STREAM_NAME, stream.getTitle());
        return bundle;
    }

    public static StreamFragment newInstance(PlayerStreamModel stream) {
        StreamFragment streamFragment = new StreamFragment();
        streamFragment.setArguments(buildArguments(stream));
        return streamFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();
        streamManager = StreamManager.getInstance(mContext);

        View rootView = inflater.inflate(R.layout.fragment_stream, null, false);

//        streamTitle = (TextView) rootView.findViewById(R.id.streamTitle);
        artist = (TextView) rootView.findViewById(R.id.artist);
        songTitle = (TextView) rootView.findViewById(R.id.songTitle);
        coverView = (ImageView) rootView.findViewById(R.id.cover);
        btnPlay = (ImageView) rootView.findViewById(R.id.btnPlay);
        btnStop = (ImageView) rootView.findViewById(R.id.btnStop);
        timeline = (SeekBar) rootView.findViewById(R.id.timeline);
        timeline.setThumb(null);
        volume = (SeekBar) rootView.findViewById(R.id.volume);

        Bundle bundle = getArguments();

        streamCover = bundle.getString(ARG_STREAM_COVER);
        streamDescription = bundle.getString(ARG_STREAM_DESCRIPTION);
        streamUrl = bundle.getString(ARG_STREAM_URL);
        streamName = bundle.getString(ARG_STREAM_NAME);

        urlTrackSrc = HttpTransport.getTrackSrcUrl(streamUrl);

        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        volume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        setListeners();

        return rootView;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callbackInterface = (StreamingCallbackInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement StreamingCallbackInterface");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Glide.with(mContext).load(streamCover).into(coverView);

        if (callbackInterface != null) {
            callbackInterface.onStreamOpened(streamName);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (callbackInterface != null) {
            callbackInterface.onStreamClosed(streamName);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        streamManager.onDestroy();
        streamManager = null;
        mContext.getContentResolver().unregisterContentObserver(mSettingsContentObserver);
        super.onDestroy();
    }

    private void setListeners() {
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                streamManager.play(urlTrackSrc);
                btnPlay.setVisibility(View.INVISIBLE);
                btnStop.setVisibility(View.VISIBLE);

                if (callbackInterface != null) {
                    callbackInterface.onStreamStarted(streamName);
                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                streamManager.pause();
                btnStop.setVisibility(View.INVISIBLE);
                btnPlay.setVisibility(View.VISIBLE);

                if (callbackInterface != null) {
                    callbackInterface.onStreamStopped(streamName);
                }
            }
        });

        streamManager.setPositionListener(new StreamManager.PlayerPositionListener() {
            @Override
            public void onPositionUpdated(int position, int duration) {
                timeline.setMax(duration);
                timeline.setProgress(position);
            }
        }).setPlayNextListener(new Runnable() {
            @Override
            public void run() {
                HttpTransport transport = new HttpTransport(getActivity());
                transport.loadTrackInfo(streamUrl, new HttpTransport.OnTrackInfoLoadListener() {
                    @Override
                    public void onSuccess(TrackInfoModel trackInfo) {
                        Log.d(TAG, "Track info loaded");
//                        if (trackInfo.getStreamTitle() != null) {
//                            streamTitle.setText(trackInfo.getStreamTitle());
//                            streamTitle.setVisibility(View.VISIBLE);
//                        } else {
//                            streamTitle.setVisibility(View.INVISIBLE);
//                        }
                        if (trackInfo.getArtist() != null) {
                            artist.setText(trackInfo.getArtist());
                            artist.setVisibility(View.VISIBLE);
                        } else {
                            artist.setVisibility(View.INVISIBLE);
                        }
                        if (trackInfo.getSongTitle() != null) {
                            songTitle.setText(trackInfo.getSongTitle());
                            songTitle.setVisibility(View.VISIBLE);
                        } else {
                            songTitle.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Track info load error", e);
                    }
                });
            }
        });

        timeline.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int originalProgress;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                originalProgress = seekBar.getProgress();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int arg1, boolean fromUser) {
                if (fromUser) {
                    seekBar.setProgress(originalProgress);
                }
            }
        });

        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            }
        });

        mSettingsContentObserver = new SettingsContentObserver(mContext, new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                volume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            }
        };
        mContext.getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver);
    }
}
