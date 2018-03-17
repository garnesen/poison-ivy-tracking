package com.hci_capstone.poison_ivy_tracker.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.os.AsyncTask;

@Database(entities = {Report.class}, version = 1, exportSchema = false)
@TypeConverters({DateTypeConverter.class})
public abstract class ReportDatabase extends RoomDatabase {

    private static ReportDatabase INSTANCE;

    public abstract ReportDAO reportDAO();

    /**
     * Get the database as a singleton as it is expensive.
     * @param context
     * @return the database
     */
    public static ReportDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, ReportDatabase.class, "reports-database").build();
        }
        return INSTANCE;
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
     * An async task to insert reports into the database in the background.
     */
    private class AddReportsInBackground extends AsyncTask<Report, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Report... reports) {
            INSTANCE.reportDAO().insertReports(reports);
            return true;
        }
    }
}
