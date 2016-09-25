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
import bailey.rod.esportsreader.xml.ISyndicationDocumentParser;

/**
 * Parses an RSS feed - only extracting the information this app is interested in. For our purposes, an RSS
 * feed looks like this:
 * <pre>
 *     <rss ...>
 *         <title>Title of RSS Feed</title>
 *         .. other tags ...
 *         <item>
 *             ... order of the following 4 tags may vary
 *             <title>Title of item 1</title>
 *             <link>URL of item 1 in context of source web site</link>
 *             <description>Optional synopsis of item 1</description>
 *             <pubDate>Date/time when item 1 was first published</pubDate>
 *             <content:encoded>Optional full content of item 1</content:encoded>
 *             <a10:updated>Optional date/time when item 1 was last updated</a10:updated>
 *         </item>
 *         ... More <item> tags until </rss>
 *     </rss>
 * </pre>
 * NOTE: For each "item" tag it is assumed that at least one of the "description" and "content" child tags will be
 * present.
 * This class has only been tested on RSS 2.0 but may well work on other versions of RSS.
 */
public class RSSFeedParser implements ISyndicationDocumentParser {

    private static final String TAG = RSSFeedParser.class.getSimpleName();

    public ESportsFeed parse(InputStream inputStream, String url, String etag) throws XmlPullParserException,
            IOException {
        Log.i(TAG, "** Into RSSFeedParser.parse() **");

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);
            return parseFeed(parser, url, etag);
        } finally {
            inputStream.close();
        }
    }

    private ESportsFeed parseFeed(XmlPullParser parser, String url, String etag) throws
            XmlPullParserException,
            IOException {
        List<ESportsFeedEntry> entryList = new LinkedList<>();

        // Skip the <?xml> declaration advance to the <rss> feed tag
        XPPUtils.skipToNextStartTag(parser, "rss");

        // Skip to the <title> tag, text of which is the title of the feed
        XPPUtils.skipToNextStartTag(parser, "title");
        String feedTitle = parser.nextText();

        // Advance through all the <item> tags until the end of the document
        while (XPPUtils.skipToNextStartTag(parser, "item")) {
            entryList.add(parseItem(parser));
        }

        return new ESportsFeed(feedTitle, entryList, url, etag);
    }

    private ESportsFeedEntry parseItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        String title = null;
        String link = null;
        String description = null;
        String content = null;
        String publishedDate = null;
        String lastUpdated = null;

        // At this point, the parser's current tag is "item" and event type is START_TAG

        // Go to first child of <item> tag
        XPPUtils.skipToNextStartTagExclusive(parser);

        while (XPPUtils.skipToNextStartTag(parser)) {
            String tagName = parser.getName();

            if ("title".equals(tagName)) {
                title = parser.nextText();
            } else if ("link".equals(tagName)) {
                link = parser.nextText();
            } else if ("description".equals(tagName)) {
                description = parser.nextText();
            } else if ("encoded".equals(tagName) && ("content".equals(parser.getPrefix()))) {
                content = parser.nextText();
            } else if ("pubDate".equals(tagName)) {
                publishedDate = parser.nextText();
            } else if ("updated".equals(tagName)) {
                lastUpdated = parser.nextText();
            } else if ("item".equals(tagName)) {
                break;
            } else if (parser.getEventType() == XmlPullParser.END_DOCUMENT) {
                break;
            } else {
                parser.next();
            }

        } // while

        ESportsFeedEntry result = new ESportsFeedEntry(content, lastUpdated, link, publishedDate, description, title);
        Log.d(TAG, "Parsed ESportsFeedEntry: " + result);
        return result;
    }
}
