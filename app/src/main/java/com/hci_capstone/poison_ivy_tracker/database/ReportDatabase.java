package com.hci_capstone.poison_ivy_tracker.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.os.AsyncTask;

import com.hci_capstone.poison_ivy_tracker.exceptions.UninitializedSingletonException;

import java.util.List;

@Database(entities = {Report.class}, version = 1, exportSchema = false)
@TypeConverters({DateTypeConverter.class})
public abstract class ReportDatabase extends RoomDatabase {

    private static ReportDatabase INSTANCE;

    public abstract ReportDAO reportDAO();

    /**
     * Get the database as a singleton as it is expensive.
     * @return the database
     */
    public static ReportDatabase getDatabase() {
        if (INSTANCE == null) {
            throw new UninitializedSingletonException("You must initialize the singleton before use.");
        }
        return INSTANCE;
    }

    /**
     * Initialize the singleton instance.
     * @param context
     */
    public static void init(Context context) {
        if (INSTANCE == null) {
            context = context.getApplicationContext();
            INSTANCE = Room.databaseBuilder(context, ReportDatabase.class, "reports-database").build();
        }
    }

    /**
     * Empty the instance.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Insert reports into the database.
     * @param reports
     */
    public void insertReports(Report... reports) {
        new AddReportsInBackground().execute(reports);
    }

    /**
     * Get all reports in the database.
     * @param callback
     */
    public void getAll(OnGetAllCompleted callback) {
        new GetReportsInBackground(callback).execute();
    }

    /**
     * An async task to insert reports into the database in the background.
     */
    private static class AddReportsInBackground extends AsyncTask<Report, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Report... reports) {
            INSTANCE.reportDAO().insertReports(reports);
            return true;
        }
    }

    /**
     * An async task to get all reports from the database in the background.
     */
    private static class GetReportsInBackground extends AsyncTask<Void, Void, List<Report>> {

        private OnGetAllCompleted callback;

        public GetReportsInBackground(OnGetAllCompleted callback) {
            this.callback = callback;
        }

        @Override
        protected List<Report> doInBackground(Void... params) {
            return INSTANCE.reportDAO().getAll();
        }

        @Override
        protected void onPostExecute(List<Report> reports) {
            callback.onGetAllCompleted(reports);
        }
    }

    /**
     * Callback interface for getAll.
     */
    public interface OnGetAllCompleted {
        void onGetAllCompleted(List<Report> reports);
    }
}
