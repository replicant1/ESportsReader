package bailey.rod.esportsreader.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import bailey.rod.esportsreader.R;
import bailey.rod.esportsreader.adapter.AtomServiceCollectionListAdapter;
import bailey.rod.esportsreader.cache.SessionCache;
import bailey.rod.esportsreader.job.GetXmlDocumentJob;
import bailey.rod.esportsreader.job.IJobFailureHandler;
import bailey.rod.esportsreader.job.IJobSuccessHandler;
import bailey.rod.esportsreader.job.JobEngineSingleton;
import bailey.rod.esportsreader.util.ConfigSingleton;
import bailey.rod.esportsreader.xml.atom.AtomServiceCollection;
import bailey.rod.esportsreader.xml.atom.AtomServiceDocument;
import bailey.rod.esportsreader.xml.atom.AtomServiceDocumentParser;

/**
 * Activity presents user with a list of eSports to choose from. This is the first screen the user sees when the app
 * starts up. It contains options like "League of Legends", "Hearthstone" etc.
 */
public class ESportListActivity extends ESportAsyncRequestingActivity {

    /**
     * Tag for Android logging
     */
    private static final String TAG = ESportListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConfigSingleton config = ConfigSingleton.getInstance().init(this);
        JobEngineSingleton jobEngine = JobEngineSingleton.getInstance();

        String documentHref;
        AtomServiceDocument serviceDocument;
        SessionCache cache = SessionCache.getInstance();

        if (config.loadFromLocalAtomFiles()) {
            documentHref = config.localAtomServiceDocument();
        } else {
            documentHref = config.atomServiceDocument();
        }

        Log.i(TAG, String.format("Atom Service Document at %s required to display eSport list", documentHref));

        if (cache.contains(documentHref)) {
            Log.d(TAG, "Retrieving ASD from cache");
            // TODO: Up-to-date check
            serviceDocument = (AtomServiceDocument) cache.get(documentHref);
            updateDisplayPerCachedServiceDocument(documentHref);

        } else {
            Log.d(TAG, "ASD not in cache. Retrieving ASD async from file system or remote server");
            showProgressMessage("Loading eSports...");
            GetXmlDocumentJob job = new GetXmlDocumentJob(documentHref, "lastModified");
            jobEngine.doJobAsync(job, //
                                 new GetASDSuccessHandler(documentHref), //
                                 new GetASDFailureHandler());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.esport_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete_cache) {
            Log.i(TAG, "Clear cache");
            SessionCache.getInstance().clear();

            // Restart the present activity with an empty cache
            recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
     * @param documentHref
     */
    private void updateDisplayPerCachedServiceDocument(String documentHref) {
        showListView();
        AtomServiceDocument serviceDocument = (AtomServiceDocument) SessionCache.getInstance().get(documentHref);

        List<AtomServiceCollection> collections = serviceDocument.getCollections();
        AtomServiceCollectionListAdapter adapter = new AtomServiceCollectionListAdapter(ESportListActivity.this,
                                                                                        collections);
        listView.setAdapter(adapter);

        getSupportActionBar().setTitle(serviceDocument.getTitle());
    }

    /**
     * Called by Volley if the request to load the list of eSports fails. Displays an error message if so. Leaves the
     * user to manually redo the command using the "refresh" icon in the action bar.
     */
    private class GetASDFailureHandler implements IJobFailureHandler {
        private final String TAG = GetASDFailureHandler.class.getSimpleName();

        @Override
        public void onFailure(String failureMsg) {
            Log.w(TAG, failureMsg);
            showErrorMessage(failureMsg);
        }
    }

    /**
     * Called by Volley if the request to load the ASD succeeds. Parses the document just loaded and
     * feeds the data into a listView for display. Also copied to cache.
     */
    private class GetASDSuccessHandler implements IJobSuccessHandler {
        private final String TAG = GetASDSuccessHandler.class.getSimpleName();

        private final String documentHref;

        public GetASDSuccessHandler(String documentHref) {
            this.documentHref = documentHref;
        }

        @Override
        public void onSuccess(String response) {
            Log.i(TAG, "onResponse: " + response);

            InputStream stream = new ByteArrayInputStream(Charset.forName("UTF-8").encode(response).array());
            AtomServiceDocumentParser parser = new AtomServiceDocumentParser();

            try {
                AtomServiceDocument serviceDocument = parser.parse(stream, documentHref, "now");
                SessionCache.getInstance().put(serviceDocument);
                updateDisplayPerCachedServiceDocument(documentHref);
            } catch (XmlPullParserException xppe) {
                Log.w(TAG, "Failed to parse " + documentHref, xppe);
            } catch (IOException iox) {
                Log.w(TAG, "Failed to parse " + documentHref, iox);
            }
        }
    }
}
