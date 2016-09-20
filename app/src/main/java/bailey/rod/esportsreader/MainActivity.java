package bailey.rod.esportsreader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

import bailey.rod.esportsreader.adapter.AtomCollectionEntryListAdapter;
import bailey.rod.esportsreader.adapter.AtomServiceCollectionListAdapter;
import bailey.rod.esportsreader.util.ConfigSingleton;
import bailey.rod.esportsreader.xml.atom.AtomCollectionDocument;
import bailey.rod.esportsreader.xml.atom.AtomCollectionDocumentParser;
import bailey.rod.esportsreader.xml.atom.AtomCollectionEntry;
import bailey.rod.esportsreader.xml.atom.AtomServiceCollection;
import bailey.rod.esportsreader.xml.atom.AtomServiceDocument;
import bailey.rod.esportsreader.xml.atom.AtomServiceDocumentParser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConfigSingleton config = ConfigSingleton.getInstance().init(this);
        String documentName = config.localAtomCollectionDocument();
        Log.i(TAG, "****** collection document=" + documentName + " *******");

        try {
            Log.i(TAG, "Getting input stream to document");
            InputStream stream = getAssets().open(documentName);

            Log.i(TAG, "Creating parser");
            //AtomServiceDocumentParser parser = new AtomServiceDocumentParser();
            AtomCollectionDocumentParser parser = new AtomCollectionDocumentParser();

            Log.i(TAG, "Parsing document");
            //AtomServiceDocument serviceDocument = parser.parse(stream);
            AtomCollectionDocument document = parser.parse(stream);

            Log.i(TAG, "Finished parsing OK");

            setContentView(R.layout.activity_main);

            List<AtomCollectionEntry> entries = document.getEntries();

            Log.i(TAG, "Finished parsing. #entries=" + entries.size());
            AtomCollectionEntryListAdapter adapter = new AtomCollectionEntryListAdapter(this, entries);

            // Initialize the eSports listview
            ListView eSportListView = (ListView) findViewById(R.id.esport_list_view);
            eSportListView.setAdapter(adapter);

            getSupportActionBar().setTitle(document.getTitle());

//            List<AtomServiceCollection> esports = serviceDocument.getCollections();
//
//            Log.i(TAG, "Finished parsing #esports=" + esports.size());
//            AtomServiceCollectionListAdapter adapter = new AtomServiceCollectionListAdapter(this, esports);
//            eSportListView.setAdapter(adapter);
//            adapter.notifyDataSetChanged();

        } catch (IOException iox) {
            Log.e(TAG, "Failed to parse document", iox);
        } catch (XmlPullParserException xppx) {
            Log.e(TAG, "Failed to parse document", xppx);
        } catch (ParseException pex) {
            Log.e(TAG, "Failed to parse document", pex);
        }


    }
}
