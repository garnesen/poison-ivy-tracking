package com.hci_capstone.poison_ivy_tracker;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter.database.Report;
import com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter.database.ReportDAO;
import com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter.database.ReportDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Simple database tests.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private ReportDAO reportDAO;
    private ReportDatabase reportDb;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        reportDb = Room.inMemoryDatabaseBuilder(context, ReportDatabase.class).build();
        reportDAO = reportDb.reportDAO();
    }

    @After
    public void closeDb() {
        reportDb.close();
    }

    @Test
    public void testDb() {
        Date testDate1 = new Date(2018, 1, 12);
        Date testDate2 = new Date(2018, 3, 25);
        Report testReport1 = new Report("uid1", "creeping", 10.0f, -20.5f, testDate1, "pics");
        Report testReport2 = new Report("uid2", "climbing", 5.0f, -100.5f, testDate2, "photos");

        reportDAO.insertReports(testReport1);
        reportDAO.insertReports(testReport2);

        List<Report> reports = reportDAO.getAll();
        assertEquals(2, reports.size());
        assertEquals("uid1", reports.get(0).getUid());
        assertEquals("uid2", reports.get(1).getUid());
        assertTrue(reports.get(0).getDate().compareTo(testReport1.getDate()) == 0);
        assertTrue(reports.get(1).getDate().compareTo(testReport2.getDate()) == 0);

        reportDAO.clearTable();
        assertEquals(0, reportDAO.getAll().size());
    }
}
