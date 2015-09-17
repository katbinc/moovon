package com.wewant.moovon.newsfbsdk.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FeedModel {

    private static final String TYPE_PHOTO = "photo";
    private static final String TYPE_VIDEO = "video";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    private String id;
    private String objectId;
    private String type;
    private String authorLogo;
    private String authorTitle;
    private String message;
    private String postTitle;
    private String postDescription;
    private Date postDateTime;
    private String postImage;
    private String postVideo;
    private String link;
    private boolean canLike = false;
    private boolean canComment = false;
    private int likesCount;
    private int commentsCount;
    private boolean isLiked = false;

    public static FeedModel load(JSONObject feedObj) throws JSONException {
        FeedModel model = new FeedModel();

        if (feedObj.has("id")) {
            model.setId(feedObj.getString("id"));
        }
        if (feedObj.has("type")) {
            model.setType(feedObj.getString("type"));
        }
        if (feedObj.has("object_id")) {
            model.setObjectId(feedObj.getString("object_id"));
        }
        if (feedObj.has("created_time")) {
            model.setPostDateTime(feedObj.getString("created_time"));
        }
        if (feedObj.has("name")) {
            model.setPostTitle(feedObj.getString("name"));
        }
        if (feedObj.has("description")) {
            model.setPostDescription(feedObj.getString("description"));
        }
        if (feedObj.has("message")) {
            model.setMessage(feedObj.getString("message"));
        }
        if (feedObj.has("link")) {
            model.setLink(feedObj.getString("link"));
        }
        if (feedObj.has("from")) {
            JSONObject  from = feedObj.getJSONObject("from");
            if (from.has("name")) {
                model.setAuthorTitle(from.getString("name"));
            }
            try {
                model.setAuthorLogo(from.getJSONObject("picture")
                    .getJSONObject("data")
                    .getString("url"));
            } catch (JSONException e) {}
        }
        try {
            model.setLikesCount(feedObj.getJSONObject("likes").getJSONObject("summary").getInt("total_count"));
        } catch (JSONException e) {}
        try {
            model.setIsLiked(feedObj.getJSONObject("likes").getJSONObject("summary").getBoolean("has_liked"));
        } catch (JSONException e) {}
        try {
            model.setCanLike(feedObj.getJSONObject("likes").getJSONObject("summary").getBoolean("can_like"));
        } catch (JSONException e) {}
        try {
            model.setCommentsCount(feedObj.getJSONObject("comments").getJSONObject("summary").getInt("total_count"));
        } catch (JSONException e) {}
        try {
            model.setCanComment(feedObj.getJSONObject("comments").getJSONObject("summary").getBoolean("can_comment"));
        } catch (JSONException e) {}

        return model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthorLogo() {
        return authorLogo;
    }

    public void setAuthorLogo(String authorLogo) {
        this.authorLogo = authorLogo;
    }

    public String getAuthorTitle() {
        return authorTitle;
    }

    public void setAuthorTitle(String authorTitle) {
        this.authorTitle = authorTitle;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public Date getPostDateTime() {
        return postDateTime;
    }

    public void setPostDateTime(String postDateTime) {
        // TODO fix bug
        DateFormat df = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        try {
            this.postDateTime =  df.parse(postDateTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPostDateTime(Date postDateTime) {
        this.postDateTime =  postDateTime;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostVideo() {
        return postVideo;
    }

    public void setPostVideo(String postVideo) {
        this.postVideo = postVideo;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public boolean isVideo() {
        return type.equals(TYPE_VIDEO);
    }

    public boolean isImage() {
        return type.equals(TYPE_PHOTO);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isCanLike() {
        return canLike;
    }

    public void setCanLike(boolean canLike) {
        this.canLike = canLike;
    }

    public boolean canComment() {
        return canComment;
    }

    public void setCanComment(boolean canComment) {
        this.canComment = canComment;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }
}
