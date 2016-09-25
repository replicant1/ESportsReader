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
import bailey.rod.esportsreader.cache.ICacheable;
import bailey.rod.esportsreader.cache.SessionCache;
import bailey.rod.esportsreader.job.GetXmlDocumentJob;
import bailey.rod.esportsreader.job.IJobFailureHandler;
import bailey.rod.esportsreader.job.IJobSuccessHandler;
import bailey.rod.esportsreader.job.JobEngineSingleton;
import bailey.rod.esportsreader.job.TimestampedXmlDocument;
import bailey.rod.esportsreader.util.ConfigSingleton;
import bailey.rod.esportsreader.util.DateUtils;
import bailey.rod.esportsreader.util.XPPUtils;
import bailey.rod.esportsreader.xml.ESportsFeed;
import bailey.rod.esportsreader.xml.ESportsFeedEntry;
import bailey.rod.esportsreader.xml.ISyndicationDocumentParser;
import bailey.rod.esportsreader.xml.atom.AtomFeedParser;
import bailey.rod.esportsreader.xml.rss.RSSFeedParser;

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

        // Check cache to see how recent a copy of the ACD we have, if any
        String etag = null;

        if (cache.contains(documentHref)) {
            etag = cache.get(documentHref).getEtag();
            Log.d(TAG, String.format("ACD exists in cache with etag = " + etag));
        }

        showProgressMessage("Loading feed...");
        GetXmlDocumentJob job = new GetXmlDocumentJob(documentHref, etag);
        IJobFailureHandler failureHandler = new GetFeedDocumentFailureHandler();
        IJobSuccessHandler successHandler = new GetFeedDocumentSuccessHandler(documentHref, failureHandler);
        jobEngine.doJobAsync(job, successHandler, failureHandler);
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

        private final IJobFailureHandler failureHandler;

        public GetFeedDocumentSuccessHandler(String documentHref, IJobFailureHandler failureHandler) {
            this.documentHref = documentHref;
            this.failureHandler = failureHandler;
        }

        @Override
        public void onSuccess(Object result) {
            TimestampedXmlDocument timedDoc = (TimestampedXmlDocument) result;

            Log.i(TAG, "GetFeedDocumentSuccessHandler.onSuccess: " + timedDoc);

            // If we've just retrieved an identical copy to what's already in the cache, don't bother
            // adding to the SessionCache.
            if (SessionCache.getInstance().containsDifferentVersion(documentHref, timedDoc.getEtag())) {
                Log.i(TAG, String.format("Parsing %s to add to cache", documentHref));
                ISyndicationDocumentParser parser = null;

                // Have a quick peek ahead into the XML document to see what XML format it's in, so we
                // can setup the appropriate parser.

                if (timedDoc.getContent() != null) {
                    switch (XPPUtils.getSyndicationFormat(timedDoc.getContent())) {
                        case ATOM:
                            parser = new AtomFeedParser();
                            break;
                        case RSS:
                            parser = new RSSFeedParser();
                            break;
                        case UNRECOGNIZED:
                            Log.w(TAG, "Unrecognized syndication format in " + documentHref);
                            failureHandler.onFailure("Unrecognized syndication format in " + documentHref);
                            break;
                    }
                }

                if ((parser != null) && (timedDoc.getContent() != null)) {
                    InputStream stream = new ByteArrayInputStream(Charset.forName("UTF-8").encode(
                            timedDoc.getContent()).array());

                    try {
                        ICacheable feed = parser.parse(stream, documentHref, timedDoc.getEtag());
                        SessionCache.getInstance().put(feed);

                    } catch (XmlPullParserException xppe) {
                        Log.w(TAG, "Failed to parse " + documentHref, xppe);
                    } catch (IOException iox) {
                        Log.w(TAG, "Failed to parse " + documentHref, iox);
                    }
                }
            } else {
                Log.i(TAG, String.format("Not parsing %s and not adding to cache", documentHref));
            }

            updateDisplayPerCachedFeedDocument(documentHref);
        }
    }
}
