package bailey.rod.esportsreader.net;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Contains singleton instance of outgoing request queue for all XML files this app downloads (not images)
 */
public class VolleySingleton {

    public static final String REQUEST_QUEUE_TAG = "esportsreader-request-queue";

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
        request.setTag(REQUEST_QUEUE_TAG);
        requestQueue.add(request);
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public VolleySingleton init(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        initialized = true;
        return singleton;
    }


}
