package bailey.rod.esportsreader.job;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rodbailey on 24/09/2016.
 */
public class GetXmlDocumentJob implements IJob {

    private static final String TAG = GetXmlDocumentJob.class.getSimpleName();

    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36);AppVersion/11";

    private final String documentURL;

    private static final int TIMEOUT_MILLIS = 30000;

    private final String lastModified;

    public GetXmlDocumentJob(String documentURL, String lastModified) {
        this.documentURL = documentURL;
        this.lastModified = lastModified;
    }

    private String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Override
    public String doJob() throws Throwable {
        Log.i(TAG, "*** Into GetXmlDocumentJob.doJob() ***");
        String result = null;

        URL url = new URL(documentURL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setInstanceFollowRedirects(true);

        // For reasons unknown, the feed at feed.esportsreader.com insists on a User-Agent being supplied,
        // otherwise it fails.
        urlConnection.setRequestProperty("User-Agent", USER_AGENT);

        // Version is 1.1, server likes to have it.
        urlConnection.setRequestProperty("v", "11");

        // Server supports conditional gets
//        urlConnection.setIfModifiedSince(/*long*/);

        urlConnection.setUseCaches(true);

        urlConnection.setReadTimeout(TIMEOUT_MILLIS);
        urlConnection.setConnectTimeout(TIMEOUT_MILLIS);

        // Add "?v=11". Add  "User-Agent" and "Last-Modified"

        InputStream stream = null;

        try {
            stream = new BufferedInputStream(urlConnection.getInputStream());
            result = convertStreamToString(stream);
            stream.close();
        } finally {
            urlConnection.disconnect();

            if (stream != null) {
                stream.close();
            }
        }

        return result;
    }
}
