package com.wewant.moovon.newsfbsdk.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommentModel {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    private String fromLogo;
    private String fromTitle;
    private Date date;
    private String comment;

    public static CommentModel load(JSONObject feedObj) throws JSONException {
        CommentModel model = new CommentModel();

        if (feedObj.has("created_time")) {
            model.setDate(feedObj.getString("created_time"));
        }
        if (feedObj.has("message")) {
            model.setComment(feedObj.getString("message"));
        }
        if (feedObj.has("from")) {
            JSONObject  from = feedObj.getJSONObject("from");
            if (from.has("name")) {
                model.setFromTitle(from.getString("name"));
            }
            try {
                model.setFromLogo(from.getJSONObject("picture")
                        .getJSONObject("data")
                        .getString("url"));
            } catch (JSONException e) {}
        }

        return model;
    }

    public String getFromLogo() {
        return fromLogo;
    }

    public void setFromLogo(String fromLogo) {
        this.fromLogo = fromLogo;
    }

    public String getFromTitle() {
        return fromTitle;
    }

    public void setFromTitle(String fromTitle) {
        this.fromTitle = fromTitle;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDate(String date) {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        try {
            this.date =  df.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
