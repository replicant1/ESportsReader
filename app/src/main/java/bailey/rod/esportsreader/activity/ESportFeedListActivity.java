package bailey.rod.esportsreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

import bailey.rod.esportsreader.R;
import bailey.rod.esportsreader.adapter.AtomCollectionEntryListAdapter;
import bailey.rod.esportsreader.cache.ESportsCache;
import bailey.rod.esportsreader.util.ConfigSingleton;
import bailey.rod.esportsreader.xml.atom.AtomCollectionDocument;
import bailey.rod.esportsreader.xml.atom.AtomCollectionDocumentParser;
import bailey.rod.esportsreader.xml.atom.AtomCollectionEntry;

/**
 * Activity presents user with a list of feeds that cater to the same eSport.
 */
public class ESportFeedListActivity extends AppCompatActivity {

    public static final String EXTRA_ATOM_COLLECTION_DOCUMENT_HREF = "atom-collection-document-href";

    private static final String TAG = ESportFeedListActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String acdRef = intent.getStringExtra(EXTRA_ATOM_COLLECTION_DOCUMENT_HREF);
        String documentRef;

        ConfigSingleton config = ConfigSingleton.getInstance();

        if (config.loadFromLocalAtomFiles()) {
            documentRef = config.localAtomCollectionDocument();
        }
        else {
            documentRef = acdRef;
        }

        Log.d(TAG, "Received acdRef " + acdRef + ". Overwriting with " + documentRef);

        Log.i(TAG, "Into ESportFeedListActivity.onCreate with collection document=" + documentRef + " *******");

        try {
            Log.i(TAG, "Getting input stream to document");
            InputStream stream = getAssets().open(documentRef);

            Log.i(TAG, "Creating parser");
            AtomCollectionDocumentParser parser = new AtomCollectionDocumentParser();

            Log.i(TAG, "Parsing document");
            AtomCollectionDocument collectionDocument = parser.parse(stream, documentRef, "now");

            Log.i(TAG, "Finished parsing OK");

            Log.i(TAG, "Creating GUI");
            setContentView(R.layout.activity_with_list_view);

            Log.d(TAG, "Populating list");

            getSupportActionBar().setTitle(collectionDocument.getTitle());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            List<AtomCollectionEntry> entries = collectionDocument.getEntries();
            AtomCollectionEntryListAdapter adapter = new AtomCollectionEntryListAdapter(this, entries);
            ListView listView = (ListView) findViewById(R.id.esport_list_view);
            listView.setAdapter(adapter);

            // Put the Atom Collection Document into the cache
            ESportsCache.getInstance().put(collectionDocument);
            Log.d(TAG, "New cache contents: " + ESportsCache.getInstance().dump());

        } catch (IOException iox) {
            Log.e(TAG, "Failed to parse document", iox);
        } catch (XmlPullParserException xppx) {
            Log.e(TAG, "Failed to parse document", xppx);
        } catch (ParseException pex) {
            Log.e(TAG, "Failed to parse document", pex);
        }
    }
}
