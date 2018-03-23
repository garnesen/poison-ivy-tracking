package com.hci_capstone.poison_ivy_tracker.sync;

import android.content.Context;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.hci_capstone.poison_ivy_tracker.database.Report;
import com.hci_capstone.poison_ivy_tracker.database.ReportDatabase;

import java.util.List;

// TODO: Add ability to send data to a server.
public class IvyReportUploadService extends JobService {

    private static final String LOG_TAG = "IVY_UPLOAD_SERVICE";
    private static final String JOB_TAG = "report-upload-job";
    private static boolean syncRequested = false;

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.v(LOG_TAG, "Starting service " + params.getTag());

        // This will be ignored if the app is running.
        ReportDatabase.init(getApplicationContext());

        // Test Code
        ReportDatabase.getDatabase().getAll(new ReportDatabase.OnGetAllCompleted() {
            @Override
            public void onGetAllCompleted(List<Report> reports) {
                Log.v(LOG_TAG, "Reports: " + reports.size() + "  Last Report: " + reports.get(reports.size()-1).toString());
                jobFinished(params, false);
                syncRequested = false;
            }
        });
        // End Test Code

        return true; // Return true if work is still going on.
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false; // Return true if this job should be retried.
    }

    /**
     * Starts a "one-off" job to upload new data.
     * @param context
     * @return false if a sync was already requested, true otherwise
     */
    public static boolean requestSync(Context context) {

        if (syncRequested) {
            return false;
        }
        syncRequested = true;

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job uploadJob = dispatcher.newJobBuilder()
                // Set the JobService that will be called.
                .setService(IvyReportUploadService.class)
                // Name the job.
                .setTag(JOB_TAG)
                // This job will only execute once.
                .setRecurring(false)
                // Start between 0 and 60 seconds from now.
                .setTrigger(Trigger.executionWindow(0, 60))
                // Make sure this runs even after a reboot.
                .setLifetime(Lifetime.FOREVER)
                // Do not replace this job if another is made.
                .setReplaceCurrent(false)
                // Retry with exponential backoff.
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // Only perform the job on unmetered network and while charging.
                .setConstraints(Constraint.ON_UNMETERED_NETWORK, Constraint.DEVICE_CHARGING)
                .build();

        dispatcher.mustSchedule(uploadJob);

        return true;
    }
}
