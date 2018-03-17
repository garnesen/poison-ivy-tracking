package com.hci_capstone.poison_ivy_tracker.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {Report.class}, version = 1, exportSchema = false)
@TypeConverters({DateTypeConverter.class})
public abstract class ReportDatabase extends RoomDatabase {
    public abstract ReportDAO reportDAO();
}
