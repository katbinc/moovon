package com.wewant.moovon.newsfbsdk.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.facebook.FacebookSdk;
import com.wewant.moovon.newsfbsdk.R;
import com.wewant.moovon.newsfbsdk.adapter.CommentAdapter;
import com.wewant.moovon.newsfbsdk.adapter.FeedAdapter;
import com.wewant.moovon.newsfbsdk.manager.FbManager;
import com.wewant.moovon.newsfbsdk.model.CommentModel;
import com.wewant.moovon.newsfbsdk.model.FeedModel;
import com.wewant.moovon.newsfbsdk.view.CommentsLayout;
import com.wewant.moovon.newsfbsdk.view.EndlessListView;

import java.util.ArrayList;

public class NewsListFragment extends Fragment {
    public static final String TAG = NewsListFragment.class.getSimpleName();
    private static final int FOREGROUND_ALPHA_NORMAL = 0;
    private static final int FOREGROUND_ALPHA_POPUP = 127;

    private Context mContext;
    private SwipeRefreshLayout refreshLayout;
    private EndlessListView newsList;
    private FeedAdapter mAdapter;
    private FbManager fbManager;
    private FrameLayout rootView;

    private PopupWindow popWindow;
    CommentsLayout inflatedView;
    private int animationDuration;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();
        animationDuration = mContext.getResources().getInteger(R.integer.popupAnimClose);
        FacebookSdk.sdkInitialize(mContext);

        rootView = (FrameLayout)inflater.inflate(R.layout.fragment_news_list, null, false);
        rootView.getForeground().setAlpha(FOREGROUND_ALPHA_NORMAL);
        newsList = (EndlessListView) rootView.findViewById(R.id.newsList);
        newsList.setOnMoreListener(new EndlessListView.OnMoreListener() {
            @Override
            public void onMoreAsked() {
                loadNext();
            }
        });
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fbManager.reset();
                bindList();
            }
        });


        fbManager = FbManager.getInstance(mContext);
        fbManager.reset();
        buildNewsList();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void bindList() {
        fbManager.loadFeed(new FbManager.OnFeedLoadListener() {
            @Override
            public void onSuccess(ArrayList<FeedModel> news) {
//                NewsListFragment.this.newsAdapter.setObjects(news);
                NewsListFragment.this.mAdapter.setObjects(news);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Exception e) {
                // TODO
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadNext() {
        fbManager.loadFeedNext(new FbManager.OnFeedLoadListener() {
            @Override
            public void onSuccess(ArrayList<FeedModel> news) {
                if (news.size() > 0) {
                    NewsListFragment.this.mAdapter.addObjects(news);
                    newsList.setLoadingMore(false);
                }
            }

            @Override
            public void onError(Exception e) {
                // TODO
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected void buildNewsList() {
        mAdapter = new FeedAdapter(mContext);
        mAdapter.setOnLikeClickListener(new FeedAdapter.OnSocialBntClick() {

            @Override
            public void run(final int position, final View view) {
                if (fbManager.isLoggedIn()) {
                    performLike(position);
                } else {
                    fbManager.login(NewsListFragment.this, new Runnable() {
                        @Override
                        public void run() {
                            performLike(position);
                        }
                    });

                }
            }
        }).setOnCommentClickListener(new FeedAdapter.OnSocialBntClick() {
            @Override
            public void run(final int position, View view) {
                if (fbManager.isLoggedIn()) {
                    performComment(position);
                } else {
                    fbManager.login(NewsListFragment.this, new Runnable() {
                        @Override
                        public void run() {
                            performComment(position);
                        }
                    });

                }
            }
        }).setOnShareClickListener(new FeedAdapter.OnSocialBntClick() {
            @Override
            public void run(int position, View view) {
                fbManager.share(NewsListFragment.this, mAdapter.getObject(position));
            }
        });

        newsList.setAdapter(mAdapter);
        bindList();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbManager.onActivityResult(requestCode, resultCode, data);
    }

    private void performLike(final int position) {
        fbManager.like(
                mAdapter.getObject(position).getId(),
                new FbManager.OnLikesLoadListener() {
                    @Override
                    public void onSuccess(int likesCount) {
                        mAdapter.getObject(position).setLikesCount(likesCount);
                        mAdapter.invalidate();
                    }
                }
        );
    }

    private void performComment(final int position) {
        String id = mAdapter.getObject(position).getId();
        fbManager.setOnCommentAddedListener(new Runnable() {
            @Override
            public void run() {
                mAdapter.getObject(position).setCommentsCount(mAdapter.getObject(position).getCommentsCount() + 1);
                mAdapter.invalidate();
            }
        });
        showPopup(id);
    }

    public void showPopup(String objId) {

        rootView.getForeground().setAlpha(FOREGROUND_ALPHA_POPUP);
        LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        inflatedView = (CommentsLayout)layoutInflater.inflate(R.layout.popup_comments, null, false);
        inflatedView.setAnimDuration(animationDuration);
        // find the ListView in the popup layout
        ListView listView = (ListView)inflatedView.findViewById(R.id.commentsListView);

        // set height depends on the device size
        popWindow = new PopupWindow(inflatedView, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, true );
        inflatedView.setOnCloseListener(new Runnable() {
            @Override
            public void run() {
                popWindow.setAnimationStyle(android.R.style.Animation);
                popWindow.dismiss();
            }
        });
        // set a background drawable with rounders corners
        popWindow.setBackgroundDrawable(new ColorDrawable());
        // make it focusable to show the keyboard to enter in `EditText`
        popWindow.setFocusable(true);
        // make it outside touchable to dismiss the popup window
        popWindow.setOutsideTouchable(false);
        popWindow.setAnimationStyle(R.style.popup);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                rootView.getForeground().setAlpha(FOREGROUND_ALPHA_NORMAL);
            }
        });
        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(getView(), Gravity.BOTTOM, 0, 100);

        setCommentsList(objId, listView);
    }

    private void setCommentsList(final String objId, ListView listView) {
        final EditText newComment = (EditText) inflatedView.findViewById(R.id.newComment);
        final Button btnSend = (Button) inflatedView.findViewById(R.id.btnSend);
        final CommentAdapter commentAdapter = new CommentAdapter(mContext);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(newComment, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 300);

        listView.setAdapter(commentAdapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    inflatedView.setPreventScrollDown(true);
                } else {
                    inflatedView.setPreventScrollDown(false);
                }
                if (totalItemCount == firstVisibleItem + visibleItemCount) {
                    inflatedView.setPreventScrollUp(true);
                } else {
                    inflatedView.setPreventScrollUp(false);
                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSend.setEnabled(false);
                fbManager.comment(
                        objId,
                        newComment.getText().toString(),
                        new Runnable() {
                            @Override
                            public void run() {
                                newComment.setText("");
                                loadComments(objId, commentAdapter);
                                btnSend.setEnabled(true);
                            }
                        }
                );
            }
        });

        loadComments(objId, commentAdapter);
    }

    private void loadComments(String objId, final CommentAdapter adapter) {
        fbManager.loadLastComments(objId, new FbManager.OnCommentsLoadListener() {
            @Override
            public void onSuccess(ArrayList<CommentModel> comments) {
                adapter.setObjects(comments);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

}
