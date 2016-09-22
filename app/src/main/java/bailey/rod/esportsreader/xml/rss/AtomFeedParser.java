package bailey.rod.esportsreader.xml.rss;

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

    private String parseLink(XmlPullParser parser) throws XmlPullParserException, IOException {
        String result = null;

        // At entry, parser is at START_TAG of <link>
        Log.d(TAG, "Attribute count for link is " + parser.getAttributeCount());

        for (int i = 0; i < parser.getAttributeCount(); i++) {
            String attributeValue = parser.getAttributeValue(i);
            String attributeName = parser.getAttributeName(i);
            if ("href".equals(attributeName)) {
                result = attributeValue;
                break;
            }
        }

        parser.next();

        return result;
    }

    /**
     * Gets the body of the current tag, where the body itself contains XHTML.
     *
     * @param parser Parser whose current event type is START_TAG and whose current tag is the one whose
     *               body contains the XHTML we want to get as a string.
     * @return XHTML of the body of the current tag, as one string. Note that comments will not be preserved. Neither
     * will "xmlns" attributes.
     */
    private String parseNestedXHTML(XmlPullParser parser) throws XmlPullParserException, IOException {
        StringBuffer xhtml = new StringBuffer();

        String openingTagName = parser.getName();
        Log.d(TAG, "Opening tag name=" + openingTagName);

        boolean continueReading = true;

        while (continueReading) {
            parser.next();
            switch (parser.getEventType()) {
                case XmlPullParser.START_TAG:
                    // attributeBuf contains all the attribute name/value pairs for this tag as a string
                    StringBuffer attributeBuf = new StringBuffer();

                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        String attributePrefix = parser.getAttributePrefix(i);
                        String attributeName = parser.getAttributeName(i);
                        String attributeValue = parser.getAttributeValue(i);

                        if (attributePrefix == null) {
                            attributeBuf.append(String.format("%s=\"%s\"", attributeName, attributeValue));
                        } else {
                            attributeBuf.append(String.format("%s:%s=\"%s\"", attributePrefix, attributeName,
                                                              attributePrefix));
                        }

                        // Space after all attribute values except last, to separate from next attribute def
                        if (i != (parser.getAttributeCount() - 1)) {
                            attributeBuf.append(" ");
                        }
                    }

                    // Append the start tag to xhtml, with or without attributes
                    if (attributeBuf.length() == 0) {
                        xhtml.append(String.format("<%s>", parser.getName()));
                    } else {
                        xhtml.append(String.format("<%s %s>", parser.getName(), attributeBuf.toString()));
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if (openingTagName.equals(parser.getName())) {
                        continueReading = false;
                    } else {
                        xhtml.append("</" + parser.getName() + ">");
                    }
                    break;

                case XmlPullParser.TEXT:
                    xhtml.append(parser.getText());
                    break;

                case XmlPullParser.END_DOCUMENT:
                    continueReading = false;
                    break;
            }
        }

        String result = xhtml.toString();
        Log.d(TAG, "Get Nested XHTML is \"" + result + "\"");
        return result;
    }
}
