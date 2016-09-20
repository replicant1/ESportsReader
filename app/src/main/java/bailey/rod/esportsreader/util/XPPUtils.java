package bailey.rod.esportsreader.util;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Utility methods associated with the XmlPullParser.
 */
public class XPPUtils {

    private static String TAG = XPPUtils.class.getSimpleName();

    public static boolean skipToNextEndTag(XmlPullParser parser, String tagName) throws XmlPullParserException,
            IOException {
        boolean endTagFound = false;

        while (!endTagFound) {
            if ((parser.getEventType() == XmlPullParser.END_TAG) && parser.getName().equals(tagName)) {
                endTagFound = true;
            } else if (parser.getEventType() == XmlPullParser.END_DOCUMENT) {
                break;
            } else {
                parser.next();
            }
        }

        return endTagFound;
    }

    public static boolean skipToNextStartTag(XmlPullParser parser, String tagName) throws XmlPullParserException,
            IOException {
        boolean startTagFound = false;

        while (!startTagFound) {
            if ((parser.getEventType() == XmlPullParser.START_TAG) && parser.getName().equals(tagName)) {
                startTagFound = true;
            } else if (parser.getEventType() == XmlPullParser.END_DOCUMENT) {
                break;
            } else {
                parser.next();
            }
        }

        return startTagFound;
    }
}
