package bailey.rod.esportsreader.net;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import bailey.rod.esportsreader.util.HttpUtils;

/**
 * This Volley request is used to retrieve all XML documents from remote machines. It uses conditional GET queries to
 * do this.
 */
public class GetXmlDocumentRequest extends StringRequest {

    public GetXmlDocumentRequest(String url, Response.Listener<String> successListener, Response.ErrorListener
            errorListener) {
        super(HttpUtils.mungeURLforCDN(url), successListener, errorListener);
        setShouldCache(false);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36);AppVersion/11");
        return headers;
    }

}
