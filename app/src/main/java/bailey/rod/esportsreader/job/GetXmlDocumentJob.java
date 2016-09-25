package bailey.rod.esportsreader.job;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
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
     * @return An instance of TimestampedXmlDocument. null means that there has been a failure.
     */
    @Override
    public Object doJob() throws Throwable {
        Log.i(TAG, "*** Into GetXmlDocumentJob.doJob() ***");
        TimestampedXmlDocument resultingDoc = null;

        HttpURLConnection urlConnection = (HttpURLConnection) new URL(documentURL).openConnection();
        setupHttpURLConnection(urlConnection, etag, lastModified);

        resultingDoc = launchRequestHandleResponse(urlConnection);

        // Note that the builtin HttpUrlConnection does not allow redirects/moves across different schemes
        // eg. from http to https. We have one at http://dotaland.net/feed -> https://dotaland.net/feed
        int status = urlConnection.getResponseCode();

        if (status != HttpURLConnection.HTTP_OK) {
            if ((status == HttpURLConnection.HTTP_MOVED_TEMP) ||
                    (status == HttpURLConnection.HTTP_MOVED_PERM) ||
                    (status == HttpURLConnection.HTTP_SEE_OTHER)) {
                Log.d(TAG, "Found a redirect code so we'd better manually start another connection.");

                String newTargetURL = urlConnection.getHeaderField("Location");
                Log.d(TAG, "newTargetURL=" + newTargetURL);

                HttpURLConnection newUrlConnection = (HttpURLConnection) new URL(newTargetURL).openConnection();
                setupHttpURLConnection(newUrlConnection, etag, lastModified);

                resultingDoc = launchRequestHandleResponse(newUrlConnection);
            }
        }

        return resultingDoc;
    }

    private TimestampedXmlDocument launchRequestHandleResponse(HttpURLConnection urlConnection) throws IOException {

        InputStream stream = null;

        try {
            Log.d(TAG, String.format("Requesting document %s with If-None-Match of %s and if-modified-since of %s",
                                     urlConnection.getURL(),
                                     etag, lastModified));

            stream = new BufferedInputStream(urlConnection.getInputStream());
            String resultContent = convertStreamToString(stream);

            Log.d(TAG, String.format("Back from requesting document %s", documentURL));

            String resultEtag = urlConnection.getHeaderField("Etag");
            long resultLastModified = urlConnection.getLastModified();

            Log.d(TAG, String.format("Retrieved document has content \"%s\" with resultEtag of %s and " +
                                             "resultLastModified of %s",
                                     StringUtils.ellipsizeNullSafe(resultContent, 30), resultEtag, DateUtils
                                             .timeSinceEpochToString(resultLastModified)));
            Log.d(TAG, String.format("Response code = %d, response msg=%s", urlConnection.getResponseCode(),
                                     urlConnection.getResponseMessage()));

            stream.close();

            return new TimestampedXmlDocument(resultContent, resultEtag, resultLastModified);
        } finally {
            urlConnection.disconnect();

            if (stream != null) {
                stream.close();
            }
        }
    }

    private void setupHttpURLConnection(HttpURLConnection urlConnection, String etag, long lastModified) throws
            ProtocolException {
        Log.i(TAG, "Into setUpHttURLConnection");

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

        Log.i(TAG, "Exiting setUpHttURLConnection");
    }
}
