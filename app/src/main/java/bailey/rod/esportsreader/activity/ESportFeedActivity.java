package bailey.rod.esportsreader.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import bailey.rod.esportsreader.R;
import bailey.rod.esportsreader.adapter.ESportsFeedEntrySynopsisListAdapter;
import bailey.rod.esportsreader.cache.SessionCache;
import bailey.rod.esportsreader.job.GetXmlDocumentJob;
import bailey.rod.esportsreader.job.IJobFailureHandler;
import bailey.rod.esportsreader.job.IJobSuccessHandler;
import bailey.rod.esportsreader.job.JobEngineSingleton;
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
        JobEngineSingleton jobEngine = JobEngineSingleton.getInstance();

        String documentHref;
        ESportsFeed eSportsFeed;
        SessionCache cache = SessionCache.getInstance();

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
            showProgressMessage("Loading feed...");
            GetXmlDocumentJob job = new GetXmlDocumentJob(documentHref, "lastModified");
            jobEngine.doJobAsync(job, //
                                 new GetFeedDocumentSuccessHandler(documentHref), //
                                 new GetFeedDocumentFailureHandler());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        JobEngineSingleton.getInstance().cancelAll();
    }

    /**
     * By the time this is called, the SessionCache is guaranteed to contain a copy of documentHref that is
     * up-to-date enough to be displayed in the list view. Either it will have been retrieved from an external source
     * and placed in the SessionCache, or it may have been found to be already in the SessionCache and just as
     * up-to-date as the external source.
     *
     * @param documentHref URL of the feed document. This is the key by which is retrieved from cache.
     */
    private void updateDisplayPerCachedFeedDocument(String documentHref) {
        showListView();
        ESportsFeed feed = (ESportsFeed) SessionCache.getInstance().get(documentHref);

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

    private class GetFeedDocumentFailureHandler implements IJobFailureHandler {
        private final String TAG = GetFeedDocumentFailureHandler.class.getSimpleName();

        @Override
        public void onFailure(String failureMsg) {
            Log.w(TAG, failureMsg);
            showErrorMessage(failureMsg);
        }
    }

    private class GetFeedDocumentSuccessHandler implements IJobSuccessHandler {
        private final String TAG = GetFeedDocumentSuccessHandler.class.getSimpleName();

        private final String documentHref;

        public GetFeedDocumentSuccessHandler(String documentHref) {
            this.documentHref = documentHref;
        }

        @Override
        public void onSuccess(String response) {
            Log.i(TAG, "GetFeedDocumentSuccessHandler.onResponse: " + response);

            InputStream stream = new ByteArrayInputStream(Charset.forName("UTF-8").encode(response).array());
            // TODO: Prescan doc to see if atom, rss or unrecognized. see RSSParser.
            AtomFeedParser parser = new AtomFeedParser();

            try {
                ESportsFeed feed = parser.parse(stream, documentHref, "now");
                SessionCache.getInstance().put(feed);
                updateDisplayPerCachedFeedDocument(documentHref);
            } catch (XmlPullParserException xppe) {
                Log.w(TAG, "Failed to parse " + documentHref, xppe);
            } catch (IOException iox) {
                Log.w(TAG, "Failed to parse " + documentHref, iox);
            }
        }
    }
}
