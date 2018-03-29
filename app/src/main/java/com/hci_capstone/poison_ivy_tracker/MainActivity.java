package com.hci_capstone.poison_ivy_tracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    static {
        // Set dark theme to always be on.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize singletons that require context.
        InstanceID.init(this);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                removeMenuItemUntilReturn(item);
                switchToFragment(new SettingsFragment(), true);
            default:
                return super.onOptionsItemSelected(item);
        }
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

    /**
     * Makes the given menu item invisible until the user has returned from the fragment.
     * @param item the MenuItem to handle
     */
    private void removeMenuItemUntilReturn(final MenuItem item) {
        item.setVisible(false);
        final FragmentManager manager = getSupportFragmentManager();
        final int currentBackStackCount = manager.getBackStackEntryCount();
        manager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (currentBackStackCount == manager.getBackStackEntryCount()) {
                    item.setVisible(true);
                    manager.removeOnBackStackChangedListener(this);
                }
            }
        });
    }
}
