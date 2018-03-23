package com.hci_capstone.poison_ivy_tracker.sync;

import android.util.Log;

import com.hci_capstone.poison_ivy_tracker.database.Report;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * A class that contains all the data that needs to be synced on the server.
 */
public class SyncPackage {

    private final String LOG_TAG = "SYNC_PACKAGE";

    private String screenName;
    private String email;
    private List<Report> reports;

    public SyncPackage(String screenName, String email, List<Report> reports) {
        this.screenName = screenName;
        this.email = email;
        this.reports = reports;
    }

    public SyncPackage() {}

    public String getScreenName() {
        return screenName;
    }

    public void syncScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getEmail() {
        return email;
    }

    public void syncEmail(String email) {
        this.email = email;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void syncReports(List<Report> reports) {
        this.reports = reports;
    }

    /**
     * Gets the sync data as a JSON object.
     * @return
     */
    public JSONObject getAsJson() {
        JSONObject data = new JSONObject();

        try {
            data.put("screen_name", getScreenName());
            data.put("email", getEmail());
            data.put("reports", reportsToJson(getReports()));
        } catch (JSONException e) {
            Log.v(LOG_TAG, "Failed to create JSON object for package.");
            e.printStackTrace();
            return null;
        }

        return data;
    }

    /**
     * Converts a list of reports in JSON.
     * @param reports the list of reports to be turned into json
     * @return JSONArray
     * @throws JSONException passes exception up
     */
    private JSONArray reportsToJson(List<Report> reports) throws JSONException {
        JSONArray reportsJson = new JSONArray();
        for (Report report : reports) {
            reportsJson.put(reportToJson(report));
        }
        return reportsJson;
    }

    /**
     * Converts a report into JSON.
     * @param report a report object to be turned into json
     * @return JSONObject
     * @throws JSONException passes exception up
     */
    private JSONObject reportToJson(Report report) throws JSONException {
        JSONObject reportJson = new JSONObject();
        reportJson.put("plant_type", report.getPlantType());
        reportJson.put("longitude", report.getLongitude());
        reportJson.put("latitude", report.getLatitude());
        reportJson.put("date", report.getDate().toString());
        reportJson.put("images", imageListToJson(report.getImageLocations()));
        return reportJson;
    }

    /**
     * Converts a list of image locations into json.
     * @param imageLocations list of image locations
     * @return json
     */
    private JSONArray imageListToJson(List<String> imageLocations) {
        JSONArray imagesJson = new JSONArray();
        for (String location : imageLocations) {
            imagesJson.put(location);
        }
        return imagesJson;
    }
}
