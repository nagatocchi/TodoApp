package com.todo.androidapp.util;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
/**
 * Created by Anika on 07.07.15
 */

/**
 * Converts the output of a gregorian calendar object into readable formats.
 */
public class ConvertCalendar {

    /**
     * Method for converting the Calendar into a YYYY-MM-DD T HH:MM:SS.SSS Z format.
     *
     * @param expiry Calendar to convert.
     * @return A formatted string.
     */
    public static String convertCalendarToReadableString(GregorianCalendar expiry) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.GERMAN);
        f.setCalendar(expiry);
        return f.format(expiry.getTime());
    }

    /**
     * Method for converting the Calendar into a DD.MM.YYYY  HH:MM format.
     *
     * @param expiry Calendar to convert.
     * @return A formatted string.
     */
    public static String convertCalendarToSimpleString(GregorianCalendar expiry) {
        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy  HH:mm", Locale.GERMAN);
        f.setCalendar(expiry);
        return f.format(expiry.getTime());
    }
}
