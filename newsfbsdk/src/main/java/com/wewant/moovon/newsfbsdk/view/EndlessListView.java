package com.wewant.moovon.newsfbsdk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

public class EndlessListView extends ListView {
    protected int ITEM_LEFT_TO_LOAD_MORE = 2;

    private OnMoreListener mOnMoreListener;
    private boolean isLoadingMore = false;

    public EndlessListView(Context context) {
        super(context);
        init();
    }

    public EndlessListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EndlessListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnScrollListener(mOnScrollListener);

    }

    public void setOnMoreListener(OnMoreListener listener){
        this.mOnMoreListener = listener;
    }

    /**
     * need to call after finishing loading
     * */
    public void setLoadingMore(boolean isLoadingMore) {
        this.isLoadingMore = isLoadingMore;
    }

    private OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (((totalItemCount - firstVisibleItem - visibleItemCount) == ITEM_LEFT_TO_LOAD_MORE || (totalItemCount - firstVisibleItem - visibleItemCount) == 0 && totalItemCount > visibleItemCount) && !isLoadingMore) {
                if (mOnMoreListener != null) {
                    isLoadingMore = true;
                    mOnMoreListener.onMoreAsked();
                }
            }
        }
    };

    public interface OnMoreListener{
        void onMoreAsked();
    }
}
