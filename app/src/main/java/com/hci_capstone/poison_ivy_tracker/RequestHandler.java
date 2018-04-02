package com.hci_capstone.poison_ivy_tracker;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.hci_capstone.poison_ivy_tracker.exceptions.UninitializedSingletonException;

/**
 * Singleton class to handle sending requests to a server.
 */
public class RequestHandler {

    private static RequestHandler INSTANCE;
    private RequestQueue requestQueue;

    private RequestHandler(Context context) {
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
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
}
