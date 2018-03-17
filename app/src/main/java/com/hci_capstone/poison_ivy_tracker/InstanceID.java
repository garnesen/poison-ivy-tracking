package com.hci_capstone.poison_ivy_tracker;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * A singleton class that handles creating and retrieving an instance ID.
 */
public class InstanceID {

    private static InstanceID id;
    private String guid;

    /**
     * Private constructor to prevent instantiation.
     * @param context
     */
    private InstanceID(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        if (!sharedPref.contains(context.getString(R.string.guid_key))){
            SharedPreferences.Editor editor = sharedPref.edit();
            guid = UUID.randomUUID().toString();
            editor.putString(context.getString(R.string.guid_key), guid);
            editor.commit();
        }
        else {
            guid = sharedPref.getString(context.getString(R.string.guid_key), "0");
        }
    }

    /**
     * Get the instance InstanceID.
     * @param context
     * @return InstanceID
     */
    public static InstanceID getInstance(Context context) {
        if (id == null) {
            id = new InstanceID(context);
        }

        return id;
    }

    /**
     * Get the instance id for this user.
     * @return the guid
     */
    public String getId() {
        return guid;
    }
}
