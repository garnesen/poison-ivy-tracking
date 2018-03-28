package com.hci_capstone.poison_ivy_tracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

public class ItchyProjectMain extends AppCompatActivity {

    static {
        // Set dark theme to always be on.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itchy);

        HomePageFragment mainPage = new HomePageFragment();
        mainPage.addProjectSelectedListener(new HomePageFragment.OnProjectSelected() {
            @Override
            public void onProjectSelected(Fragment fragment) {
                switchToFragment(fragment, true);
            }
        });

        // Set the initial page.
        switchToFragment(mainPage, false);
    }

    private void switchToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.project_fragment_holder, fragment, fragment.getTag());

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }
}
