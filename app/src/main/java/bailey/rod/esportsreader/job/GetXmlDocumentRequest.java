package bailey.rod.esportsreader.job;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import bailey.rod.esportsreader.util.HttpUtils;

/**
 * This Volley request is used to retrieve all XML documents from remote machines. It uses conditional GET queries to
 * do this.
 *
 * TODO: Note that images referenced from news feed entries are NOT retrieved via Volley, and are therefore not
 * subject to caching. It would probably be better if images were cached, as the eSports news feeds seem to often
 * have big images in them.
 */
public class GetXmlDocumentRequest extends StringRequest {

    public GetXmlDocumentRequest(String url, Response.Listener<String> successListener, Response.ErrorListener
            errorListener) {
        super(HttpUtils.mungeURLforCDN(url), successListener, errorListener);

        // Setting true means that Volley should use conditional GETs - if it gets a response of 304 "Not Modified" it
        // will use its internally cached version of the xml document, otherwise it reloads a fresh version.
        // Sometimes useful to set this to false for testing.
        setShouldCache(false);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36);AppVersion/11");
        return headers;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        return super.parseNetworkResponse(response);
    }
}
