package com.wewant.moovon.newsfbsdk.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wewant.moovon.newsfbsdk.R;
import com.wewant.moovon.newsfbsdk.activity.FacebookDetailsActivity;
import com.wewant.moovon.newsfbsdk.manager.TextLinkUtils;
import com.wewant.moovon.newsfbsdk.manager.URLSpanConverter;
import com.wewant.moovon.newsfbsdk.model.CustomURLSpan;
import com.wewant.moovon.newsfbsdk.model.FeedModel;
import com.wewant.moovon.newsfbsdk.view.ExpandableTextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class FeedAdapter extends AbstractGenericAdapter<FeedModel> {
    private static final String TAG = FeedAdapter.class.getSimpleName();

    private final Context mContext;
    private final SparseBooleanArray mCollapsedStatus;
    private OnSocialBntClick onLikeClickListener;
    private OnSocialBntClick onCommentClickListener;
    private OnSocialBntClick onShareClickListener;

    public FeedAdapter(Context context) {
        this.mContext = context;
        mCollapsedStatus = new SparseBooleanArray();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {
            rowView = LayoutInflater.from(mContext).inflate(R.layout.item_feed, null, true);

            holder = new ViewHolder();
            holder.feedImage = (ImageView) rowView.findViewById(R.id.feed_image);
            holder.feedMessage = (ExpandableTextView) rowView.findViewById(R.id.feed_message);
            holder.feedTitle = (TextView) rowView.findViewById(R.id.feed_title);
            holder.fromLogo = (ImageView) rowView.findViewById(R.id.from_logo);
            holder.fromTitle = (TextView) rowView.findViewById(R.id.from_title);
            holder.fromDate = (TextView) rowView.findViewById(R.id.from_date);

            holder.summary = (TextView) rowView.findViewById(R.id.summary);

            holder.btnLike = rowView.findViewById(R.id.btnLike);
            holder.btnComment = rowView.findViewById(R.id.btnComment);
            holder.btnShare = rowView.findViewById(R.id.btnShare);

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        final FeedModel obj = getObject(position);
        Glide.with(mContext).load(obj.getAuthorLogo()).into(holder.fromLogo);
        holder.fromTitle.setText(obj.getAuthorTitle());
        holder.fromDate.setText(new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(obj.getPostDateTime()));

        holder.feedTitle.setText(obj.getPostTitle());
        Glide.with(mContext).load(obj.getPostImage()).into(holder.feedImage);
        holder.feedImage.setVisibility(TextUtils.isEmpty(obj.getPostImage()) ? View.GONE : View.VISIBLE);
        holder.feedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(obj.getPostImage())) {
                    FacebookDetailsActivity.openImage(mContext, obj.getPostImage());
                }
            }
        });

        holder.summary.setText(String.format("%d %s  %d %s",
                obj.getLikesCount(), mContext.getResources().getString(R.string.likesCount),
                obj.getCommentsCount(), mContext.getResources().getString(R.string.commentsCount)));

        String message = obj.getPostDescription();
        if (TextUtils.isEmpty(message)) {
            message = obj.getMessage();
        }

        if (TextUtils.isEmpty(message)) {
            holder.feedMessage.setVisibility(View.GONE);
        } else {
            holder.feedMessage.setVisibility(View.VISIBLE);
            holder.feedMessage.setText(message);
        }

        boolean hasLinks = Linkify.addLinks(holder.feedMessage.getTextView(), Linkify.WEB_URLS);
        if (hasLinks) {
            Spannable formattedContent = TextLinkUtils.replaceAll((Spanned) holder.feedMessage.getText(), URLSpan.class, new URLSpanConverter(), new CustomURLSpan.OnClickListener() {

                @Override
                public void onClick(String url) {
                    FacebookDetailsActivity.openPage(mContext, url);
                }
            });
            holder.feedMessage.setText(formattedContent, mCollapsedStatus, position);
        }

        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLikeClickListener != null) {
                    onLikeClickListener.run(position, v);
                }
            }
        });
//        holder.btnComment.setTag(position);
        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCommentClickListener != null) {
                    onCommentClickListener.run(position, v);
                }
            }
        });
//        holder.btnLike.setTag(position);
        holder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onShareClickListener != null) {
                    onShareClickListener.run(position, v);
                }
            }
        });

        return rowView;
    }

    public static class ViewHolder {
        public ImageView fromLogo;
        public TextView fromTitle;
        public TextView fromDate;
        public TextView feedTitle;
        public ExpandableTextView feedMessage;
        public ImageView feedImage;

        public TextView summary;

        public View btnLike;
        public View btnComment;
        public View btnShare;
    }

    public FeedAdapter setOnLikeClickListener(OnSocialBntClick onLikeClickListener) {
        this.onLikeClickListener = onLikeClickListener;
        return this;
    }

    public FeedAdapter setOnCommentClickListener(OnSocialBntClick onCommentClickListener) {
        this.onCommentClickListener = onCommentClickListener;
        return this;
    }

    public FeedAdapter setOnShareClickListener(OnSocialBntClick onShareClickListener) {
        this.onShareClickListener = onShareClickListener;
        return this;
    }

    public interface OnSocialBntClick {
        void run(int position, View view);
    }

}
