package com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter.sync;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter.database.Report;
import com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter.database.ReportDatabase;
import com.hci_capstone.poison_ivy_tracker.R;
import com.hci_capstone.poison_ivy_tracker.RequestHandler;

import org.json.JSONObject;

import java.util.List;

/**
 * Service that attempts to upload reports to the server.
 */
public class IvyReportUploadService extends JobService {

    private static final String LOG_TAG = "IVY_UPLOAD_SERVICE";
    private static final String JOB_TAG = "report-upload-job";

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.v(LOG_TAG, "Starting Job: " + params.getTag());

        // This will be ignored if the app is running.
        ReportDatabase.init(getApplicationContext());
        RequestHandler.init(getApplicationContext());

        final String url = getApplicationContext().getString(R.string.server_url);
        if (TextUtils.isEmpty(url)) {
            Log.v(LOG_TAG, "Exiting Job: Server url is empty.");
            return false;
        }

        ReportDatabase.getDatabase().getAll(new ReportDatabase.OnGetAllCompleted() {
            @Override
            public void onGetAllCompleted(final List<Report> reports) {
                if (reports.size() == 0) {
                    Log.v(LOG_TAG, "Exiting Job: There are no reports.");
                    jobFinished(params, false);
                    return;
                }

                String uid = reports.get(0).getUid();
                JSONObject json = JsonUtils.createReportSyncJson(uid, reports);

                if (json == null) {
                    Log.v(LOG_TAG, "Exiting Job: Failed to create JSON.");
                    jobFinished(params, false);
                    return;
                }

                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        json,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(final JSONObject response) {
                                Log.v(LOG_TAG, "Successfully synced records. Response: " + response.toString());
                                
                                // Convert list to array.
                                Report[] reportsAsArray = new Report[reports.size()];
                                reportsAsArray = reports.toArray(reportsAsArray);

                                // Delete the synced records.
                                ReportDatabase.getDatabase().deleteReports(new ReportDatabase.OnDeleteCompleted() {
                                    @Override
                                    public void onDeleteCompleted() {
                                        Log.v(LOG_TAG, "Exiting Job: Removed the synced records from local database.");
                                        jobFinished(params, false);
                                    }
                                }, reportsAsArray);

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.v(LOG_TAG, "Rescheduling Job: Error response.\n" + getErrorAsString(error));
                                jobFinished(params, false);
                            }
                        }
                );

                Log.v(LOG_TAG,"Adding to request queue, attempting to sync " + reports.size() + " records.");
                RequestHandler.getInstance().addToRequestQueue(request);
            }
        });

        return true; // Return true if work is still going on.
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false; // Return true if this job should be retried.
    }

    /**
     * Turns a VolleyError into a printable string.
     * @param error the error
     * @return the string
     */
    private String getErrorAsString(VolleyError error) {
        StringBuilder sb = new StringBuilder();
        sb.append("\tError: ").append(error);
        sb.append("\n\tNetworkResponse: ").append(error.networkResponse);
        if (error.networkResponse != null) {
            sb.append("\n\tStatusCode: ").append(error.networkResponse.statusCode);
            sb.append("\n\tHeaders: ").append(error.networkResponse.allHeaders.toString());
        }
        return sb.toString();
    }

    /**
     * Starts a "one-off" job to upload new data.
     * @param context
     * @return false if a sync was already requested, true otherwise
     */
    public static boolean requestSync(Context context) {

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
