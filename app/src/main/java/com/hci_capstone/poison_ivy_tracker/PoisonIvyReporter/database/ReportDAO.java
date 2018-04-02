package com.hci_capstone.poison_ivy_tracker.PoisonIvyReporter.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ReportDAO {

    @Insert
    void insertReports(Report... report);

    @Delete
    void deleteReports(Report... report);

    @Query("DELETE FROM report")
    void clearTable();

    @Query("SELECT * FROM report")
    List<Report> getAll();
}
