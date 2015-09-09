package com.wewant.moovon.newsfbsdk.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wewant.moovon.newsfbsdk.R;
import com.wewant.moovon.newsfbsdk.adapter.NewsAdapter;

import java.util.ArrayList;

public class NewsListFragment extends Fragment {
    public static final String TAG = NewsListFragment.class.getSimpleName();

    private Context mContext;
    private ListView newsList;
    private NewsAdapter newsAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();

        View rootView = inflater.inflate(R.layout.fragment_news_list, null, false);
        newsList = (ListView) rootView.findViewById(R.id.newsList);

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
        ArrayList<String> news = new ArrayList<>();
        news.add("news 1");
        news.add("news 2");
        NewsListFragment.this.newsAdapter.setObjects(news);
    }

    protected void buildNewsList() {
        newsAdapter = new NewsAdapter(mContext);
        newsList.setAdapter(newsAdapter);
    }

}
