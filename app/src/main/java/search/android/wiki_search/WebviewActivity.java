package search.android.wiki_search;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import search.android.customview.StatusToolbar;
import search.android.tools.WikiPageFinder;

/**
 * Created by nhnent on 2017. 5. 4..
 */

public class WebviewActivity extends AppCompatActivity {

    private StatusToolbar statusToolbar;
    private WebView webView;

    public static Intent moveWebviewPage(Context context, String title) {
        Intent intent = new Intent(context, WebviewActivity.class);
        intent.putExtra("Search", title);
        return intent;
    }

    public static Intent shareWikiPage(String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Wikipedia_URL");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.setType("text/plain");
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_page);

        statusToolbar = (StatusToolbar) findViewById(R.id.statusToolbar);

        // 뒤로가기 버튼 클릭시 finish 호출
        statusToolbar.setOnBackButtonClickedListener(new StatusToolbar.OnStatusBarClickedListener() {
            @Override
            public void onStatusButtonClicked() {
                setResult(0);
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

        //상단의 제목 클릭시 URL 공유
        statusToolbar.setOnTitleClickedListener(new StatusToolbar.OnStatusBarClickedListener() {
            @Override
            public void onStatusButtonClicked() {
                Intent intent = shareWikiPage(new WikiPageFinder().getHtmlUrl(statusToolbar.getTitle()));
                startActivity(Intent.createChooser(intent, "Share"));
            }
        });

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

                switch(errorCode) {
                    case ERROR_AUTHENTICATION:                // 서버에서 사용자 인증 실패
                    case ERROR_BAD_URL:                            // 잘못된 URL
                    case ERROR_CONNECT:                           // 서버로 연결 실패
                    case ERROR_FAILED_SSL_HANDSHAKE:     // SSL handshake 수행 실패
                    case ERROR_FILE:                                   // 일반 파일 오류
                    case ERROR_FILE_NOT_FOUND:                // 파일을 찾을 수 없습니다
                    case ERROR_HOST_LOOKUP:            // 서버 또는 프록시 호스트 이름 조회 실패
                    case ERROR_IO:                               // 서버에서 읽거나 서버로 쓰기 실패
                    case ERROR_PROXY_AUTHENTICATION:    // 프록시에서 사용자 인증 실패
                    case ERROR_REDIRECT_LOOP:                // 너무 많은 리디렉션
                    case ERROR_TOO_MANY_REQUESTS:      // 페이지 로드중 너무 많은 요청 발생
                    case ERROR_UNKNOWN:                         // 일반 오류
                    case ERROR_UNSUPPORTED_AUTH_SCHEME:  // 지원되지 않는 인증 체계
                    case ERROR_UNSUPPORTED_SCHEME:           // URI가 지원되지 않는 방식
                        setResult(1);
                        break;

                    case ERROR_TIMEOUT:                           // 연결 시간 초과
                        setResult(1);
                        break;

                    default :
                        setResult(1);
                        break;
                }
                finish();
            }
        });

        //인터넷 연결 확인 - PERMISSION(ACCESS_NETWORK_STATE) 필요
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getActiveNetworkInfo();
        if(mobile == null) {
            setResult(1);
            finish();
        }

        //Intent 내의 값이 null이 아니면 주어진 URL에 해당하는 웹 표시
        Intent intent = getIntent();
        if (intent != null) {
            String searchText = intent.getStringExtra("Search");
            statusToolbar.setTitle(searchText);

            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl(new WikiPageFinder().getHtmlUrl(searchText));
        } else {
            setResult(1);
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_hold, R.anim.right_slide);
    }
}
