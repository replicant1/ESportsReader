package bailey.rod.esportsreader.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.List;

import bailey.rod.esportsreader.adapter.AtomCollectionEntryListAdapter;
import bailey.rod.esportsreader.cache.SessionCache;
import bailey.rod.esportsreader.job.GetXmlDocumentJob;
import bailey.rod.esportsreader.job.IJobFailureHandler;
import bailey.rod.esportsreader.job.IJobSuccessHandler;
import bailey.rod.esportsreader.job.JobEngineSingleton;
import bailey.rod.esportsreader.util.ConfigSingleton;
import bailey.rod.esportsreader.xml.atom.AtomCollectionDocument;
import bailey.rod.esportsreader.xml.atom.AtomCollectionDocumentParser;
import bailey.rod.esportsreader.xml.atom.AtomCollectionEntry;

/**
 * Activity presents user with a list of feeds that cater to the same eSport.
 */
public class ESportFeedListActivity extends ESportAsyncRequestingActivity {

    public static final String EXTRA_ATOM_COLLECTION_DOCUMENT_HREF = "atom-collection-document-href";

    private static final String TAG = ESportFeedListActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConfigSingleton config = ConfigSingleton.getInstance().init(this);
        JobEngineSingleton jobEngine = JobEngineSingleton.getInstance();

        String documentRef;
        AtomCollectionDocument collectionDocument;
        SessionCache cache = SessionCache.getInstance();

        if (config.loadFromLocalAtomFiles()) {
            documentRef = config.localAtomCollectionDocument();
        } else {
            documentRef = getIntent().getStringExtra(EXTRA_ATOM_COLLECTION_DOCUMENT_HREF);
        }

        Log.i(TAG, String.format("Atom Collection Document at %s is required to display feed list",
                                 documentRef));

        if (cache.contains(documentRef)) {
            Log.d(TAG, "Retrieving ACD from cache");
            // TODO Update to date check
            collectionDocument = (AtomCollectionDocument) cache.get(documentRef);
            updateDisplayPerCachedCollectionDocument(documentRef);
        } else {
            Log.d(TAG, "ACD not in cache. Getting ACD async from file system or remote server");
            showProgressMessage("Loading feed list...");
            GetXmlDocumentJob job = new GetXmlDocumentJob(documentRef, "lastModified");
            jobEngine.doJobAsync(job,//
                                 new GetACDSuccessHandler(documentRef),//
                                 new GetACDFailureHandler());
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
    private void updateDisplayPerCachedCollectionDocument(String documentHref) {
        showListView();

        AtomCollectionDocument collectionDocument = (AtomCollectionDocument) SessionCache.getInstance().get
                (documentHref);

        // Update the ListView
        List<AtomCollectionEntry> collectionEntries = collectionDocument.getEntries();
        AtomCollectionEntryListAdapter adapter = new AtomCollectionEntryListAdapter(ESportFeedListActivity
                                                                                            .this,
                                                                                    collectionEntries);
        listView.setAdapter(adapter);

        // Update the action bar
        getSupportActionBar().setTitle(collectionDocument.getTitle());
    }

    /**
     * Called by Volley if the request to load the list of feeds fails. Displays an error message
     * if so. Leaves the user to redo the command by clicking the "Retry" button.
     */
    private class GetACDFailureHandler implements IJobFailureHandler {
        private final String TAG = GetACDFailureHandler.class.getSimpleName();

        @Override
        public void onFailure(String failureMessage) {
            Log.w(TAG, failureMessage);
            showErrorMessage(failureMessage);
        }
    }

    private class GetACDSuccessHandler implements IJobSuccessHandler{
        private final String TAG = GetACDSuccessHandler.class.getSimpleName();

        private final String documentHref;

        public GetACDSuccessHandler(String documentHref) {
            this.documentHref = documentHref;
        }

        @Override
        public void onSuccess(String response) {
            Log.i(TAG, "GetACDSuccessHandler.onResponse: " + response);

            InputStream stream = new ByteArrayInputStream(Charset.forName("UTF-8").encode(response).array());
            // TODO: Prescan document to confirm it is atom format.
            AtomCollectionDocumentParser parser = new AtomCollectionDocumentParser();

            try {
                AtomCollectionDocument collectionDocument = parser.parse(stream, documentHref, "now");
                SessionCache.getInstance().put(collectionDocument);
                updateDisplayPerCachedCollectionDocument(documentHref);
            } catch (XmlPullParserException xppe) {
                Log.w(TAG, "Failed to parse " + documentHref, xppe);
            } catch (ParseException pex) {
                Log.w(TAG, "Failed to parse " + documentHref, pex);
            } catch (IOException iox) {
                Log.w(TAG, "Failed to parse " + documentHref, iox);
            }
        }
    }
}
