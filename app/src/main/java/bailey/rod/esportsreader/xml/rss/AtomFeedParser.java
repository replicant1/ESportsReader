package bailey.rod.esportsreader.xml.rss;

import android.renderscript.ScriptGroup;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import bailey.rod.esportsreader.util.XPPUtils;
import bailey.rod.esportsreader.xml.ESportsFeed;
import bailey.rod.esportsreader.xml.ESportsFeedEntry;

/**
 * Parses an Atom feed - only extracting the information this app is interested in. Note that although
 * the "service_document.atom" and "collection_document.atom" documents are both in Atom format, this class does the
 * parsing of neither. Each has its own parsing class. </p>
 * AtomFeedParser is used only for parsing the low level Atom documents that contain the individual news items that
 * the user wants to read.
 * For our purposes, such an Atom feed looks like this:
 * <pre>
 *     <feed ...>
 *         ... other tags ...
 *         <title>Title of Atom Feed</title>
 *         ... other tag ...
 *         <entry>
 *             ... order of the following tags may vary
 *             <title> of entry 1</title>
 *             <link href="url">Optional URL of entry 1 in context of source web site</link>
 *             <summary>Optional synopsis of entry 1. XHTML or HTML.</summary>
 *             <published>Date/time entry 1 was originally published</published>
 *             <updated>Date/time entry 1 was last updated</updated>
 *             <content>Optional full content of entry 1. XHTML or HTML</content>
 *         </entry>
 *         ... More <entry> tags until </feed>
 *     </feed>
 * </pre>
 * This class has only been tested on Atom 1.0 but may well work on other versions of Atom.
 *
 * @see bailey.rod.esportsreader.xml.atom.AtomCollectionDocumentParser
 * @see bailey.rod.esportsreader.xml.atom.AtomServiceDocumentParser
 */
public class AtomFeedParser {

    private static final String TAG = AtomFeedParser.class.getSimpleName();

    // TODO: Augment this to copy the attributes as well
    private String getNestedXHTML(XmlPullParser parser) throws XmlPullParserException, IOException {
        // At entry , we are at START_TAG of tag whose body constitutes all the xhtml we want to return
        StringBuffer buf = new StringBuffer();

        String openingTagName = parser.getName();
        Log.d(TAG, "Opening tag name=" + openingTagName);

        boolean continueReading = true;

        while (continueReading) {
            parser.next();
            switch (parser.getEventType()) {
                case XmlPullParser.START_TAG:
                    buf.append("<" + parser.getName() + ">");
                    break;

                case XmlPullParser.END_TAG:
                    if (openingTagName.equals(parser.getName())) {
                        continueReading = false;
                    } else {
                        buf.append("</" + parser.getName() + ">");
                    }
                    break;

                case XmlPullParser.TEXT:
                    buf.append(parser.getText());
                    break;

                case XmlPullParser.END_DOCUMENT:
                    continueReading = false;
                    break;
            }
        }

        String result = buf.toString();
        Log.d(TAG, "Get Nested XHTML is \"" + result + "\"");
        return result;
    }

    public ESportsFeed parse(InputStream inputStream) throws XmlPullParserException, IOException {
        Log.i(TAG, "*** Into AtomFeedParser.parse() ***");

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);
            return parseFeed(parser);
        } finally {
            Log.i(TAG, "** Exiting AtomFeedParser.parse() **");
            inputStream.close();
        }
    }

    private ESportsFeedEntry parseEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        String title = null;
        String link = null;
        String summary = null;
        String content = null;
        String published = null;
        String updated = null;

        Log.d(TAG, "**** Into parseEntry ****");

        // At this point, the parser's current tag is "entry" and event type is START_TAG

        Log.d(TAG, "Before skip exclusive, parser state = " + parser.getPositionDescription());

        // Go to first child of <entry> tag
        XPPUtils.skipToNextStartTagExclusive(parser);

        Log.d(TAG, "After skip exlusive, parser.state =" + parser.getPositionDescription());

        while (XPPUtils.skipToNextStartTag(parser)) {
            Log.d(TAG, "At beginning of while loop, parser = " + parser.getPositionDescription());
            String tagName = parser.getName();

            if ("title".equals(tagName)) {
                title = parser.nextText();
            } else if ("link".equals(tagName)) {
                link = parseLink(parser);
            } else if ("summary".equals(tagName)) {
                summary = parseNestedXHTML(parser);
            } else if ("content".equals(tagName)) {
                content = parseNestedXHTML(parser);
            } else if ("published".equals(tagName)) {
                published = parser.nextText();
            } else if ("updated".equals(tagName)) {
                updated = parser.nextText();
            } else if ("entry".equals(tagName)) {
                break;
            } else if (parser.getEventType() == XmlPullParser.END_DOCUMENT) {
                break;
            } else {
                parser.next();
            }
        } // while

        ESportsFeedEntry entry = new ESportsFeedEntry(content, updated, link, published, summary, title);

        Log.d(TAG, "Parsed Atom entry: " + entry);

        return entry;
    }

    private String parseLink(XmlPullParser parser) throws XmlPullParserException, IOException {
        String result = null;

        // At entry, parser is at START_TAG of <link>
        Log.d(TAG, "Attribute count for link is " + parser.getAttributeCount());

        for (int i = 0; i < parser.getAttributeCount(); i++) {
            String attributeValue= parser.getAttributeValue(i);
            String attributeName= parser.getAttributeName(i);
            if ("href".equals(attributeName)) {
                result = attributeValue;
                break;
            }
        }

        parser.next();

        return result;
    }

    private ESportsFeed parseFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<ESportsFeedEntry> entryList = new LinkedList<>();

        Log.d(TAG, "Into parseFeed");

        // Skip the <?xml> declaratio advance to the <feed> tag
        XPPUtils.skipToNextStartTag(parser, "feed");

        // Skip to the <title> tag, text of which is the title of the feed
        XPPUtils.skipToNextStartTag(parser, "title");
        String feedTitle = parser.nextText();

        Log.d(TAG, "Got feedTitle=" + feedTitle);

        // Iterate over all the <entry> tags until the end of the document
        while (XPPUtils.skipToNextStartTag(parser, "entry")) {
            entryList.add(parseEntry(parser));
        }

        ESportsFeed result = new ESportsFeed(feedTitle, entryList);
        Log.d(TAG, "Parsed atom feed to: " + result);
        return result;
    }

    private String parseNestedXHTML(XmlPullParser parser) throws XmlPullParserException, IOException {
        // At entry, parser is at START_TAG for <content type="xhtml"> or <content type="html">
        // If "type" attribute, html appears in the <content> tag. If xhtml, there is a <div> first and
        // THEN html.
        String typeAttrValue = parser.getAttributeValue(0);
        String result = null;

//        if ("xhtml".equals(typeAttrValue)) {
//            Log.d(TAG, "Into xhtml clause");
////            Log.d(TAG, "Prior to skipping to div, parser=" + parser.getPositionDescription() + ",depth=" + parser.getDepth());
//            result = getNestedXHTML(parser);
//            // Leave parser at END_TAG of "content"
//        } else if ("html".equals(typeAttrValue)) {
//            result = parser.getText();
//            // Leave parser at END_TAG of "content"
//        } else {
//            result = "Unrecognized XHTML";
//            // Leave parser at START_TAG of "content"
//        }

        result =getNestedXHTML(parser);

        return result;
    }


}
