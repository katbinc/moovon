package com.wewant.moovon.newsfbsdk.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wewant.moovon.newsfbsdk.R;
import com.wewant.moovon.newsfbsdk.view.ZoomView;

public class ImageFragment extends Fragment {
    public static final String TAG = ImageFragment.class.getSimpleName();

    private static final String KEY_URL = "url";

    public static ImageFragment newInstance(String url) {
        ImageFragment result = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(KEY_URL, url);
        result.setArguments(args);
        return result;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);

        String url = getArguments().getString(KEY_URL);

        ImageView image = new ImageView(getActivity());

        ZoomView zoomView = new ZoomView(getActivity());


        zoomView.addView(image);
        zoomView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));


        FrameLayout imageContainer = (FrameLayout)
                view.findViewById(R.id.image_container);
        imageContainer.addView(zoomView);

        Glide.with(getActivity())
                .load(url)
                .into(image);

        return view;
    }
}
