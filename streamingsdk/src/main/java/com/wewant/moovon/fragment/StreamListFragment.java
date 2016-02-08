package com.wewant.moovon.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wewant.moovon.R;
import com.wewant.moovon.adapter.StreamAdapter;
import com.wewant.moovon.model.PlayerStreamModel;
import com.wewant.moovon.transport.HttpTransport;

import java.util.ArrayList;

public class StreamListFragment extends Fragment {
    public static final String TAG = StreamListFragment.class.getSimpleName();

    private Context mContext;
    private ListView streamList;
    private StreamAdapter streamAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();

        View rootView = inflater.inflate(R.layout.fragment_stream_list, null, false);
        streamList = (ListView) rootView.findViewById(R.id.streamList);

        buildStreamList();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        HttpTransport transport = new HttpTransport(mContext);
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
        streamAdapter = new StreamAdapter(mContext);
        streamList.setAdapter(streamAdapter);
        streamList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlayerStreamModel model = StreamListFragment.this.streamAdapter.getObject(position);
                openStreamFragment(model);
            }
        });
    }

    private void openStreamFragment(PlayerStreamModel stream) {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(((ViewGroup) getView().getParent()).getId(), StreamFragment.newInstance(stream));
        ft.addToBackStack(null);
        ft.commit();
    }
}
