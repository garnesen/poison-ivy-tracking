package com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter.database;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTypeConverter {

    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @TypeConverter
    public Date fromTimestamp(String value) {
        Date date = null;
        try {
            date = sdfDate.parse(value);
        }
        catch (ParseException e) {
            Log.v("DateTypeConverter", "Failed to parse date: " + value);
        }
        return date;
    }

    @TypeConverter
    public String dateToTimestamp(Date date) {
        if (date == null) {
            return null;
        } else {
            return sdfDate.format(date);
        }
    }
}
