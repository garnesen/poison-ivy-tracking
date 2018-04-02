package com.hci_capstone.poison_ivy_tracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final String LOG_TAG = "ITCHY_SETTINGS";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.fragment_settings, rootKey);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Store data in intent.
        Bundle data = new Bundle();
        data.putString("pref_key", key);
        data.putString("pref_data", sharedPreferences.getString(key, ""));

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getContext().getApplicationContext()));
        Job uploadJob = dispatcher.newJobBuilder()
                // Set the JobService that will be called.
                .setService(SettingsSyncService.class)
                // Name the job.
                .setTag(key + "_sync_service")
                // This job will only execute once.
                .setRecurring(false)
                // Start between 0 and 60 seconds from now.
                .setTrigger(Trigger.executionWindow(0, 60))
                // Make sure this runs even after a reboot.
                .setLifetime(Lifetime.FOREVER)
                // Do not replace this job if another is made.
                .setReplaceCurrent(true)
                // Retry with exponential backoff.
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // Only perform the job on unmetered network.
                .setConstraints(Constraint.ON_UNMETERED_NETWORK)
                // Send data through bundle.
                .setExtras(data)
                .build();

        Log.v(LOG_TAG, "Attempting to schedule settings sync job.");
        dispatcher.mustSchedule(uploadJob);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
}