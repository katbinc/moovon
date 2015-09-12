package com.wewant.moovon.newsfbsdk.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wewant.moovon.newsfbsdk.R;
import com.wewant.moovon.newsfbsdk.model.FeedModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created on 12.09.2015.
 * (c) All rights reserved
 */
public class FeedAdapter extends AbstractGenericAdapter<FeedModel> {
    private static final String TAG = NewsAdapter.class.getSimpleName();

    private Context mContext;

    public FeedAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {
            rowView = LayoutInflater.from(mContext).inflate(R.layout.item_feed, null, true);

            holder = new ViewHolder();
            holder.feedImage = (ImageView) rowView.findViewById(R.id.feed_image);
            holder.feedMessage = (TextView) rowView.findViewById(R.id.feed_message);
            holder.feedTitle = (TextView) rowView.findViewById(R.id.feed_title);
            holder.fromLogo = (ImageView) rowView.findViewById(R.id.from_logo);
            holder.fromTitle = (TextView) rowView.findViewById(R.id.from_title);
            holder.fromDate = (TextView) rowView.findViewById(R.id.from_date);

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        FeedModel obj = getObject(position);
        Glide.with(mContext).load(obj.getAuthorLogo()).into(holder.fromLogo);
        holder.fromTitle.setText(obj.getAuthorTitle());
        holder.fromDate.setText(new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(obj.getPostDateTime()));

        holder.feedMessage.setText(obj.getPostDescription());
        holder.feedTitle.setText(obj.getPostTitle());
        Glide.with(mContext).load(obj.getPostImage()).into(holder.feedImage);
        holder.feedImage.setVisibility(TextUtils.isEmpty(obj.getPostImage()) ? View.GONE : View.VISIBLE);

        return rowView;
    }

    static class ViewHolder {
        ImageView fromLogo;
        TextView fromTitle;
        TextView fromDate;
        TextView feedTitle;
        TextView feedMessage;
        ImageView feedImage;
    }
}
