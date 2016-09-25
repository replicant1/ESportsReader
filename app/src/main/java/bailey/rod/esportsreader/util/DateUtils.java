package bailey.rod.esportsreader.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility methods related to date handling.
 */
public final class DateUtils {

    private static final String[] DAYS_OF_WEEK = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    // NOTE: Output date/times are in Sydney/Melbourne EST
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

    // Input date/times found in feeds are assumed to be ISO 8601 format
    private static final SimpleDateFormat ISO8601_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    static {
        ISO8601_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static Date parseFromGMT(String gmtString) throws ParseException {
        return ISO8601_DATE_FORMAT.parse(gmtString);
    }

    public static String timeSinceEpochToString(long timestamp) {
        String result = null;

        if (timestamp == 0) {
            result = "Unknown";
        } else {
            Date date = new Date(timestamp);
            result = DATE_FORMAT.format(date);
        }

        return result;
    }

    public static long parseFromTimeSinceEpoch(String str) throws ParseException {
        if (startsWithDayOfWeek(str)) {
            return parseFromNamedStr(str);
        } else {
            return parseFromNumberedStr(str);
        }
    }

    private static boolean startsWithDayOfWeek(String str) {
        boolean result = false;

        for (String day : DAYS_OF_WEEK) {
            if (str.startsWith(day)) {
                result = true;
                break;
            }
        }

        return result;
    }


    /**
     * Parse date/time strings that look like this:
     * <p/>
     * Sun, 18 Sep 2016 07:51:47 Z
     * Mon, 19 Sep 2016 18:45:48 +0000
     * <p/>
     * "Z" is equivalent to a Time Zone of "+00:00" = UT = GMT
     *
     * @param str
     * @return
     */
    public static long parseFromNamedStr(String str) throws ParseException {
        String strWithTZExpanded = null;

        // Time zone of Z is equivalent to time zone of +0000
        if (str.endsWith("Z")) {
            strWithTZExpanded = str.replace("Z", "+0000");

        } else {
            strWithTZExpanded = str;

        }

        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        Date resultDate = format.parse(strWithTZExpanded);
        return resultDate.getTime();
    }

    /**
     * Parse date/time strings that look like this:
     * <p/>
     * 2016-09-19T20:07:49.248+00:00
     * 2016-09-21T07:56:03.431Z
     * <p/>
     * 2016-09-19T20:07:49.248+00:00
     * 2016-09-18T07:51:54Z
     */
    public static long parseFromNumberedStr(String str) throws ParseException {
        SimpleDateFormat format = null;
        String strWithTZExpanded = null;

        if (str.contains(".")) {
            // Has milliseconds
            if (str.endsWith("Z")) {
                strWithTZExpanded = str.replace("Z", "+00:00");
            } else {
                strWithTZExpanded = str;
            }
            format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        } else {
            // Doesn't have milliseconds
            if (str.endsWith("Z")) {
                strWithTZExpanded = str.replace("Z", "+00:00");
            } else {
                strWithTZExpanded = str;
            }
            format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        }

        Date resultDate = format.parse(str);
        return resultDate.getTime();
    }

}
