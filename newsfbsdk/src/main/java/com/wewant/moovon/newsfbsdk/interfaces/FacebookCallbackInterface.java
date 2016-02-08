package com.wewant.moovon.newsfbsdk.interfaces;

public interface FacebookCallbackInterface {

    /**
     * notify when comment button clicked
     */
    void onStartCommenting(String entryName);

    /**
     * notify when share button clicked
     */
    void onShare(String entryName);
}
