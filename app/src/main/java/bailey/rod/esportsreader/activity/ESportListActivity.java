package bailey.rod.esportsreader.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
        String documentHref;
        AtomServiceDocument serviceDocument;
        ESportsCache cache = ESportsCache.getInstance();

        if (config.loadFromLocalAtomFiles()) {
            documentHref = config.localAtomServiceDocument();
        } else {
            documentHref = config.atomServiceDocument();
        }

        try {
            Log.i(TAG, String.format("Atom Service Document at %s required to display eSport list", documentHref));

            if (cache.contains(documentHref)) {
                Log.d(TAG, "Retrieving ASD from cache");
                serviceDocument = (AtomServiceDocument) cache.get(documentHref);
            } else {
                Log.d(TAG, "Retrieving ASD from non-cache source and adding to cache");
                InputStream stream = getAssets().open(documentHref);
                AtomServiceDocumentParser parser = new AtomServiceDocumentParser();
                serviceDocument = parser.parse(stream, documentHref, "now");
                cache.put(serviceDocument);
            }

            Log.i(TAG, "Creating GUI");
            setContentView(R.layout.activity_with_list_view);

            Log.d(TAG, "Populating list");
            List<AtomServiceCollection> serviceCollections = serviceDocument.getCollections();

            AtomServiceCollectionListAdapter adapter = new AtomServiceCollectionListAdapter(this, serviceCollections);
            ListView listView = (ListView) findViewById(R.id.esport_list_view);
            listView.setAdapter(adapter);

            getSupportActionBar().setTitle(serviceDocument.getTitle());
        } catch (IOException iox) {
            Log.e(TAG, "Failed to parse document", iox);
        } catch (XmlPullParserException xppx) {
            Log.e(TAG, "Failed to parse document", xppx);
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

            // TODO: How to restart the current activity?

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
