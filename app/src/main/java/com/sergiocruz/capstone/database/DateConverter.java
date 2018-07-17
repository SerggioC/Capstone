package com.sergiocruz.capstone.database;
import android.arch.persistence.room.TypeConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateConverter {

    @TypeConverter
    public static String getFormattedDateString(Long timestampMillis) {
        if (timestampMillis == null) return "";
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        dateFormatter.setTimeZone(TimeZone.getDefault());
        Date resultDate = new Date(timestampMillis);
        return dateFormatter.format(resultDate);
    }


    //    @TypeConverter
//    public static Long toTimestamp(Date date) {
//        return date == null ? null : date.getTime();
//    }

}