package com.hci_capstone.poison_ivy_tracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;

import com.hci_capstone.poison_ivy_tracker.database.Report;
import com.hci_capstone.poison_ivy_tracker.database.ReportDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import im.delight.android.location.SimpleLocation;

// TODO: Create an error page if the user denies location services.
// TODO: Create listener for ReportFragment to receive reports.
// TODO: Add reports to room database.
public class MainActivity extends AppCompatActivity implements ReportFragment.OnReportSubmittedListener {

    private BottomNavigationView bottomNavigationView;
    private List<Fragment> fragments;

    private SimpleLocation location;
    final int REQUEST_LOCATION = 200;

    enum FragmentTag {
        REPORT, IDENTIFY, LEADERBOARDS, ABOUT, SETTINGS
    }

    static {
        // Set dark theme to always be on.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // New SimpleLocation object, easy access to latitude and longitude.
        location = new SimpleLocation(this);

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
            SimpleLocation.openSettings(this);
        }
        
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

    /**
     * Check if we have access to user location.
     * @return true if we have access, false otherwise
     */
    private boolean hasLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onReportSubmitted(boolean ivyPresent, String ivyType, String imageLocation) {
        Date curDate = new Date();
        ivyType = ivyPresent ? ivyType : "absent";
        String uid = InstanceID.getInstance(getApplicationContext()).getId();

        Report report = new Report(
                uid,
                ivyType,
                (float) location.getLatitude(),
                (float) location.getLongitude(),
                curDate,
                imageLocation
        );

        ReportDatabase.getDatabase(getApplicationContext()).insertReports(report);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Make the device update its location.
        if (hasLocationPermissions()) {
            location.beginUpdates();
        }
    }

    @Override
    protected void onPause() {
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
