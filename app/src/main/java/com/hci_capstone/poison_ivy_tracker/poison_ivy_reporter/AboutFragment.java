package com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hci_capstone.poison_ivy_tracker.R;

public class AboutFragment extends Fragment {

    Button linkButton;

    public AboutFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        linkButton = (Button) rootView.findViewById(R.id.linkButton);

        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), IvyResearchWebView.class));
            }
        });

        return rootView;
    }
}




