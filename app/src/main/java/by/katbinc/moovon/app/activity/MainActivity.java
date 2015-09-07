package by.katbinc.moovon.app.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import by.katbinc.moovon.app.R;
import by.katbinc.moovon.fragment.NewsFragment;
import by.katbinc.moovon.fragment.StreamListFragment;

/**
 * Created on 31.08.15.
 * (c)
 */
public class MainActivity extends Activity {

    Button fbBtn;
    Button streamBtn;
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        fbBtn = (Button) findViewById(R.id.fbBtn);
        streamBtn = (Button) findViewById(R.id.streamBtn);
        fm = getFragmentManager();

        fbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.beginTransaction().replace(R.id.mainContent, new NewsFragment()).commit();
            }
        });
        streamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.beginTransaction().replace(R.id.mainContent, new StreamListFragment()).commit();
            }
        });

        fm.beginTransaction().add(R.id.mainContent, new StreamListFragment()).commit();

    }
}
