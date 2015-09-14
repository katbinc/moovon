package com.wewant.moovon.newsfbsdk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wewant.moovon.newsfbsdk.R;
import com.wewant.moovon.newsfbsdk.model.CommentModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CommentAdapter extends AbstractGenericAdapter<CommentModel> {
    private static final String TAG = CommentAdapter.class.getSimpleName();

    private final Context mContext;

    public CommentAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {
            rowView = LayoutInflater.from(mContext).inflate(R.layout.item_comment, null, true);

            holder = new ViewHolder();
            holder.fromLogo = (ImageView) rowView.findViewById(R.id.from_logo);
            holder.fromTitle = (TextView) rowView.findViewById(R.id.from_title);
            holder.fromDate = (TextView) rowView.findViewById(R.id.from_date);
            holder.comment = (TextView) rowView.findViewById(R.id.comment);

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        final CommentModel obj = getObject(position);
        Glide.with(mContext).load(obj.getFromLogo()).into(holder.fromLogo);
        holder.fromTitle.setText(obj.getFromTitle());
        holder.fromDate.setText(new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(obj.getDate()));
        holder.comment.setText(obj.getComment());

        return rowView;
    }

    public static class ViewHolder {
        ImageView fromLogo;
        TextView fromTitle;
        TextView fromDate;
        TextView comment;
    }

}
