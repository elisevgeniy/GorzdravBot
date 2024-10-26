package ru.kusok_piroga.gorzdravbot.common;


import lombok.experimental.UtilityClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@UtilityClass
public class DateConverter {
    private static String getPrintableDateTime(Date date) {
        SimpleDateFormat printableFormatter = new SimpleDateFormat("dd.MM.yyyy в HH:mm");
        return printableFormatter.format(date);
    }

    public static String getPrintableAppointmentDateTime(String dateStr) {
        try {
            return getPrintableDateTime(parseAppointmentDate(dateStr));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date parseAppointmentDate(String dateStr) throws ParseException {
        SimpleDateFormat appointmentFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return appointmentFormatter.parse(dateStr);
    }


    public static Date plusTenMin(Date date) {
        return changeMinCount(date, 10);
    }

    public static Date minusTenMin(Date date) {
        return changeMinCount(date, -10);
    }

    private static Date changeMinCount(Date date, int mins) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, mins);
        return calendar.getTime();
    }
}