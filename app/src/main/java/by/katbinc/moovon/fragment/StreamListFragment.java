package by.katbinc.moovon.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import by.katbinc.moovon.R;
import by.katbinc.moovon.adapter.StreamAdapter;
import by.katbinc.moovon.api.Api;
import by.katbinc.moovon.model.NavigationModel;
import by.katbinc.moovon.model.PlayerStreamModel;
import by.katbinc.moovon.transport.HttpTransport;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by katb on 31.08.15.
 */
public class StreamListFragment extends Fragment {
    public static final String TAG = StreamListFragment.class.getSimpleName();

    private ListView streamList;
    private StreamAdapter streamAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stream_list, null, false);
        streamList = (ListView) rootView.findViewById(R.id.streamList);

        buildStreamList();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HttpTransport transport = new HttpTransport(getActivity());
        transport.loadPlayerStreams(new HttpTransport.OnStreamLoadListener() {
            @Override
            public void onSuccess(ArrayList<PlayerStreamModel> streams) {
                Log.d(TAG, "Load Streams Success");
                StreamListFragment.this.streamAdapter.setObjects(streams);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Load Streams Error", e);
            }
        });
    }

    protected void buildStreamList() {
        Log.d(TAG, "build stream list");
        streamAdapter = new StreamAdapter(getActivity());
        streamList.setAdapter(streamAdapter);
    }
}
