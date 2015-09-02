package by.katbinc.moovon.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PlayerModel {
    private int id;
    private String type;

    @SerializedName("content")
    private ArrayList<PlayerStreamModel> streams;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<PlayerStreamModel> getStreams() {
        return streams;
    }

    public void setStreams(ArrayList<PlayerStreamModel> streams) {
        this.streams = streams;
    }
}
