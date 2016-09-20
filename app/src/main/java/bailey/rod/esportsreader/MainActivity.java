package bailey.rod.esportsreader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import bailey.rod.esportsreader.adapter.AtomServiceCollectionListAdapter;
import bailey.rod.esportsreader.util.ConfigSingleton;
import bailey.rod.esportsreader.xml.atom.AtomServiceCollection;
import bailey.rod.esportsreader.xml.atom.AtomServiceDocument;
import bailey.rod.esportsreader.xml.atom.AtomServiceDocumentParser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConfigSingleton config = ConfigSingleton.getInstance().init(this);
        Log.i(TAG, "****** service document=" + config.localAtomServiceDocument() + " *******");

        try {
            Log.i(TAG, "Getting input stream to document");
            InputStream stream = getAssets().open(config.localAtomServiceDocument());

            Log.i(TAG, "Creating parser");
            AtomServiceDocumentParser parser = new AtomServiceDocumentParser();

            Log.i(TAG, "Parsing service document");
            AtomServiceDocument serviceDocument = parser
                    .parse(stream);

            Log.i(TAG, "Finished parsing OK");

            setContentView(R.layout.activity_main);

            // Initialize the eSports listview
            ListView eSportListView = (ListView) findViewById(R.id.esport_list_view);
            List<AtomServiceCollection> esports = serviceDocument.getCollections();

            Log.i(TAG, "Finished parsing #esports=" + esports.size());
            AtomServiceCollectionListAdapter adapter = new AtomServiceCollectionListAdapter(this, esports);
            eSportListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } catch (IOException iox) {
            Log.e(TAG, "Failed to parse atom service document", iox);
        } catch (XmlPullParserException xppx) {
            Log.e(TAG, "Failed to parse atom service document", xppx);
        }


    }
}
