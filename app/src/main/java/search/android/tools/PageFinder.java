package search.android.tools;

import com.android.volley.Response;

import org.json.JSONObject;

/**
 * Created by nhnent on 2017. 5. 2..
 */

public interface PageFinder {
    void findRelatedPages(String tag, String title, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener);
    void findSummaryPage(String tag, String title, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener);
    String getHtmlUrl(String title);
}