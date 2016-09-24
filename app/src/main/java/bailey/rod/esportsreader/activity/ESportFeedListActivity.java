package bailey.rod.esportsreader.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.List;

import bailey.rod.esportsreader.adapter.AtomCollectionEntryListAdapter;
import bailey.rod.esportsreader.cache.ESportsCache;
import bailey.rod.esportsreader.net.GetXmlDocumentRequest;
import bailey.rod.esportsreader.net.VolleySingleton;
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
        VolleySingleton volley = VolleySingleton.getInstance().init(this);

        String documentRef;
        AtomCollectionDocument collectionDocument;
        ESportsCache cache = ESportsCache.getInstance();

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
            GetXmlDocumentRequest request = new GetXmlDocumentRequest(documentRef,
                                                                      new GetACDListener(documentRef),
                                                                      new GetACDErrorListener());
            showProgressMessage("Loading feed list...");
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
    private void updateDisplayPerCachedCollectionDocument(String documentHref) {
        showListView();

        AtomCollectionDocument collectionDocument = (AtomCollectionDocument) ESportsCache.getInstance().get
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
    private class GetACDErrorListener implements Response.ErrorListener {
        private final String TAG = GetACDListener.class.getSimpleName();

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.w(TAG, "error response: " + error.getMessage(), error.getCause());
            showErrorMessage(error.getMessage());
        }
    }

    private class GetACDListener implements Response.Listener<String> {
        private final String TAG = GetACDListener.class.getSimpleName();

        private final String documentHref;

        public GetACDListener(String documentHref) {
            this.documentHref = documentHref;
        }

        @Override
        public void onResponse(String response) {
            Log.i(TAG, "GetACDListener.onResponse: " + response);

            InputStream stream = new ByteArrayInputStream(Charset.forName("UTF-8").encode(response).array());
            // TODO: Prescan document to confirm it is atom format.
            AtomCollectionDocumentParser parser = new AtomCollectionDocumentParser();

            try {
                AtomCollectionDocument collectionDocument = parser.parse(stream, documentHref, "now");
                ESportsCache.getInstance().put(collectionDocument);
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
