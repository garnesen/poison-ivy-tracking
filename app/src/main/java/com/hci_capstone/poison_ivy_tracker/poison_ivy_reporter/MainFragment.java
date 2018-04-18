package com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hci_capstone.poison_ivy_tracker.GetLocationListener;
import com.hci_capstone.poison_ivy_tracker.InstanceID;
import com.hci_capstone.poison_ivy_tracker.R;
import com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter.database.Report;
import com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter.database.ReportDatabase;
import com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter.sync.IvyReportUploadService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import im.delight.android.location.SimpleLocation;

public class MainFragment extends Fragment implements ReportFragment.OnReportSubmittedListener {

    private BottomNavigationView bottomNavigationView;
    private List<Fragment> fragments;
    private Fragment reportFragment;
    private boolean fragmentIsAlive;

    private GetLocationListener locationCallback;
    private SimpleLocation location;

    enum FragmentTag {
        REPORT, IDENTIFY, LEADERBOARDS, ABOUT
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            locationCallback = (GetLocationListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement GetLocationListener.");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Initialize singletons that require context.
        ReportDatabase.init(getActivity());

        location = locationCallback.getLocation();

        bottomNavigationView = (BottomNavigationView) rootView.findViewById(R.id.bottom_nav);
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
                }
                return false;
            }
        });

        // If the fragment is still alive, do not recreate the child fragments.
        if (fragmentIsAlive) {
            return rootView;
        }
        fragmentIsAlive = true;

        buildFragmentsList();

        // Set the default fragment.
        switchToFragment(FragmentTag.REPORT);

        return rootView;
    }

    /**
     * Loads a new fragments onto the screen.
     * @param tag the tag of the desired fragment
     */
    private void switchToFragment(FragmentTag tag) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_fragmentholder, fragments.get(tag.ordinal()), tag.toString())
                .commit();
    }

    /**
     * Initializes all fragments and stores them in a list.
     */
    private void buildFragmentsList() {
        fragments = new ArrayList<>(5);

        reportFragment = new ReportFragment();
        Fragment identifyFragment = new IdentifyFragment();
        Fragment leaderboardsFragment = new LeaderboardFragment();
        Fragment aboutFragment = new AboutFragment();

        fragments.add(reportFragment);
        fragments.add(identifyFragment);
        fragments.add(leaderboardsFragment);
        fragments.add(aboutFragment);
    }



    @Override
    public void onReportSubmitted(boolean ivyPresent, String ivyType, List<String> imageLocations) {
        Date curDate = new Date();
        ivyType = ivyPresent ? ivyType : "absent";
        String uid = InstanceID.getInstance().getId();
        if (location == null) {
            Log.v("IVY_MAIN_FRAGMENT", "Location object null. Skipping report.");
            return;
        }

        Report report = new Report(
                uid,
                ivyType,
                (float) location.getLatitude(),
                (float) location.getLongitude(),
                curDate,
                imageLocations
        );

        ReportDatabase.getDatabase().insertReports(new ReportDatabase.OnInsertCompleted() {
            @Override
            public void onInsertCompleted() {
                IvyReportUploadService.requestSync(getActivity());
            }
        }, report);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentIsAlive = false;
        ((ReportFragment) reportFragment).cleanUpImages();
    }
}
