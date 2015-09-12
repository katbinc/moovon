package com.wewant.moovon.newsfbsdk.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.wewant.moovon.newsfbsdk.R;
import com.wewant.moovon.newsfbsdk.adapter.FeedAdapter;
import com.wewant.moovon.newsfbsdk.manager.FbManager;
import com.wewant.moovon.newsfbsdk.model.FeedModel;
import com.wewant.moovon.newsfbsdk.view.EndlessListView;

import java.util.ArrayList;

public class NewsListFragment extends Fragment {
    public static final String TAG = NewsListFragment.class.getSimpleName();

    private Context mContext;
    private EndlessListView newsList;
    //    private NewsAdapter newsAdapter;
    private FeedAdapter mAdapter;
    private FbManager fbManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();

        FacebookSdk.sdkInitialize(mContext);

        View rootView = inflater.inflate(R.layout.fragment_news_list, null, false);
        newsList = (EndlessListView) rootView.findViewById(R.id.newsList);
        newsList.setOnMoreListener(new EndlessListView.OnMoreListener() {
            @Override
            public void onMoreAsked() {
                //TODO load next page
                loadNext();

            }
        });


        fbManager = FbManager.getInstance(mContext);
        buildNewsList();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    //TODO add pagination
    private void loadNext() {
        //TODO newsList.setLoadingMore(false); in callback after loading finished
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "Loading finished", Toast.LENGTH_SHORT).show();
                newsList.setLoadingMore(false);
            }
        }, 2000);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected void buildNewsList() {
//        newsAdapter = new NewsAdapter(mContext);
        mAdapter = new FeedAdapter(mContext);
        mAdapter.setOnLikeClickListener(new FeedAdapter.OnSocialBntClick() {

            @Override
            public void run(final int position, final View view) {
                if (fbManager.isLoggedIn()) {
                    fbManager.like(((FeedModel) mAdapter.getItem(position)).getId(), new FbManager.OnLikesLoadListener() {
                        @Override
                        public void onSuccess(int likesCount) {
                            ((FeedModel)mAdapter.getObject(position)).setLikesCount(likesCount);
                            mAdapter.invalidate();
                        }
                    });
                } else {
                    fbManager.login(NewsListFragment.this, new Runnable() {
                        @Override
                        public void run() {
                            fbManager.like(((FeedModel) mAdapter.getItem(position)).getId(), new FbManager.OnLikesLoadListener() {
                                @Override
                                public void onSuccess(int likesCount) {
                                    ((FeedModel)mAdapter.getObject(position)).setLikesCount(likesCount);
                                    mAdapter.invalidate();
                                }
                            });
                        }
                    });

                }
            }
        }).setOnCommentClickListener(new FeedAdapter.OnSocialBntClick() {
            @Override
            public void run(int position, View view) {

            }
        }).setOnShareClickListener(new FeedAdapter.OnSocialBntClick() {
            @Override
            public void run(int position, View view) {

            }
        });

        newsList.setAdapter(mAdapter);
        fbManager.loadFeed(new FbManager.OnFeedLoadListener() {
            @Override
            public void onSuccess(ArrayList<FeedModel> news) {
//                NewsListFragment.this.newsAdapter.setObjects(news);
                NewsListFragment.this.mAdapter.setObjects(news);
            }

            @Override
            public void onError(Exception e) {
                // TODO
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbManager.onActivityResult(requestCode, resultCode, data);
    }
}
