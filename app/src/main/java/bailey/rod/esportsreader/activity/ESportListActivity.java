package bailey.rod.esportsreader.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import bailey.rod.esportsreader.R;
import bailey.rod.esportsreader.adapter.AtomServiceCollectionListAdapter;
import bailey.rod.esportsreader.cache.ESportsCache;
import bailey.rod.esportsreader.util.ConfigSingleton;
import bailey.rod.esportsreader.xml.atom.AtomServiceCollection;
import bailey.rod.esportsreader.xml.atom.AtomServiceDocument;
import bailey.rod.esportsreader.xml.atom.AtomServiceDocumentParser;

/**
 * Activity presents user with a list of eSports to choose from. This is the first screen the user sees when the app
 * starts up. It contains options like "League of Legends", "Hearthstone" etc.
 */
public class ESportListActivity extends AppCompatActivity {

    private static final String TAG = ESportListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConfigSingleton config = ConfigSingleton.getInstance().init(this);
        String documentName = config.localAtomServiceDocument();

        Log.i(TAG, "ASD document=" + documentName + " *******");

        try {
            Log.i(TAG, "Getting input stream to document");
            InputStream stream = getAssets().open(documentName);

            Log.i(TAG, "Creating parser");
            AtomServiceDocumentParser parser = new AtomServiceDocumentParser();

            Log.i(TAG, "Parsing document");
            AtomServiceDocument serviceDocument = parser.parse(stream, documentName, "now");

            Log.i(TAG, "Finished parsing OK");

            Log.i(TAG, "Creating GUI");
            setContentView(R.layout.activity_with_list_view);

            Log.d(TAG, "Populating list");

            List<AtomServiceCollection> serviceCollections = serviceDocument.getCollections();
            AtomServiceCollectionListAdapter adapter = new AtomServiceCollectionListAdapter(this, serviceCollections);
            ListView listView = (ListView) findViewById(R.id.esport_list_view);
            listView.setAdapter(adapter);

            getSupportActionBar().setTitle(serviceDocument.getTitle());

            // Put the Atom Service Document into the cache
            ESportsCache.getInstance().put(serviceDocument);

            // Print out the new cache contents
            Log.d(TAG, "New cache contents: " + ESportsCache.getInstance().dump());

        } catch (IOException iox) {
            Log.e(TAG, "Failed to parse document", iox);
        } catch (XmlPullParserException xppx) {
            Log.e(TAG, "Failed to parse document", xppx);
        }
    }

    private class ItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

        }
    }
}
