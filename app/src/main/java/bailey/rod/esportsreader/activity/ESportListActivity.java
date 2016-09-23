package bailey.rod.esportsreader.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import bailey.rod.esportsreader.R;
import bailey.rod.esportsreader.adapter.AtomServiceCollectionListAdapter;
import bailey.rod.esportsreader.cache.ESportsCache;
import bailey.rod.esportsreader.net.GetXmlDocumentRequest;
import bailey.rod.esportsreader.net.VolleySingleton;
import bailey.rod.esportsreader.util.ConfigSingleton;
import bailey.rod.esportsreader.xml.atom.AtomServiceCollection;
import bailey.rod.esportsreader.xml.atom.AtomServiceDocument;
import bailey.rod.esportsreader.xml.atom.AtomServiceDocumentParser;

/**
 * Activity presents user with a list of eSports to choose from. This is the first screen the user sees when the app
 * starts up. It contains options like "League of Legends", "Hearthstone" etc.
 */
public class ESportListActivity extends AppCompatActivity {

    /** Tag for Android logging */
    private static final String TAG = ESportListActivity.class.getSimpleName();

    private TextView errorMessage;

    private ListView listView;

    private TextView progressMessage;

    private View progressMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_with_list_view);

        progressMonitor = findViewById(R.id.progress_monitor);
        listView = (ListView) ESportListActivity.this.findViewById(R.id.esport_list_view);
        errorMessage = (TextView) findViewById(R.id.error_message);
        progressMessage = (TextView) findViewById(R.id.progress_message);

        ConfigSingleton config = ConfigSingleton.getInstance().init(this);
        VolleySingleton volley = VolleySingleton.getInstance().init(this);

        String documentHref;
        AtomServiceDocument serviceDocument;
        ESportsCache cache = ESportsCache.getInstance();

        if (config.loadFromLocalAtomFiles()) {
            documentHref = config.localAtomServiceDocument();
        } else {
            documentHref = config.atomServiceDocument();
        }

        Log.i(TAG, String.format("Atom Service Document at %s required to display eSport list", documentHref));

        if (cache.contains(documentHref)) {
            Log.d(TAG, "Retrieving ASD synch from cache");
            serviceDocument = (AtomServiceDocument) cache.get(documentHref);
        } else {
            Log.d(TAG, "ASD not in cache. Retrieving ASD async from file system or remote server");
            GetXmlDocumentRequest request = new GetXmlDocumentRequest(documentHref, //
                                                                      new GetASDListener(documentHref), //
                                                                      new GetASDErrorListener());
            showProgressMessage("Loading eSports...");
            volley.addRequest(request);
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
            ESportsCache.getInstance().clear();

            // Restart the present activity with an empty cache
            recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Show the error message panel while hiding the listView and progressMonitor. This occurs when an attempt to
     * load XML fails due to network problems.
     */
    private void showErrorMessage(String msg) {
        progressMonitor.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        errorMessage.setVisibility(View.VISIBLE);
        errorMessage.setText(msg);
    }

    /**
     * Show the listView while hiding the progressMonitor and errorMessage. This occurs when loading of XML completes
     * successfully.
     */
    private void showListView() {
        progressMonitor.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }

    /**
     * Show the progressMonitor and set a given progressMessage. Hide all else. This occurs when data loading is
     * underway but not yet complete.
     */
    private void showProgressMessage(String msg) {
        listView.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
        progressMonitor.setVisibility(View.VISIBLE);
        progressMessage.setText(msg);
    }

    /**
     * Called by Volley if the request to load the list of eSports fails. Displays an error message if so. Leaves the
     * user to manually redo the command using the "refresh" icon in the action bar.
     */
    private class GetASDErrorListener implements Response.ErrorListener {
        private final String TAG = GetASDErrorListener.class.getSimpleName();

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i(TAG, "error response: " + error.getMessage());
            showErrorMessage(error.getMessage());
        }
    }

    /**
     * Called by Volley if the request to load the list of eSports succeeds. Parses the document just loaded and
     * feeds the data into a listView for display. Also copied to cache.
     */
    private class GetASDListener implements Response.Listener<String> {
        private final String TAG = GetASDListener.class.getSimpleName();

        private final String documentHref;

        public GetASDListener(String documentHref) {
            this.documentHref = documentHref;
        }

        @Override
        public void onResponse(String response) {
            Log.i(TAG, "onResponse: " + response);

            showListView();

            InputStream stream = new ByteArrayInputStream(Charset.forName("UTF-8").encode(response).array());
            AtomServiceDocumentParser parser = new AtomServiceDocumentParser();

            try {
                AtomServiceDocument serviceDocument = parser.parse(stream, documentHref, "now");
                ESportsCache.getInstance().put(serviceDocument);

                List<AtomServiceCollection> serviceCollections = serviceDocument.getCollections();
                AtomServiceCollectionListAdapter adapter = new AtomServiceCollectionListAdapter(ESportListActivity.this,
                                                                                                serviceCollections);

                listView.setAdapter(adapter);
                getSupportActionBar().setTitle(serviceDocument.getTitle());
            } catch (XmlPullParserException xppe) {
                Log.w(TAG, "Failed to parse " + documentHref, xppe);
            } catch (IOException iox) {
                Log.w(TAG, "Failed to parse " + documentHref, iox);
            }
        }
    }
}
