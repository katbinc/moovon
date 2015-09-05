package by.katbinc.moovon.manager;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StreamManager {
    private static final String TAG = StreamManager.class.getSimpleName();
    /**
     * start next track before previous ended, ms
     */
    private static final int START_NEXT_BEFORE_END = 3000;

    private class PauseTimeData {
        long time;
        int position;
        int duration;

        PauseTimeData(int duration, int position) {
            this.duration = duration;
            this.position = position;
            time = System.currentTimeMillis();
        }

        boolean isExpired() {
            return getNewPosition() > duration - START_NEXT_BEFORE_END;
        }

        int getNewPosition() {
            return (int) (position + System.currentTimeMillis() - time);
        }
    }

    private static StreamManager instance;

    private Context mContext;

    private MediaPlayer player;

    private Runnable playNextListener;
    private PlayerPositionListener positionListener;
    private MediaPlayer.OnErrorListener playerErrorListener;

    private ScheduledExecutorService positionSchedule;
    private Handler monitorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            monitorPlayerPosition();
        }
    };

    enum PlayerState {
        Idle, Initialized, Prepared, Started, Paused,
        Stopped, PlaybackCompleted, End, Error, Preparing}

    PlayerState playerState;

    private PauseTimeData pauseTimeData;

    private StreamManager(Context context) {
        mContext = context;

        positionSchedule = Executors.newScheduledThreadPool(1);
        positionSchedule.scheduleWithFixedDelay(
            new Runnable(){
                @Override
                public void run() {
                    monitorHandler.sendMessage(monitorHandler.obtainMessage());
                }
            },
            200,
            200,
            TimeUnit.MILLISECONDS
        );

        new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer player, int what, int extra) {
                playerState = PlayerState.Error;
                if (playerErrorListener != null) {
                    playerErrorListener.onError(player, what, extra);
                }
                return false;
            }
        };
    }

    public static StreamManager getInstance(Context context) {
        if (instance == null) {
            instance = new StreamManager(context);
        }
        return instance;
    }

    public void play(String url) {
        Log.d(TAG, "Play");
        if (player == null || !player.isPlaying()) {
            if (pauseTimeData != null && !pauseTimeData.isExpired()) {
                Log.d(TAG, "Resume");
                player.seekTo(pauseTimeData.getNewPosition());
                player.start();
            } else {
                playNext(url);
            }
            pauseTimeData = null;
        }
    }

    public void playNext(String url) {
        Log.d(TAG, "Play next");
        prepare(url, new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                playerState = PlayerState.Prepared;
                player.start();
                playerState = PlayerState.Started;
            }
        });
        if (playNextListener != null) {
            playNextListener.run();
        }
    }

    public void pause() {
        Log.d(TAG, "Pause");
        if (player != null && player.isPlaying()) {
            pauseTimeData = new PauseTimeData(player.getDuration(), player.getCurrentPosition());
            player.pause();
            playerState = PlayerState.Paused;
        }
    }

    private void prepare(String url, MediaPlayer.OnPreparedListener listener) {
        player = new MediaPlayer();
        try {
            player.setDataSource(url);
            playerState = PlayerState.Initialized;
        } catch (Exception e) {
            Log.e(TAG, "Play error", e);
        }
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        Log.d(TAG, "prepareAsync");
        player.setOnPreparedListener(listener);
        player.prepareAsync();
    }

    public void release() {
        if (player != null) {
            try {
                player.release();
                player = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void monitorPlayerPosition() {
        if (positionListener != null) {
            if ( player == null) {
                positionListener.onPositionUpdated(0, 0);
            } else if (player.isPlaying()) {
                positionListener.onPositionUpdated(player.getCurrentPosition(), player.getDuration());
            }
        }
    }

    public void onDestroy() {
        release();
        positionSchedule.shutdown();
        positionSchedule = null;
        instance = null;
    }

    public StreamManager setPositionListener(PlayerPositionListener positionListener) {
        this.positionListener = positionListener;
        return this;
    }

    public StreamManager setPlayNextListener(Runnable playNextListener) {
        this.playNextListener = playNextListener;
        return this;
    }

    public StreamManager setPlayerErrorListener(MediaPlayer.OnErrorListener playerErrorListener) {
        this.playerErrorListener = playerErrorListener;
        return this;
    }

    public interface PlayerPositionListener {
        void onPositionUpdated(int position, int duration);
    }
}
