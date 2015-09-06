package by.katbinc.moovon.fragment;

import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import by.katbinc.moovon.R;
import by.katbinc.moovon.manager.StreamManager;
import by.katbinc.moovon.model.TrackInfoModel;
import by.katbinc.moovon.transport.HttpTransport;

public class StreamFragment extends Fragment {
    public static final String TAG = StreamFragment.class.getSimpleName();

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
    private String urlStream;
    private String coverSrc;
    private String description;

    private StreamManager streamManager;
    private AudioManager audioManager;

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
        coverSrc = bundle.getString("coverSrc");
        description = bundle.getString("description");

        urlStream = bundle.getString("streamUrl");
        urlTrackSrc = HttpTransport.getTrackSrcUrl(urlStream);

        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        volume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        setListeners();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Glide.with(mContext).load(coverSrc).into(coverView);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        streamManager.onDestroy();
        streamManager = null;
        super.onDestroy();
    }

    private void setListeners() {
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                streamManager.play(urlTrackSrc);
                btnPlay.setVisibility(View.INVISIBLE);
                btnStop.setVisibility(View.VISIBLE);
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                streamManager.pause();
                btnStop.setVisibility(View.INVISIBLE);
                btnPlay.setVisibility(View.VISIBLE);
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
                transport.loadTrackInfo(urlStream, new HttpTransport.OnTrackInfoLoadListener() {
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
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }
        });
    }
}
