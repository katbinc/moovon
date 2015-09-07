package by.katbinc.moovon.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import by.katbinc.moovon.R;
import by.katbinc.moovon.adapter.NewsAdapter;
import com.facebook.FacebookSdk;

public class NewsFragment extends Fragment {
    private static final String TAG = NewsFragment.class.getSimpleName();

    private ListView newsList;
//    private UiLifecycleHelper uiHelper;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, null, false);
        newsList = (ListView) rootView.findViewById(R.id.newsList);
        buildNewsList();

        return rootView;
    }

    private void initFb() {
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
    }

    private void buildNewsList() {
        Log.d(TAG, "Build news list");
        NewsAdapter adapter = new NewsAdapter(getActivity());
        newsList.setAdapter(adapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFb();
//        uiHelper = new UiLifecycleHelper(getActivity(), callback);
//        uiHelper.onCreate(savedInstanceState);
    }
}
