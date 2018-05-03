package com.hci_capstone.poison_ivy_tracker;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A service that will attempt to upload the changed settings to the server.
 */
public class SettingsSyncService extends JobService {

    private final String LOG_TAG = "ITCHY_SETTINGS_SERVICE";

    private final String pref_key = "pref_key";
    private final String data_key = "pref_data";

    @Override
    public boolean onStartJob(final JobParameters params) {
        final String url = getApplicationContext().getString(R.string.server_url);
        if (TextUtils.isEmpty(url)) {
            Log.v(LOG_TAG, "Exiting Job: Server url is empty.");
            return false;
        }

        // This will be ignored if the app is running.
        InstanceID.init(getApplicationContext());
        RequestHandler.init(getApplicationContext());

        // Get values for request.
        String uid = InstanceID.getInstance().getId();
        String key = params.getExtras().getString(pref_key);
        String data = params.getExtras().getString(data_key);

        // Create the JSON.
        JSONObject json = new JSONObject();
        JSONObject setting_change = new JSONObject();
        try {
            setting_change.put(key, data);
            json.put("uid", uid);
            json.put("payloadType", "SETTINGS");
            json.put("payload", setting_change);
        } catch (JSONException e) {
            Log.v(LOG_TAG, "Exiting Job: Failed to create JSON for request.");
            return false;
        }

        // Create the request.
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(LOG_TAG, "Response: " + response.toString());
                        Log.v(LOG_TAG, "Exiting Job: Successfully synced settings with server.");
                        jobFinished(params, false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(LOG_TAG, "Rescheduling Job: Error response from request: " + error);
                        jobFinished(params, true);
                    }
                }
        );

        Log.v(LOG_TAG, "Adding to request queue, attempting to sync " + key + ":" + data);
        RequestHandler.getInstance().addToRequestQueue(request);
        return true; // Return true if work is still going on.
    }

    @Override
    public boolean onStopJob(final JobParameters params) {
        Log.v(LOG_TAG, "Exiting Job: onStopJob called.");
        return false; // Return true if this job should be retried.
    }
}
