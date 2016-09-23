package bailey.rod.esportsreader.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import bailey.rod.esportsreader.R;
import bailey.rod.esportsreader.adapter.ESportsFeedEntrySynopsisListAdapter;
import bailey.rod.esportsreader.xml.ESportsFeed;
import bailey.rod.esportsreader.xml.ESportsFeedEntry;
import bailey.rod.esportsreader.xml.rss.AtomFeedParser;

/**
 * Activity presents user with a list of news feeds relating to the same eSport.
 */
public class ESportFeedActivity extends AppCompatActivity {

    private static final String TAG = ESportFeedActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            String documentName= "atom/hearthstone/feeds/Hearthstone.atom";

            Log.i(TAG, "Getting input stream to document");
            InputStream stream = getAssets().open(documentName);

            Log.i(TAG, "Creating parser");
            AtomFeedParser parser = new AtomFeedParser();

            Log.i(TAG, "Parsing document");
            ESportsFeed feed = parser.parse(stream);
            Log.i(TAG, "Finished parsing OK");

            Log.i(TAG, "Creating GUI");
            setContentView(R.layout.activity_with_list_view);

            Log.d(TAG, "Populating list");

            List<ESportsFeedEntry> entries = feed.getEntries();
            ESportsFeedEntrySynopsisListAdapter adapter = new ESportsFeedEntrySynopsisListAdapter(this, entries);
            ListView listView = (ListView) findViewById(R.id.esport_list_view);
            listView.setAdapter(adapter);
        } catch (IOException iox) {
            Log.e(TAG, "Failed to parse document", iox);
        } catch (XmlPullParserException xppx) {
            Log.e(TAG, "Failed to parse document", xppx);
        }

    }
}
