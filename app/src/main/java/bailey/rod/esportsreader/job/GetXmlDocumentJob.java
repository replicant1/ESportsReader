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
     * Timeout after 20 seconds
     */
    private static final int TIMEOUT_MILLIS = 20000;

    private final String documentURL;

    private final String lastModified;

    /**
     * @param documentURL The URL from which to retrieve the document with an HTTP GET request.
     * @param etag        The lastModified timestamp for the copy of the document that we currently have in the
     *                    SessionCache. If we don't have a copy, lastModified == 0.
     */
    public GetXmlDocumentJob(String documentURL, String etag) {
        this.documentURL = documentURL;
        this.lastModified = etag;
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
        String result = null;
        String etag = null;

        URL url = new URL(documentURL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setInstanceFollowRedirects(true);

        // For reasons unknown, the feed at feed.esportsreader.com insists on a User-Agent being supplied,
        // otherwise it fails.
        urlConnection.setRequestProperty("User-Agent", USER_AGENT);

        // Version is 1.1, server likes to have it.
        urlConnection.setRequestProperty("v", "11");

//        urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");

        if (etag != null) {
            urlConnection.setRequestProperty("If-None-Match", etag);
        }

        // Server supports conditional gets
//        urlConnection.setIfModifiedSince(lastModified);

        urlConnection.setReadTimeout(TIMEOUT_MILLIS);
        urlConnection.setConnectTimeout(TIMEOUT_MILLIS);

        InputStream stream = null;

        try {
            Log.d(TAG, String.format("About to request document %s with etag of %s", documentURL, etag));
            stream = new BufferedInputStream(urlConnection.getInputStream());
            result = convertStreamToString(stream);
            etag = urlConnection.getHeaderField("Etag");
            Log.d(TAG, String.format("Raw document is \"%s\" with etag of %s",
                                     StringUtils.ellipsizeNullSafe(result, 30), etag));
            Log.d(TAG, String.format("Response code = %d, response msg=%s", urlConnection.getResponseCode(),
                                     urlConnection.getResponseMessage()));
            stream.close();
        }
        finally {
            Log.d(TAG, "Executing the 'finally' clause of GetXmlDocumentJob.doJob()");
            urlConnection.disconnect();

            if (stream != null) {
                stream.close();
            }
        }

        return new TimestampedXmlDocument(result, etag);
    }
}
