package com.hci_capstone.poison_ivy_tracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * Created by douglasbotello on 2/23/18.
 */

public class IdentifyFragment extends Fragment {

    public IdentifyFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_identify, container, false);

        return rootView;
    }
}



