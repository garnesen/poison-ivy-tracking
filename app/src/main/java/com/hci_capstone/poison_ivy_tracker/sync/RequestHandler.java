package com.hci_capstone.poison_ivy_tracker.sync;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hci_capstone.poison_ivy_tracker.R;
import com.hci_capstone.poison_ivy_tracker.exceptions.UninitializedSingletonException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Singleton class to handle sending requests to a server.
 */
public class RequestHandler {

    private final String LOG_TAG = "IVY_REQUEST";

    private static RequestHandler INSTANCE;
    private RequestQueue requestQueue;
    private String serverUrl;

    private RequestHandler(Context context) {
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        this.serverUrl = context.getString(R.string.server_url);
    }

    /**
     * Initialize the singleton instance.
     * @param context the application context
     */
    public static void init(Context context) {
        if (INSTANCE == null) {
            context = context.getApplicationContext();
            INSTANCE = new RequestHandler(context);
        }
    }

    /**
     * Get the instance object for the singleton.
     * @return the instance
     */
    public synchronized static RequestHandler getInstance() {
        if (INSTANCE == null) {
            throw new UninitializedSingletonException("You must initialize the singleton before use.");
        }
        return INSTANCE;
    }

    /**
     * Gets the RequestQueue.
     * @return the RequestQueue
     */
    private RequestQueue getRequestQueue() {
        return requestQueue;
    }

    /**
     * Adds a request to the queue.
     * @param req the request
     * @param <T> type of request
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /**
     * Sends a request to the server with data that needs to be synced.
     * @param uid the uid of the user
     * @param syncPackage a package of information that needs to be synced
     * @param responseListener the listener for the POST response
     * @param errorListener the listener for an error
     * @return true if the request was made, false if an error occurred
     */
    public boolean addSyncRequest(String uid, SyncPackage syncPackage, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {

        // Create JSON
        JSONObject syncData = syncPackage.getAsJson();
        if (syncData == null) {
            return false;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("uid", uid);
            json.put("data", syncData);
        } catch(JSONException e) {
            Log.v(LOG_TAG,"Failed to create JSON object");
            e.printStackTrace();
            return false;
        }

        // Create Request
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                serverUrl,
                json,
                responseListener,
                errorListener
        );

        Log.v(LOG_TAG,"Adding request!");
        addToRequestQueue(request);
        return true;
    }
}
