package bailey.rod.esportsreader.xml.atom;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import bailey.rod.esportsreader.util.DateUtils;
import bailey.rod.esportsreader.util.XPPUtils;

/**
 * Parses an Atom "Collection Document" - only extracting the information we are interested in.
 *
 * See http://www.atomenabled.org/developers/protocol/#collection
 */
public class AtomCollectionDocumentParser {

    private static final String TAG = AtomCollectionDocumentParser.class.getSimpleName();

    public AtomCollectionDocument parse(InputStream inputStream)
            throws XmlPullParserException, IOException, ParseException {
        Log.i(TAG, "** Into AtomCollectionDocument.parse() **");

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);
            return parseFeed(parser);
        } finally {
            Log.i(TAG, "** Finished AtomCollectionDocumentParser **");
            inputStream.close();
        }
    }

    private AtomCollectionEntry parseEntry(XmlPullParser parser)
            throws XmlPullParserException, IOException, ParseException {
        // Skip to <title> tag, text of which is the title of the feed
        XPPUtils.skipToNextStartTag(parser, "title");
        String title = parser.nextText();

        // Skip to <updated> tag, text of which is the date this feed was
        // last (substantially) updated.
        XPPUtils.skipToNextStartTag(parser, "updated");
        String updatedStr = parser.nextText();

        // Skip to <summary> tag, text of which is a brief description of the feed
        XPPUtils.skipToNextStartTag(parser, "summary");
        String summary = parser.nextText();

        // Skip to the <link rel="via"> tag, text the "href" attribute of this tag
        // is URL of the feed.
        XPPUtils.skipToNextStartTag(parser, "link");
        String href = parser.getAttributeValue(1);

        AtomCollectionEntry result = new AtomCollectionEntry(title, updatedStr, summary, href);

        Log.d(TAG, "Parsed entry: " + result);

        return result;
    }

    private AtomCollectionDocument parseFeed(XmlPullParser parser)
            throws XmlPullParserException, IOException, ParseException {
        List<AtomCollectionEntry> entryList = new LinkedList<>();

        // Skip the <?xml> declaration, advancing to the <feed> tag
        XPPUtils.skipToNextStartTag(parser, "feed");

        // Skip to the <title> tag, text of which is the title of the feed
        XPPUtils.skipToNextStartTag(parser, "title");
        String feedTitle = parser.nextText();

        // Skip to the <updated> tag, text of which is last updated
        XPPUtils.skipToNextStartTag(parser, "updated");
        String updated = parser.nextText();

        // Advance through all the <entry> tags until end of document
        while (XPPUtils.skipToNextStartTag(parser, "entry")) {
            entryList.add(parseEntry(parser));
        }

        AtomCollectionDocument result = new AtomCollectionDocument(feedTitle, entryList);
        return result;
    }

}
