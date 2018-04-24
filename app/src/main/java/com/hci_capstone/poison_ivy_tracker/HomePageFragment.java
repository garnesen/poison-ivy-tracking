package com.hci_capstone.poison_ivy_tracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter.MainFragment;

/**
 * The first fragment that is loaded in the MainActivity. The user picks a project here.
 */
public class HomePageFragment extends Fragment {

    private OnProjectSelected callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        // Follow this pattern for adding new projects.
        Button poisonIvyProjectButton = view.findViewById(R.id.poison_ivy_project_button);
        poisonIvyProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    // Send a new instance of the project fragment.
                    callback.onProjectSelected(new MainFragment());
                }
            }
        });

        return view;
    }

    /**
     * Add a listener for when a project is selected on the home page.
     * @param callback the callback interface
     */
    public void addProjectSelectedListener(OnProjectSelected callback) {
        this.callback = callback;
    }

    /**
     * Listener interface.
     */
    public interface OnProjectSelected {
        void onProjectSelected(Fragment fragment);
    }
}
