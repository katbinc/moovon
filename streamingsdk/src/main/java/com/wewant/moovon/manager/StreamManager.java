package com.wewant.moovon.manager;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;
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
    private MediaPlayer nextPlayer;

    private Runnable playNextListener;
    private PlayerPositionListener positionListener;
    private MediaPlayer.OnErrorListener playerErrorListener;

    private ScheduledExecutorService positionSchedule;
    private Handler monitorHandler;

    enum PlayerState {
        Idle, Initialized, Prepared, Started, Paused,
        Stopped, PlaybackCompleted, End, Error, Preparing}

    PlayerState playerState;

    private PauseTimeData pauseTimeData;
    private String playedSrc;
    private boolean canChangePlayer = false;
    private boolean isNextPlayerInitialized = false;

    private StreamManager(Context context) {
        mContext = context;

        monitorHandler = new WeekHandler(this);
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
                playerState = PlayerState.Started;
            } else {
                playNext(url);
            }
            pauseTimeData = null;
        }
    }

    public void playNext(String url) {
        Log.d(TAG, "Play next");
        playedSrc = url;
        canChangePlayer = false;
        if (nextPlayer != null) {
            releasePlayer(nextPlayer);
        }
        player = new MediaPlayer();
        setListeners(player);
        prepare(player, url, new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                playerState = PlayerState.Prepared;
                player.start();
                playerState = PlayerState.Started;
                if (playNextListener != null) {
                    playNextListener.run();
                }
            }
        });
    }

    public void pause() {
        Log.d(TAG, "Pause");
        if (player != null && player.isPlaying()) {
            pauseTimeData = new PauseTimeData(player.getDuration(), player.getCurrentPosition());
            player.pause();
            playerState = PlayerState.Paused;
        }
    }

    private MediaPlayer prepare(MediaPlayer player, String url, MediaPlayer.OnPreparedListener listener) {
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
        return player;
    }

    public void release() {
        releasePlayer(player);
        releasePlayer(nextPlayer);
    }

    private void releasePlayer(MediaPlayer player) {
        Log.d(TAG, "Release player");
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
            try {
                if ( player == null) {
                    positionListener.onPositionUpdated(0, 0);
                } else if (player.isPlaying()) {
                    int position = player.getCurrentPosition();
                    int duration = player.getDuration();
                    positionListener.onPositionUpdated(player.getCurrentPosition(), player.getDuration());

                    if (duration - position > START_NEXT_BEFORE_END) {
                        isNextPlayerInitialized = false;
                    } else if (!isNextPlayerInitialized) {
                        Log.d(TAG, "Init next player");
                        isNextPlayerInitialized = true;
                        nextPlayer = new MediaPlayer();
                        setListeners(nextPlayer);
                        prepare(nextPlayer, this.playedSrc, new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(final MediaPlayer player) {
                                Log.d(TAG, "Next player prepared");
                                if (canChangePlayer) {
                                    startNextPlayer();
                                } else {
                                    StreamManager.this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            Log.d(TAG, "Play completed");
                                            startNextPlayer();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Monitor player position error", e);
            }
        }
    }

    public void onDestroy() {
        positionSchedule.shutdown();
        if (monitorHandler != null) {
            monitorHandler.removeCallbacksAndMessages(null);
        }
        release();
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

    private void startNextPlayer() {
        Log.d(TAG, "Start next player");
        canChangePlayer = false;
        releasePlayer(player);
        player = nextPlayer;
        nextPlayer = null;
        player.start();
        playerState = PlayerState.Started;
        if (playNextListener != null) {
            playNextListener.run();
        }
    }

    private void setListeners(MediaPlayer player) {
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "Play completed");
                canChangePlayer = true;
            }
        });
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e(TAG, "Player error");
                playerState = PlayerState.Error;
                if (playerErrorListener != null) {
                    playerErrorListener.onError(mp, what, extra);
                }
                return false;
            }
        });
    }

    public interface PlayerPositionListener {
        void onPositionUpdated(int position, int duration);
    }

    static class WeekHandler extends Handler {
        WeakReference<StreamManager> wrManager;

        public WeekHandler(StreamManager manager) {
            wrManager = new WeakReference<StreamManager>(manager);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            StreamManager manager = wrManager.get();
            if (manager != null) {
                manager.monitorPlayerPosition();
            }
        }
    }
}
