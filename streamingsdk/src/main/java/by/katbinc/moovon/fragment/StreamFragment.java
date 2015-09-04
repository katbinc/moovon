package by.katbinc.moovon.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import by.katbinc.moovon.R;
import by.katbinc.moovon.model.TrackInfoModel;
import by.katbinc.moovon.transport.HttpTransport;

public class StreamFragment extends Fragment {
    public static final String TAG = StreamFragment.class.getSimpleName();

    private TextView descriptionView;
    private ImageView coverView;

    private String urlTrackSrc;
    private String coverSrc;
    private String description;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stream, null, false);

        descriptionView = (TextView) rootView.findViewById(R.id.description);
        coverView = (ImageView) rootView.findViewById(R.id.cover);

        Bundle bundle = getArguments();
        coverSrc = bundle.getString("coverSrc");
        description = bundle.getString("description");

        String streamUrl = bundle.getString("streamUrl");
        urlTrackSrc = HttpTransport.getTrackSrcUrl(streamUrl);

        HttpTransport transport = new HttpTransport(getActivity());
        transport.loadTrackInfo(streamUrl, new HttpTransport.OnTrackInfoLoadListener() {
            @Override
            public void onSuccess(TrackInfoModel trackInfo) {
                Log.d(TAG, "Track info loaded");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Track info load error", e);
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        descriptionView.setText(description);
        Glide.with(getActivity()).load(coverSrc).into(coverView);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
