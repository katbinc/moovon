package com.wewant.moovon.newsfbsdk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.wewant.moovon.newsfbsdk.R;

public class NewsAdapter extends AbstractGenericAdapter<String> {
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
            holder.title = (TextView) rowView.findViewById(R.id.title);

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.title.setText(getObject(position));
        return rowView;
    }

    static class ViewHolder {
        TextView title;
    }
}
