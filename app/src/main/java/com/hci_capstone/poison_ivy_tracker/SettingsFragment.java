package com.hci_capstone.poison_ivy_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * Created by douglasbotello on 3/23/18.
 */

public class SettingsFragment extends Fragment {

    public SettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        String[] settingsList = {"Trail Name (optional)", "Email Address (optional)"};

        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.simplerow, R.id.rowTextView, settingsList);

        ListView listView = (ListView) rootView.findViewById(R.id.SettingsList);
        listView.setAdapter(adapter);

        return rootView;
    }
}
