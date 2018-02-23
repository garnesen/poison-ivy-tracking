package com.hci_capstone.poison_ivy_tracker;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private List<Fragment> fragments;

    enum FragmentTag {
        REPORT, IDENTIFY, LEADERBOARDS, ABOUT, SETTINGS
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.bottombaritem_report:
                        switchToFragment(FragmentTag.REPORT);
                        return true;
                    case R.id.bottombaritem_identify:
                        switchToFragment(FragmentTag.IDENTIFY);
                        return true;
                    case R.id.bottombaritem_leaderboards:
                        switchToFragment(FragmentTag.LEADERBOARDS);
                        return true;
                   case R.id.bottombaritem_about:
                       switchToFragment(FragmentTag.ABOUT);
                        return true;
                    case R.id.bottombaritem_settings:
                        switchToFragment(FragmentTag.SETTINGS);
                        return true;
                }
                return false;
            }
        });

        buildFragmentsList();

        // Set the default fragment.
        switchToFragment(FragmentTag.REPORT);
    }

    /**
     * Loads a new fragments onto the screen.
     * @param tag the tag of the desired fragment
     */
    private void switchToFragment(FragmentTag tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_fragmentholder, fragments.get(tag.ordinal()), tag.toString())
                .commit();
    }

    /**
     * Initializes all fragments and stores them in a list.
     */
    private void buildFragmentsList() {
        fragments = new ArrayList<>(5);

        Fragment reportFragment = new ReportFragment();
        Fragment identifyFragment = new IdentifyFragment();
        Fragment leaderboardsFragment = new LeaderboardFragment();
        Fragment aboutFragment = new AboutFragment();
        Fragment settingsFragment = new Fragment();

        fragments.add(reportFragment);
        fragments.add(identifyFragment);
        fragments.add(leaderboardsFragment);
        fragments.add(aboutFragment);
        fragments.add(settingsFragment);
    }
}
