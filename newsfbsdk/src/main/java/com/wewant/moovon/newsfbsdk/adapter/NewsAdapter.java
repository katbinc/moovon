package com.wewant.moovon.newsfbsdk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.wewant.moovon.newsfbsdk.R;
import com.wewant.moovon.newsfbsdk.model.FeedModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class NewsAdapter extends AbstractGenericAdapter<FeedModel> {
    private static final String TAG = NewsAdapter.class.getSimpleName();

    private Context mContext;

    public NewsAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {
            rowView = LayoutInflater.from(mContext).inflate(R.layout.list_item_news, null, true);

            holder = new ViewHolder();
            holder.authorLogo = (TextView) rowView.findViewById(R.id.authorLogo);
            holder.authorTitle = (TextView) rowView.findViewById(R.id.authorTitle);
            holder.message = (TextView) rowView.findViewById(R.id.message);
            holder.postTitle = (TextView) rowView.findViewById(R.id.postTitle);
            holder.postDescription = (TextView) rowView.findViewById(R.id.postDescription);
            holder.postDateTime = (TextView) rowView.findViewById(R.id.postDateTime);
            holder.postImage = (TextView) rowView.findViewById(R.id.postImage);
            holder.likesCount = (TextView) rowView.findViewById(R.id.likesCount);
            holder.commentsCount = (TextView) rowView.findViewById(R.id.commentsCount);

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        FeedModel obj = getObject(position);
        holder.authorLogo.setText("logo: " + obj.getAuthorLogo());
        holder.authorTitle.setText("author: " + obj.getAuthorTitle());
        holder.message.setText("message: " + obj.getMessage());
        holder.postTitle.setText("postTitle: " + obj.getPostTitle());
        holder.postDescription.setText("postDescription: " + obj.getPostDescription());
        holder.postDateTime.setText("date: " + (new SimpleDateFormat("dd/m-yyyy", Locale.US)).format(obj.getPostDateTime()));
        holder.postImage.setText("image: " + obj.getPostImage());
        holder.likesCount.setText("likes: " + obj.getLikesCount());
        holder.commentsCount.setText("comments: " + obj.getCommentsCount());
        return rowView;
    }

    static class ViewHolder {
        TextView authorLogo;
        TextView authorTitle;
        TextView message;
        TextView postTitle;
        TextView postDescription;
        TextView postDateTime;
        TextView postImage;
        TextView likesCount;
        TextView commentsCount;
    }
}
