package com.hci_capstone.poison_ivy_tracker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ReportFragment extends Fragment {

    Button submit;
    TextView ivyTypeTextView;
    RadioGroup ivyType;
    RadioGroup ivyPresence;

    public ReportFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report, container, false);

        submit = (Button) rootView.findViewById(R.id.report_submit);
        ivyTypeTextView = (TextView) rootView.findViewById(R.id.report_ivy_type_text);
        ivyType = (RadioGroup) rootView.findViewById(R.id.report_ivy_type_radio_group);

        ivyPresence = (RadioGroup) rootView.findViewById(R.id.report_ivy_presence_radio_group);
        ivyPresence.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                boolean present = id == R.id.report_ivy_presence_yes;

                if (present) {
                    ivyType.setVisibility(View.VISIBLE);
                    ivyTypeTextView.setVisibility(View.VISIBLE);
                    submit.setEnabled(false);
                }
                else {
                    ivyType.clearCheck();
                    ivyType.setVisibility(View.INVISIBLE);
                    ivyTypeTextView.setVisibility(View.INVISIBLE);
                    submit.setEnabled(true);
                }
            }
        });

        ivyType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                submit.setEnabled(true);
            }
        });
        return rootView;
    }
}
