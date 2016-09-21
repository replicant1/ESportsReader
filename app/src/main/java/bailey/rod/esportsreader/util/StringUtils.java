package bailey.rod.esportsreader.util;

/**
 * Created by rodbailey on 21/09/2016.
 */
public final class StringUtils {

    /**
     * @param str
     * @param maxLength Must be >= 3
     * @return
     */
    public static String ellipsizeNullSafe(String str, int maxLength) {
        String result = str;

        if ((str != null) && (str.length() > maxLength)) {
            String strNoEllipsis = str.substring(0, maxLength - 3);
            result = strNoEllipsis.concat("...");
        }

        return result;
    }
}
