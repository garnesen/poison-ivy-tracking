package com.hci_capstone.poison_ivy_tracker.PoisonIvyReporter.sync;

import android.util.Log;

import com.hci_capstone.poison_ivy_tracker.PoisonIvyReporter.database.Report;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JsonUtils {

    private static String LOG_TAG = "JsonUtils";

    /**
     * Creates a JSON object for sending in a request.
     * @param uid the user id
     * @param reports the list of reports the user made
     * @return the JSON object
     */
    public static JSONObject createReportSyncJson(String uid, List<Report> reports) {
        JSONObject json = new JSONObject();
        JSONArray reportsJson = new JSONArray();

        try {
            for (Report report : reports) {
                reportsJson.put(reportToJson(report));
            }
            json.put("uid", uid);
            json.put("data", reportsJson);
        } catch(JSONException e) {
            Log.v(LOG_TAG,"Failed to create JSON object");
            e.printStackTrace();
            return null;
        }

        return json;
    }

    /**
     * Converts a report into JSON.
     * @param report a report object to be turned into json
     * @return JSONObject
     * @throws JSONException passes exception up
     */
    public static JSONObject reportToJson(Report report) throws JSONException {
        JSONObject reportJson = new JSONObject();
        reportJson.put("plant_type", report.getPlantType());
        reportJson.put("longitude", report.getLongitude());
        reportJson.put("latitude", report.getLatitude());
        reportJson.put("date", report.getDate().toString());

        JSONArray imagesJson = new JSONArray();
        for (String location : report.getImageLocations()) {
            imagesJson.put(imageToString(location));
        }
        reportJson.put("images", imagesJson);
        return reportJson;
    }

    /**
     * Takes an image and encodes it as a string.
     * @param imageLocation the location of the image
     * @return the string encoded image
     */
    public static String imageToString(String imageLocation) {
        // TODO: Implement imageToString
        return imageLocation;
    }
}
