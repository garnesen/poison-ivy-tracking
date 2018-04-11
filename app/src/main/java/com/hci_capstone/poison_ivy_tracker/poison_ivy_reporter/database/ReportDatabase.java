package com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.os.AsyncTask;

import com.hci_capstone.poison_ivy_tracker.exceptions.UninitializedSingletonException;

import java.io.File;
import java.util.List;

// TODO: May need to make query methods synchronized.
@Database(entities = {Report.class}, version = 2, exportSchema = false)
@TypeConverters({DateTypeConverter.class, ImageListConverter.class})
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
            INSTANCE = Room.databaseBuilder(context, ReportDatabase.class, "reports-database").fallbackToDestructiveMigration().build();
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
     * @param callback
     * @param reports
     */
    public void insertReports(OnInsertCompleted callback, Report... reports) {
        new InsertReportsInBackground(callback).execute(reports);
    }

    /**
     * Get all reports in the database.
     * @param callback
     */
    public void getAll(OnGetAllCompleted callback) {
        new GetReportsInBackground(callback).execute();
    }

    /**
     * Delete given reports in database.
     * @param callback
     */
    public void deleteReports(OnDeleteCompleted callback, Report... reports) {
        new DeleteReportsInBackground(callback).execute(reports);
    }

    /**
     * An async task to insert reports into the database in the background.
     */
    private static class InsertReportsInBackground extends AsyncTask<Report, Void, Boolean> {

        private OnInsertCompleted callback;

        public InsertReportsInBackground(OnInsertCompleted callback) {
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(Report... reports) {
            INSTANCE.reportDAO().insertReports(reports);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean finished) {
            if (callback != null) {
                callback.onInsertCompleted();
            }
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
            if (callback != null) {
                callback.onGetAllCompleted(reports);
            }
        }
    }

    /**
     * An async task to delete reports in the background.
     */
    private static class DeleteReportsInBackground extends AsyncTask<Report, Void, Boolean> {

        private OnDeleteCompleted callback;

        public DeleteReportsInBackground(OnDeleteCompleted callback) {
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(Report... reports) {
            // Delete images stored on device.
            for (Report report : reports) {
                if (report.getImageLocations() == null) {
                    continue;
                }
                for (String imageLocation : report.getImageLocations()) {
                    if (imageLocation != null) {
                        File image = new File(imageLocation);
                        image.delete();
                    }
                }
            }

            // Delete records in database.
            INSTANCE.reportDAO().deleteReports(reports);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean finished) {
            if (callback != null) {
                callback.onDeleteCompleted();
            }
        }
    }


    /**
     * Callback interface for getAll.
     */
    public interface OnGetAllCompleted {
        void onGetAllCompleted(List<Report> reports);
    }

    /**
     * Callback interface for insert.
     */
    public interface OnInsertCompleted{
        void onInsertCompleted();
    }

    /**
     * Callback interface for delete.
     */
    public interface OnDeleteCompleted{
        void onDeleteCompleted();
    }
}
