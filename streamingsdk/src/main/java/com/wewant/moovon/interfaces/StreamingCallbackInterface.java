package com.wewant.moovon.interfaces;

public interface StreamingCallbackInterface {

    /**
     * notify when stream fragment opened
     */
    void onStreamOpened(String streamName);

    /**
     * notify when stream fragment closed
     */
    void onStreamClosed(String streamName);

    /**
     * notify when stream start playing
     */
    void onStreamStarted(String streamName);

    /**
     * notify when stream stop playing
     */
    void onStreamStopped(String streamName);
}
