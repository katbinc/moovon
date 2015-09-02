package by.katbinc.moovon.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import by.katbinc.moovon.R;
import by.katbinc.moovon.fragment.StreamListFragment;

/**
 * Created by katb on 31.08.15.
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        Fragment fragment = new StreamListFragment();

        FragmentTransaction fTransaction = getFragmentManager().beginTransaction();
        fTransaction.add(R.id.mainContent, fragment);
        fTransaction.commit();

    }
}
