package bailey.rod.esportsreader.job;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import bailey.rod.esportsreader.util.DateUtils;
import bailey.rod.esportsreader.util.StringUtils;

/**
 * Created by rodbailey on 24/09/2016.
 */
public class GetXmlDocumentJob implements IJob {

    private static final String TAG = GetXmlDocumentJob.class.getSimpleName();

    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36);AppVersion/11";

    /**
     * Timeouts for connection and read
     */
    private static final int TIMEOUT_MILLIS = 10000;

    private final String documentURL;

    private final String etag;

    private final long lastModified;


    /**
     * @param documentURL The URL from which to retrieve the document with an HTTP GET request.
     * @param etag        The etag timestamp for the copy of the document that we currently have in the
     *                    SessionCache. If we don't have a copy, etag == 0.
     */
    public GetXmlDocumentJob(String documentURL, String etag, long lastModified) {
        this.documentURL = documentURL;
        this.etag = etag;
        this.lastModified = lastModified;
    }

    private String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * @return An instance of TimestampedXmlDocument
     */
    @Override
    public Object doJob() throws Throwable {
        Log.i(TAG, "*** Into GetXmlDocumentJob.doJob() ***");
        String resultContent = null;
        String resultEtag = null;
        long resultLastModified = 0;

        URL url = new URL(documentURL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setInstanceFollowRedirects(true);

        // We do the cacheing ourselves in SessionCache, so turn off Android's own ResponseCache
        urlConnection.setUseCaches(false);

        // For reasons unknown, the feed at feed.esportsreader.com insists on a User-Agent being supplied,
        // otherwise it fails.
        urlConnection.setRequestProperty("User-Agent", USER_AGENT);

        // Version is 1.1, server likes to have it.
        urlConnection.setRequestProperty("v", "11");

        // Server supports conditional GETs, but you must supply the etag that was returned when the
        // document was last returned.
        if (etag != null) {
            urlConnection.setRequestProperty("If-None-Match", etag);
        }

        // Server supports conditional gets, but you must supply an "If-Modified-Since" header
        if (lastModified != 0) {
            urlConnection.setIfModifiedSince(lastModified);
        }

        urlConnection.setRequestProperty("User-Agent", USER_AGENT);

        // Setup timeouts
        urlConnection.setReadTimeout(TIMEOUT_MILLIS);
        urlConnection.setConnectTimeout(TIMEOUT_MILLIS);

        InputStream stream = null;

        try {
            Log.d(TAG, String.format("Requesting document %s with If-None-Match of %s and if-modified-since of %s",
                                     documentURL,
                                     etag, lastModified));

            stream = new BufferedInputStream(urlConnection.getInputStream());
            resultContent = convertStreamToString(stream);

            Log.d(TAG, String.format("Back from requesting document %s", documentURL));

            resultEtag = urlConnection.getHeaderField("Etag");
            resultLastModified = urlConnection.getLastModified();

            Log.d(TAG, String.format("Retrieved document has content \"%s\" with resultEtag of %s and " +
                                             "resultLastModified of %s",
                                     StringUtils.ellipsizeNullSafe(resultContent, 30), resultEtag, DateUtils
                                             .timeSinceEpochToString(resultLastModified)));
            Log.d(TAG, String.format("Response code = %d, response msg=%s", urlConnection.getResponseCode(),
                                     urlConnection.getResponseMessage()));
            stream.close();
        } finally {
            urlConnection.disconnect();

            if (stream != null) {
                stream.close();
            }
        }

        return new TimestampedXmlDocument(resultContent, resultEtag, resultLastModified);
    }
}
