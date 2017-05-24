package search.android.tools;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import search.android.application.SearchApplication;

/**
 * Created by nhnent on 2017. 5. 2..
 */

public class WikiPageFinder implements PageFinder {

    private class JsonRequest extends JsonObjectRequest {

        public JsonRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
        }

        public JsonRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(url, jsonRequest, listener, errorListener);
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json; charset=utf-8");
            return headers;
        }

        @Override
        public RetryPolicy getRetryPolicy() {
            return super.getRetryPolicy();
        }
    }

    private static String htmlUrl = "https://en.wikipedia.org/api/rest_v1/page/html/";
    private static String summaryUrl = "https://en.wikipedia.org/api/rest_v1/page/summary/";
    private static String relatedUrl = "https://en.wikipedia.org/api/rest_v1/page/related/";

    public void cancelPageFinder(String tag) {
        SearchApplication.getRequestQueue().cancelAll(tag);
    }

    public void findSummaryPage(String tag, String title, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {

        JsonRequest jsonRequest = new JsonRequest(Request.Method.GET, summaryUrl + title, new JSONObject(), responseListener, errorListener);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 1, 1.0f));
        jsonRequest.setTag(tag);

        SearchApplication.getRequestQueue().add(jsonRequest);
    }

    public void findRelatedPages(String tag, String title, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        JsonRequest jsonRequest = new JsonRequest(Request.Method.GET, relatedUrl + title, new JSONObject(), responseListener, errorListener);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 1, 1.0f));
        jsonRequest.setTag(tag);

        SearchApplication.getRequestQueue().add(jsonRequest);
    }

    public String getHtmlUrl(String title) {
        return htmlUrl + title;
    }
}
