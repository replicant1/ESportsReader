package bailey.rod.esportsreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.List;

import bailey.rod.esportsreader.R;
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
            collectionDocument = (AtomCollectionDocument) cache.get(documentRef);
        } else {
            Log.d(TAG, "ACD not in cache. Getting ACD async from file system or remove server");
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

            showListView();

            InputStream stream = new ByteArrayInputStream(Charset.forName("UTF-8").encode(response).array());
            AtomCollectionDocumentParser parser = new AtomCollectionDocumentParser();

            try {
                AtomCollectionDocument collectionDocument = parser.parse(stream, documentHref, "now");
                ESportsCache.getInstance().put(collectionDocument);

                List<AtomCollectionEntry> collectionEntries = collectionDocument.getEntries();
                AtomCollectionEntryListAdapter adapter = new AtomCollectionEntryListAdapter(ESportFeedListActivity
                                                                                                    .this,
                                                                                            collectionEntries);
                listView.setAdapter(adapter);
                getSupportActionBar().setTitle(collectionDocument.getTitle());
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
