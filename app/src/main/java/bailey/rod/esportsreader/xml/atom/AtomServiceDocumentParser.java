package bailey.rod.esportsreader.xml.atom;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import bailey.rod.esportsreader.util.XPPUtils;

/**
 * Parses an Atom "service document" - only extracting the info in which we are interested.
 * <p>
 * For our purposes, an Atom Service Document looks like below. The only tags we are interested in are "service",
 * "workspace", "atom:title" and "collection". For each "collection" we are only interested in it's "href" attribute
 * and the text contents of its "atom:title" child tag.
 * </p>
 * NOTE: We assume tags are ordered exactly as they appear below.
 * <p>
 * <pre>
 *   <?xml>
 *   <service>
 *     <workspace>
 *       <atom:title>eSportsReader Sports</atom:title>
 *
 *       <collection href="http://feed.esportsreader.com/reader/sports/dota2">
 *           <atom:id>... don't care ...</atom:id>
 *           <atom:title>Dota 2</atom:title>
 *           ... don't care about other child tags ...
 *       </collection>
 *
 *       <collection href="http://feed.esportsreader.com/reader/sports/starcraft">
 *           <atom:id>... don't care ...</atom:id>
 *           <atom:title>Starcraft</atom:title>
 *           ... don't care about other child tags ...
 *       </collection>
 * </pre>
 * <p>
 * The above document would be parsed into an instance of AtomServiceDocument with a "title" property of "eSportsReader
 * Sports" and a "collections" property containing two instances of AtomServiceCollection. Each AtomServiceCollection
 * has a "href" property and a "title" property.
 * <p>
 * See http://www.atomenabled.org/developers/protocol/#service
 */
public class AtomServiceDocumentParser {

    private static final String TAG = AtomServiceDocumentParser.class.getSimpleName();

    /**
     * Parses an Atom Service Document - only those aspects we are interested in.
     *
     * @param istream Stream to the service document
     * @return Structure containing parsed contents
     */
    public AtomServiceDocument parse(InputStream istream, String url, String etag)
            throws XmlPullParserException, IOException {
        Log.i(TAG, "** Into AtomServiceDocument.parse **");

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(istream, null);
            return parseService(parser, url, etag);
        } finally {
            Log.i(TAG, "** Finished AtomServiceDocument.parse **");
            istream.close();
        }
    }

    /**
     * Parse a "collection" tag and its children.
     *
     * @param parser Parser for service document. Should be positioned at the start of a "collection" tag.
     * @return Model object representing parsed "collection".
     */
    private AtomServiceCollection parseCollection(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        // First and only attribute of <collection> tag is "href"
        String collectionHref = parser.getAttributeValue(0);

        // Skip to the <atom:id> tag which is next
        XPPUtils.skipToNextStartTag(parser, "id");

        // Next tag will be <atom:title> which is next
        XPPUtils.skipToNextStartTag(parser, "title");

        // Text of the <atom:title> tag is the title of the collection
        String collectionTitle = parser.nextText();

        Log.i(TAG, "Found an AtomServiceCollection with title=" + collectionTitle + " and collectionHref=" +
                collectionHref);

        return new AtomServiceCollection(collectionTitle, collectionHref);
    }

    /**
     * Parse a "service" tag and its children.
     *
     * @param parser Parser for service document. Should be positioned at the start of the document.
     * @return Model object representing parsed "service".
     */
    private AtomServiceDocument parseService(XmlPullParser parser, String url, String etag)
            throws XmlPullParserException, IOException {
        List<AtomServiceCollection> collectionList = new LinkedList<>();
        String workspaceTitle;

        // Skip the <xml> tag, advancing to the <service> tag
        XPPUtils.skipToNextStartTag(parser, "service");

        //Skip the <service> tag, advancing to the <workspace> tag
        XPPUtils.skipToNextStartTag(parser, "workspace");

        // Skip the <workspace> tag, advancing to the <atom:title> tag
        XPPUtils.skipToNextStartTag(parser, "title");

        // Text of the <atom:title> tag is the title of the workspace
        workspaceTitle = parser.nextText();

        Log.i(TAG, "Workspace title is " + workspaceTitle);

        // Advance through all the <collection> tags until end of document
        while (XPPUtils.skipToNextStartTag(parser, "collection")) {
            collectionList.add(parseCollection(parser));
        }

        Log.i(TAG, "Finished parsing AtomServiceDocument");

        return new AtomServiceDocument(workspaceTitle, collectionList, url, etag);
    }


}
