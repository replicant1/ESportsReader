package bailey.rod.esportsreader.net;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.valid4j.Assertive;

/**
 * Contains singleton instance of outgoing Volley request queue for all XML files this app downloads (not images).
 * All requests are tagged with APP_REQUEST_QUEUE_TAG so that they can all be cancelled at once when an Activity stops.
 */
public class VolleySingleton {

    public static final String APP_REQUEST_QUEUE_TAG = "esportsreader.rod.bailey.requestqueue";

    private static final String TAG = VolleySingleton.class.getSimpleName();

    private static final VolleySingleton singleton = new VolleySingleton();

    private boolean initialized = false;

    private RequestQueue requestQueue;

    private VolleySingleton() {
        // Empty
    }

    public synchronized static VolleySingleton getInstance() {
        return singleton;
    }

    public void addRequest(Request request) {
        Assertive.require(isInitialized());
        request.setTag(APP_REQUEST_QUEUE_TAG);
        requestQueue.add(request);
    }

    public void cancelAll() {
        Assertive.require(isInitialized());
        requestQueue.cancelAll(APP_REQUEST_QUEUE_TAG);
    }

    public RequestQueue getRequestQueue() {
        Assertive.require(isInitialized());
        return requestQueue;
    }

    public VolleySingleton init(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        initialized = true;
        Assertive.ensure(isInitialized());
        return singleton;
    }

    public boolean isInitialized() {
        return initialized;
    }


}
