package bailey.rod.esportsreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import bailey.rod.esportsreader.R;
import bailey.rod.esportsreader.xml.ESportsFeed;
import bailey.rod.esportsreader.xml.ESportsFeedEntry;
import bailey.rod.esportsreader.xml.rss.AtomFeedParser;

/**
 * Created by rodbailey on 23/09/2016.
 */
public class ESportFeedEntryActivity extends AppCompatActivity {

    public static final String EXTRA_HTML_CONTENT_URL = "extra-html-content-url";

    private static final String TAG = ESportFeedEntryActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String contentHref = intent.getStringExtra(EXTRA_HTML_CONTENT_URL);

        String documentName = "atom/hearthstone/feeds/Hearthstone.atom";

        Log.d(TAG, "Received item URL " + contentHref + ". Overwriting with " + documentName);

        try {
            Log.i(TAG, "Getting input stream to document");
            InputStream stream = getAssets().open(documentName);

            Log.i(TAG, "Creating parser");
            AtomFeedParser parser = new AtomFeedParser();

            Log.i(TAG, "Parsing document");
            ESportsFeed feed = parser.parse(stream, documentName, "now");
            Log.i(TAG, "Finished parsing OK");

            Log.i(TAG, "Creating GUI");
            setContentView(R.layout.activity_with_web_view);

            Log.d(TAG, "Populating web view");

            ESportsFeedEntry entry = feed.getEntries().get(0);
            WebView webView = (WebView) findViewById(R.id.esport_web_view);

            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setBuiltInZoomControls(true);
            settings.setDisplayZoomControls(true);
            settings.setUseWideViewPort(false);
            settings.setBlockNetworkImage(true);

            webView.loadData(entry.getContent(), "text/html; charset=utf-8", null);

            TextView titleTextView = (TextView) findViewById(R.id.text1);
            titleTextView.setText(entry.getTitle());

            TextView dateTextView = (TextView) findViewById(R.id.text2);
            dateTextView.setText(entry.getPublished());

        } catch (IOException iox) {
            Log.e(TAG, "Failed to parse document", iox);
        } catch (XmlPullParserException xppx) {
            Log.e(TAG, "Failed to parse document", xppx);
        }
    }
}
