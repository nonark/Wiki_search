package search.android.wiki_search;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import search.android.customview.StatusToolbar;
import search.android.information.PageInformation;
import search.android.tools.WikiPageFinder;
import search.android.vo.SummaryPage;

/**
 * Created by nhnent on 2017. 5. 4..
 */

public class DetailActivity extends AppCompatActivity {

    private static String TAG = DetailActivity.class.getName();

    private StatusToolbar statusToolbar;
    private RecyclerView wikiPagesView;
    private SummaryPageAdapter adapter;
    private boolean searchPage;


    public static Intent moveDetailPage(Context context, String title) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("Search", title);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        wikiPagesView = (RecyclerView) findViewById(R.id.wikiPages);

        List<SummaryPage> wikiPages = new ArrayList<>();
        adapter = new SummaryPageAdapter(null, wikiPages, R.layout.search_item_header ,R.layout.search_item);

        //연관검색어를 클릭할 경우 해당 검색어와 연관된 페이지로 이동
        adapter.setRelatedListner(new SummaryPageAdapter.OnRecyclerViewItemClickedListener() {
            @Override
            public void onItemClicked(String searchText) {
                Intent intent = moveDetailPage(getBaseContext(), searchText);
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

        wikiPagesView.setAdapter(adapter);
        wikiPagesView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        statusToolbar = (StatusToolbar) findViewById(R.id.statusToolbar);
        // 뒤로가기 버튼 클릭시 finish 호출
        statusToolbar.setOnBackButtonClickedListener(new StatusToolbar.OnStatusBarClickedListener() {
            @Override
            public void onStatusButtonClicked() {
                setResult(PageInformation.OK);
                finish();
            }
        });

        // X 버튼을 누르면 현재까지 생성한 모든 Activity를 제거하고 MainActivity를 상단에 출력
        statusToolbar.setOnCloseButtonClickedListener(new StatusToolbar.OnStatusBarClickedListener() {
            @Override
            public void onStatusButtonClicked() {
                Intent intent = MainActivity.moveMainPage(getBaseContext());
                startActivity(intent);
            }
        });

        //Intent 내의 값이 null이 아니면 주어진 검색어에 해당하는 리스트 출력
        Intent intent = getIntent();
        if (intent != null) {
            final String searchText = intent.getStringExtra("Search");
            searchPage = true;

            statusToolbar.setTitle(searchText);

            final ProgressDialog dialog = new ProgressDialog(DetailActivity.this);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    SearchApplication.getRequestQueue().cancelAll(TAG);
                    if(searchPage) {
                        setResult(PageInformation.REQUEST_CANCLE);
                        finish();
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
                                setResult(PageInformation.TIME_OUT);
                            } else {
                                NetworkResponse response = error.networkResponse;
                                if(response.statusCode == 404) {
                                    setResult(PageInformation.NO_SEARCH_RESULT);
                                } else {
                                    setResult(PageInformation.UNKOWN_ERROR);
                                }
                            }
                            finish();
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
                        setResult(PageInformation.TIME_OUT);
                    } else {
                        NetworkResponse response = error.networkResponse;
                        if(response.statusCode == 404) {
                            setResult(PageInformation.NO_SEARCH_RESULT);
                        } else {
                            setResult(PageInformation.UNKOWN_ERROR);
                        }
                    }
                    finish();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != PageInformation.OK) {
            Toast.makeText(getBaseContext(), PageInformation.statusMessage(resultCode), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_hold, R.anim.right_slide);
    }
}
