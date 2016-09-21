package bailey.rod.esportsreader.util;

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

    public static boolean skipToNextStartTag(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean startTagFound = false;

        while (!startTagFound) {
            if ((parser.getEventType() == XmlPullParser.START_TAG)) {
                startTagFound = true;
            } else if (parser.getEventType() == XmlPullParser.END_DOCUMENT) {
                break;
            } else {
                parser.next();
            }
        }


        return startTagFound;
    }

    public static boolean skipToNextStartTagExclusive(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean startTagFound = false;

        parser.next();

        while (!startTagFound) {
            startTagFound = skipToNextStartTag(parser);
        }

        return startTagFound;
    }

//    public static String statusAsString(XmlPullParser parser) throws XmlPullParserException {
//        parser.getName();
//        parser.getEventType();
//        parser.getDepth();
//        parser.getColumnNumber();
//        parser.getPrefix();
//        parser.get
//    }
}
