package bailey.rod.esportsreader.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import java.text.ParseException;

import bailey.rod.esportsreader.R;
import bailey.rod.esportsreader.util.DateUtils;
import bailey.rod.esportsreader.util.StringUtils;

/**
 * A screen containing just a WebView that displays a single new item with an artifical heading + time. Content
 * appears in a scrolling area underneath.
 */
public class ESportFeedEntryActivity extends AppCompatActivity {

    public static final String EXTRA_FEED_ENTRY_CONTENT = "extra-feed-entryu-content";

    public static final String EXTRA_FEED_ENTRY_TITLE = "extra-feed-entry-title";

    public static final String EXTRA_FEED_ENTRY_DATE = "extra-feed-entry-date";

    public static final String EXTRA_WEBSITE_URL = "extra-website-url";

    private static final String TAG = ESportFeedEntryActivity.class.getSimpleName();

    private String websiteUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String content = intent.getStringExtra(EXTRA_FEED_ENTRY_CONTENT);
        String title = intent.getStringExtra(EXTRA_FEED_ENTRY_TITLE);
        String date = intent.getStringExtra(EXTRA_FEED_ENTRY_DATE);
        websiteUrl = intent.getStringExtra(EXTRA_WEBSITE_URL);

        Log.d(TAG, "Received content = " + StringUtils.ellipsizeNullSafe(content, 50));
        Log.d(TAG, "Received title = " + title);
        Log.d(TAG, "Received date = " + date);
        Log.d(TAG, "Received website url = " + websiteUrl);

        setContentView(R.layout.activity_with_web_view);

        String convertedDate = convertDateToAEST(date);

        // Update the WebView to contain the HTML content of the entry

        WebView webView = (WebView) findViewById(R.id.esport_web_view);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(true);
        settings.setUseWideViewPort(false);
        settings.setBlockNetworkImage(true);

        webView.loadData(content, "text/html; charset=utf-8", null);

        TextView titleTextView = (TextView) findViewById(R.id.text1);
        titleTextView.setText(title);

        TextView dateTextView = (TextView) findViewById(R.id.text2);
        dateTextView.setText(convertedDate);

        // Update the action bar
        getSupportActionBar().setTitle("Article");
    }

    private String convertDateToAEST(String dateStr) {
        String result = "";

        try {
            long publishedTime = DateUtils.parseFromTimeSinceEpoch(dateStr);
            result = DateUtils.timeSinceEpochToString(publishedTime);
        }
        catch (ParseException pex) {
            Log.w(TAG, pex);
        }

        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.esport_feed_entry_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_view_website) {
            Log.i(TAG, "Viewing on web site");
            // Startup web browser on this link.
            Uri webpage = Uri.parse(websiteUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
