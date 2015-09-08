package com.wewant.moovon.model;

public class TrackInfoModel {
    private static final String SEPARATOR = "####@##";

    private String streamTitle;
    private String artist;
    private String songTitle;

    public String getStreamTitle() {
        return streamTitle;
    }

    public void setStreamTitle(String streamTitle) {
        this.streamTitle = streamTitle;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public TrackInfoModel loadFromString(String info) {
        String[] parts = info.split(SEPARATOR);
        int size = parts.length;

        if (size > 0) setStreamTitle(parts[0]);
        if (size > 1) setArtist(parts[1]);
        if (size > 2) setSongTitle(parts[2]);

        return this;
    }
}
