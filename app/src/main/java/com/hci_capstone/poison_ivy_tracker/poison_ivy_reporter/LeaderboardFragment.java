package com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter;

import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hci_capstone.poison_ivy_tracker.R;

/**
 * Created by douglasbotello on 2/23/18.
 */

public class LeaderboardFragment extends Fragment {

    public LeaderboardFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        return rootView;
    }
}