package com.wewant.moovon.newsfbsdk.manager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.wewant.moovon.newsfbsdk.model.CommentModel;
import com.wewant.moovon.newsfbsdk.model.FeedModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FbManager {

    private static final String TAG = FbManager.class.getSimpleName();
    private static final String CONFIG_PAGE = "com.wewant.moovon.newsfbsdk.PageId";
    private static final String CONFIG_TOKEN = "com.wewant.moovon.newsfbsdk.AppToken";
    private static final String CONFIG_APP_ID = "com.facebook.sdk.ApplicationId";

    private static final String IMAGE_SRC_PATTERN = "https://graph.facebook.com/%s/picture?type=normal&access_token=%s";

    private static final Integer ITEMS_PER_PAGE = 10;
    private static final Integer COMMENTS_COUNT = 255;
    private static final int START_PAGE = 1;
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");

    private static FbManager instance;
    private LoginManager loginManager;
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;

    private Context mContext;
    private String appId;
    private String pageId;
    private int currentPage = START_PAGE;
    private AccessTokenTracker tokenTracker;
    private Runnable onTokenChangedListener;

    private FbManager(Context context) {
        mContext = context;
        try {
            ApplicationInfo ai = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            pageId = bundle.getString(CONFIG_PAGE);
            appId = bundle.getString(CONFIG_APP_ID);
            loginManager = LoginManager.getInstance();
            callbackManager = CallbackManager.Factory.create();


            tokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken old, AccessToken curr) {
                    Log.d(TAG, "onCurrentAccessTokenChanged");
                    if (onTokenChangedListener != null) {
                        onTokenChangedListener.run();
                    }
                }
            };

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
    }

    public static FbManager getInstance(Context context) {
        if (instance == null) {
            instance = new FbManager(context);
        }
        return instance;
    }

    public void loadFeed(final OnFeedLoadListener listener, boolean allPages) {
        int limit = ITEMS_PER_PAGE;
        int offset = (currentPage - 1) * ITEMS_PER_PAGE;
        if (allPages) {
            limit = currentPage * ITEMS_PER_PAGE;
            offset = 0;
        }

        Bundle parameters = new Bundle();
        parameters.putString("fields", getFeedFieldsQuery());
        parameters.putString("limit", Integer.toString(limit));
        parameters.putString("offset", Integer.toString(offset));

        GraphRequest request = new GraphRequest(
                getPublicToken(),
                "/" + pageId + "/feed",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d(TAG, "Facebook Feed request completed");
                        try {
                            if (response.getError() == null) {
                                listener.onSuccess(getNews(response.getJSONObject()));
                            } else {
                                throw new Exception("Facebook Feed request error");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Facebook Feed request error", e);
                            listener.onError(e);
                        }
                    }
                }
        );
        request.executeAsync();
    }

    public void loadFeedNext(final OnFeedLoadListener listener) {
        currentPage++;
        loadFeed(listener, false);
    }

    private String getFeedFieldsQuery() {
        return "object_id,type,message,picture,story,name"
                + ",status_type,source,caption,description,link"
                + ",created_time,from{picture,name}"
                + ",comments.limit(1).summary(true)"
                + ",likes.limit(1).summary(true)";
    }

    private ArrayList<FeedModel> getNews(JSONObject obj) {
        Log.d(TAG, "Process feed response");
        ArrayList<FeedModel> news = new ArrayList<>();
        try {
            if (obj.has("data")) {
                JSONArray data = obj.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject feedObj = data.getJSONObject(i);
                    FeedModel model = FeedModel.load(feedObj);
                    if (!model.isCorrupted()) {
                        if (model.isImage()) {
                            model.setPostImage(getImageByObjectId(model.getObjectId(), getPublicToken().getToken()));
                        }
                        news.add(model);
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Wrong json", e);
        }
        return news;
    }

    private static String getImageByObjectId(String objectId, String token) {
        return String.format(IMAGE_SRC_PATTERN, objectId, token);
    }

    private AccessToken getPublicToken() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token == null) {
            try {
                ApplicationInfo ai = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
                Bundle bundle = ai.metaData;
                token = new AccessToken(bundle.getString(CONFIG_TOKEN), appId, appId, null, null, null, null, null);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return token;
    }

    private AccessToken getUserToken() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token != null) {
            if (token.getPermissions().containsAll(PERMISSIONS)) {
                return token;
            }
        }
        return null;
    }

    public boolean isLoggedIn() {
        AccessToken token = getUserToken();
        return token != null && !token.isExpired();
    }

    public void login(Fragment fragment, final Runnable onLoggedIn) {
        Log.d(TAG, "login");
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Login onSuccess");
                if (isLoggedIn()) {
                    onLoggedIn.run();
                } else {
                    Toast.makeText(mContext, "Login error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Login onCancel");
                Toast.makeText(mContext, "Login canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e(TAG, "Login onEror", exception);
                Toast.makeText(mContext, "Login error", Toast.LENGTH_SHORT).show();
            }
        });

        loginManager.logInWithPublishPermissions(fragment, PERMISSIONS);

    }

    public void share(Fragment fragment, FeedModel model) {
        Log.d(TAG, "share");

        shareDialog = new ShareDialog(fragment);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d(TAG, "Share onSuccess");
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Share onCancel");
            }

            @Override
            public void onError(FacebookException e) {
                Log.e(TAG, "Share onError", e);
            }
        });

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent.Builder linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(model.getPostTitle())
                    .setContentDescription(model.getPostDescription());
            try {
                linkContent.setContentUrl(Uri.parse(model.getLink()));
            } catch (Exception e) {}
            shareDialog.show(linkContent.build());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void like(final FeedModel model, final OnLikesLoadListener listener) {
        HttpMethod method;
        if (model.isLiked()) {
            method = HttpMethod.DELETE;
        } else {
            method = HttpMethod.POST;
        }
        new GraphRequest(
                getUserToken(),
                "/" + model.getId() + "/likes",
                null,
                method,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d(TAG, "Like POST onCompleted");
                        if (response.getError() == null) {
                            Bundle parameters = new Bundle();
                            parameters.putString("summary", "true");
                            parameters.putString("limit", "1");
                            new GraphRequest(
                                    getUserToken(),
                                    "/" + model.getId() + "/likes",
                                    parameters,
                                    HttpMethod.GET,
                                    new GraphRequest.Callback() {
                                        public void onCompleted(GraphResponse response) {
                                            Log.d(TAG, "Likes GET onCompleted");
                                            if (response.getError() == null) {
                                                JSONObject obj = response.getJSONObject();
                                                try {
                                                    int count = obj.getJSONObject("summary").getInt("total_count");
                                                    listener.onSuccess(count);
                                                } catch (JSONException e) {
                                                }
                                            } else {
                                                Log.e(TAG, "Like request error: " + response.getError().getErrorMessage());
                                            }
                                        }
                                    }
                            ).executeAsync();
                        } else {
                            Log.e(TAG, "Like POST error: " + response.getError().getErrorMessage());
                        }
                    }
                }
        ).executeAsync();

    }




    public void loadLastComments(String objId, final OnCommentsLoadListener listener) {
        Bundle parameters = new Bundle();
        parameters.putString("fields", getCommentFieldsQuery());
        parameters.putString("limit", COMMENTS_COUNT.toString());
        parameters.putString("order", "reverse_chronological");

        GraphRequest request = new GraphRequest(
                getUserToken(),
                "/" + objId + "/comments",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d(TAG, "Comments onCompleted");
                        try {
                            if (response.getError() == null) {
                                listener.onSuccess(getComments(response.getJSONObject()));
                            } else {
                                throw new Exception("Facebook Comments request error");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Facebook Comments request error", e);
                            listener.onError(e);
                        }
                    }
                }
        );
        request.executeAsync();
    }

    private String getCommentFieldsQuery() {
        return "message,created_time,from{picture,name}";
    }

    private ArrayList<CommentModel> getComments(JSONObject obj) {
        Log.d(TAG, "Process comments response");
        ArrayList<CommentModel> comments = new ArrayList<>();
        try {
            if (obj.has("data")) {
                JSONArray data = obj.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject cObj = data.getJSONObject(i);
                    CommentModel model = CommentModel.load(cObj);
                    comments.add(model);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Wrong json", e);
        }
        return comments;
    }

    public void comment(String objId, String message, final Runnable onSuccessListener) {
        Bundle params = new Bundle();
        params.putString("message", message);

        new GraphRequest(
                getUserToken(),
                "/" + objId + "/comments",
                params,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d(TAG, "Comments POST onCompleted");
                        try {
                            if (response.getError() == null) {
                                onSuccessListener.run();
                            } else {
                                throw new Exception("Facebook Comments request error");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Facebook Comments request error", e);
                        }
                    }
                }
        ).executeAsync();

    }

    public void reset() {
        currentPage = START_PAGE;
    }

    public void setOnTokenChangedListener(Runnable onTokenChangedListener) {
        this.onTokenChangedListener = onTokenChangedListener;
    }

    public void destroy() {
        tokenTracker.stopTracking();
    }

    public interface OnFeedLoadListener {
        void onSuccess(ArrayList<FeedModel> news);

        void onError(Exception e);
    }

    public interface OnCommentsLoadListener {
        void onSuccess(ArrayList<CommentModel> comments);
        void onError(Exception e);
    }

    public interface OnLikesLoadListener {
        void onSuccess(int likesCount);
    }

}
