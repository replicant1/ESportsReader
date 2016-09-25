package bailey.rod.esportsreader.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility methods related to date handling.
 */
public final class DateUtils {

    // NOTE: Output date/times should be in Sydney/Melbourne EST
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d M yyyy h:mm a");

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
        }
         else {
            Date date = new Date(timestamp);
            result = DATE_FORMAT.format(date);
        }

        return result;
    }

}
