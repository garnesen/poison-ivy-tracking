package com.hci_capstone.poison_ivy_tracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;

import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity implements GetLocationListener {

    static {
        // Set dark theme to always be on.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    private final int REQUEST_LOCATION = 200;
    private SimpleLocation location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize singletons that require context.
        InstanceID.init(this);

        // New SimpleLocation object, easy access to latitude and longitude.
        location = new SimpleLocation(getApplicationContext(), true);

        // Check for location permissions.
        if (!hasLocationPermissions()) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else {
            location.beginUpdates();
        }

        // If location is not turned on...
        if (!location.hasLocationEnabled()) {
            // ...ask the user to enable location access.
            SimpleLocation.openSettings(getApplicationContext());
        }

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
    public SimpleLocation getLocation() {
        if (hasLocationPermissions()) {
            return location;
        }

        // If there is a current project loaded, lets remove it from the backstack and replace with the error.
        if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
            getSupportFragmentManager().popBackStack();
        }

        // Show the location error.
        switchToFragment(new LocationErrorFragment(), true);

        return null;
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

    /**
     * Switches from the current fragment to the given fragment. If addToBackStack is true, the
     * fragment will be added to the backstack allowing the backbutton to return to the previous
     * fragment.
     * @param fragment the fragment to switch to
     * @param addToBackStack if true, add to backstack
     */
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

    /**
     * Check if we have access to user location.
     * @return true if we have access, false otherwise
     */
    private boolean hasLocationPermissions() {
        return ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Make the device update its location.
        if (hasLocationPermissions()) {
            location.beginUpdates();
        }
    }

    @Override
    public void onPause() {
        // Stop location updates (saves battery).
        if (hasLocationPermissions()) {
            location.endUpdates();
        }

        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                location.beginUpdates();
            }
        }
    }
}
