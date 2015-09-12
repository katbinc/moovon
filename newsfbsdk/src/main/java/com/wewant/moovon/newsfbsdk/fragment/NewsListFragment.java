package com.wewant.moovon.newsfbsdk.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.facebook.FacebookSdk;
import com.wewant.moovon.newsfbsdk.R;
import com.wewant.moovon.newsfbsdk.adapter.FeedAdapter;
import com.wewant.moovon.newsfbsdk.manager.FbManager;
import com.wewant.moovon.newsfbsdk.model.FeedModel;

import java.util.ArrayList;

public class NewsListFragment extends Fragment {
    public static final String TAG = NewsListFragment.class.getSimpleName();

    private Context mContext;
    private ListView newsList;
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
        newsList = (ListView) rootView.findViewById(R.id.newsList);

        fbManager = FbManager.getInstance(mContext);
        buildNewsList();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
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

    protected void buildNewsList() {
//        newsAdapter = new NewsAdapter(mContext);
        mAdapter = new FeedAdapter(mContext);
        newsList.setAdapter(mAdapter);
    }

}
