package bailey.rod.esportsreader.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import bailey.rod.esportsreader.R;
import bailey.rod.esportsreader.adapter.ESportsFeedEntrySynopsisListAdapter;
import bailey.rod.esportsreader.cache.ESportsCache;
import bailey.rod.esportsreader.job.GetXmlDocumentRequest;
import bailey.rod.esportsreader.job.VolleySingleton;
import bailey.rod.esportsreader.util.ConfigSingleton;
import bailey.rod.esportsreader.xml.ESportsFeed;
import bailey.rod.esportsreader.xml.ESportsFeedEntry;
import bailey.rod.esportsreader.xml.rss.AtomFeedParser;

/**
 * Activity presents user with a list of news feeds relating to the same eSport.
 */
public class ESportFeedActivity extends ESportAsyncRequestingActivity {

    public static final String EXTRA_FEED_HREF = "feed-href";

    private static final String TAG = ESportFeedActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConfigSingleton config = ConfigSingleton.getInstance();
        VolleySingleton volley = VolleySingleton.getInstance();

        String documentHref;
        ESportsFeed eSportsFeed;
        ESportsCache cache = ESportsCache.getInstance();

        if (config.loadFromLocalAtomFiles()) {
            documentHref = config.localFeed();
        } else {
            documentHref = getIntent().getStringExtra(EXTRA_FEED_HREF);
        }

        Log.d(TAG, String.format("Feed at %s is required to display list of entries", documentHref));

        if (cache.contains(documentHref)) {
            Log.d(TAG, "Retrieving feed from cache");
            // TODO: Up-to-date check
            eSportsFeed = (ESportsFeed) cache.get(documentHref);
            updateDisplayPerCachedFeedDocument(documentHref);
        } else {
            Log.d(TAG, "Feed doc not in cache. Getting feed from file system or remote server");
            GetXmlDocumentRequest request = new GetXmlDocumentRequest(documentHref,
                                                                      new GetFeedDocumentListener(documentHref),
                                                                      new GetFeedDocumentErrorListener());
            showProgressMessage("Loading feed...");
            volley.addRequest(request);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //VolleySingleton.getInstance().cancelAll();
    }

    /**
     * By the time this is called, the ESportsCache is guaranteed to contain a copy of documentHref that is
     * up-to-date enough to be displayed in the list view. Either it will have been retrieved from an external source
     * and placed in the ESportsCache, or it may have been found to be already in the ESportsCache and just as
     * up-to-date as the external source.
     *
     * @param documentHref URL of the feed document. This is the key by which is retrieved from cache.
     */
    private void updateDisplayPerCachedFeedDocument(String documentHref) {
        showListView();
        ESportsFeed feed = (ESportsFeed) ESportsCache.getInstance().get(documentHref);

        // Update the ListView
        List<ESportsFeedEntry> entries = feed.getEntries();
        ESportsFeedEntrySynopsisListAdapter adapter = new ESportsFeedEntrySynopsisListAdapter
                (ESportFeedActivity.this,
                 entries);
        ListView listView = (ListView) findViewById(R.id.esport_list_view);
        listView.setAdapter(adapter);

        // Update the action bar
        getSupportActionBar().setTitle(feed.getTitle());
    }

    private class GetFeedDocumentErrorListener implements Response.ErrorListener {
        private final String TAG = GetFeedDocumentErrorListener.class.getSimpleName();

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.w(TAG, "error response:" + error.getMessage(), error.getCause());
            showErrorMessage(error.getMessage());
        }
    }

    private class GetFeedDocumentListener implements Response.Listener<String> {
        private final String TAG = GetFeedDocumentListener.class.getSimpleName();

        private final String documentHref;

        public GetFeedDocumentListener(String documentHref) {
            this.documentHref = documentHref;
        }

        @Override
        public void onResponse(String response) {
            Log.i(TAG, "GetFeedDocumentListener.onResponse: " + response);


            InputStream stream = new ByteArrayInputStream(Charset.forName("UTF-8").encode(response).array());
            // TODO: Prescan doc to see if atom, rss or unrecognized. see RSSParser.
            AtomFeedParser parser = new AtomFeedParser();

            try {
                ESportsFeed feed = parser.parse(stream, documentHref, "now");
                ESportsCache.getInstance().put(feed);
                updateDisplayPerCachedFeedDocument(documentHref);
            } catch (XmlPullParserException xppe) {
                Log.w(TAG, "Failed to parse " + documentHref, xppe);
            } catch (IOException iox) {
                Log.w(TAG, "Failed to parse " + documentHref, iox);
            }
        }
    }
}
