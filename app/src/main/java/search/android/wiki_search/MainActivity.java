package search.android.wiki_search;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import search.android.adapter.SummaryPageAdapter;
import search.android.application.SearchApplication;
import search.android.customview.SearchToolbar;
import search.android.information.PageInformation;
import search.android.tools.WikiPageFinder;
import search.android.vo.SummaryPage;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getName();
    private SearchToolbar searchToolbar;
    private RecyclerView wikiPagesView;
    private SummaryPageAdapter adapter;
    private LinearLayout rootView;
    private boolean searchPage;

    public static Intent moveMainPage(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //이전의 Activity를 제거
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); //MainActivity가 새로 생기는 것을 방지
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wikiPagesView = (RecyclerView) findViewById(R.id.wikiPages);
        adapter = new SummaryPageAdapter(null, new ArrayList<SummaryPage>(), R.layout.search_item_header, R.layout.search_item);

        //연관검색어를 클릭할 경우 해당 검색어와 연관된 페이지로 이동
        adapter.setRelatedListner(new SummaryPageAdapter.OnRecyclerViewItemClickedListener() {
            @Override
            public void onItemClicked(String searchText) {
                Intent intent = DetailActivity.moveDetailPage(getBaseContext(), searchText);
                startActivityForResult(intent, PageInformation.OK);
                overridePendingTransition(R.anim.anim_hold, R.anim.left_slide);
            }
        });

        //헤더를 클릭할 경우 Webview 페이지로 이동
        adapter.setHeaderItemClickedLListner(new SummaryPageAdapter.OnRecyclerViewItemClickedListener() {
            @Override
            public void onItemClicked(String searchText) {
                Intent intent = WebviewActivity.moveWebviewPage(getBaseContext(), searchText);
                startActivityForResult(intent, PageInformation.OK);
                overridePendingTransition(R.anim.anim_hold, R.anim.left_slide);
            }
        });

        //adapter Event
        wikiPagesView.setAdapter(adapter);
        wikiPagesView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        searchToolbar = (SearchToolbar) findViewById(R.id.searchToolbar);
        searchToolbar.setOnSearchBarClickedListener(new SearchToolbar.OnSearchBarClickedListener() {
            @Override
            public void onSearchButtonClicked(String searchText) {
                searchPage = true;

                final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        SearchApplication.getRequestQueue().cancelAll(TAG);
                        if(searchPage) {
                            toastMessage(PageInformation.statusMessage(PageInformation.REQUEST_CANCLE));
                        }
                    }
                });
                dialog.setMessage("Search " + searchText + " page (1/2)");
                dialog.show();

                new WikiPageFinder().findSummaryPage(TAG, searchText, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        final SummaryPage headerPage = gson.fromJson(response.toString(), SummaryPage.class);
                        dialog.setMessage("Search related page (2/2)");

                        new WikiPageFinder().findRelatedPages(TAG, headerPage.getTitle(), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Gson gson = new Gson();

                                try {
                                    SummaryPage[] summaryPages = gson.fromJson(response.get("pages").toString(), SummaryPage[].class);

                                    if (summaryPages != null) {
                                        List<SummaryPage> list = new ArrayList<SummaryPage>();

                                        for (SummaryPage summaryPage : summaryPages) {
                                            summaryPage.setTitle(summaryPage.getTitle().replaceAll("_"," "));
                                            list.add(summaryPage);
                                        }

                                        adapter.setHeaderPage(headerPage);

                                        adapter.setRelatedPages(list);
                                        adapter.notifyDataSetChanged();
                                    }
                                } catch (JSONException e) {

                                }
                                searchPage = false;
                                dialog.dismiss();

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                searchPage = false;
                                dialog.dismiss();

                                if (error instanceof TimeoutError) {
                                    toastMessage(PageInformation.statusMessage(PageInformation.TIME_OUT));
                                } else {
                                    NetworkResponse response = error.networkResponse;
                                    if(response.statusCode == 404) {
                                        toastMessage(PageInformation.statusMessage(PageInformation.NO_SEARCH_RESULT));
                                    } else {
                                        toastMessage(PageInformation.statusMessage(PageInformation.UNKOWN_ERROR));
                                    }
                                }
                            }
                        });
                        //
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        searchPage = false;
                        dialog.dismiss();

                        if (error instanceof TimeoutError) {
                            toastMessage(PageInformation.statusMessage(PageInformation.TIME_OUT));
                        } else {
                            NetworkResponse response = error.networkResponse;
                            if(response.statusCode == 404) {
                                toastMessage(PageInformation.statusMessage(PageInformation.NO_SEARCH_RESULT));
                            } else {
                                toastMessage(PageInformation.statusMessage(PageInformation.UNKOWN_ERROR));
                            }
                        }
                    }
                });
            }
        });


        rootView = (LinearLayout) findViewById(R.id.rootView);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchToolbar.requestFocus();
            }
        });
        rootView.performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != PageInformation.OK) {
            Toast.makeText(getBaseContext(), PageInformation.statusMessage(resultCode), Toast.LENGTH_SHORT).show();
        }
    }

    private void toastMessage(String text) {
        Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG).show();
    }
}
