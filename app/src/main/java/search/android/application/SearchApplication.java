package search.android.application;

import android.app.Application;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import search.android.wiki_search.R;

/**
 * Created by nhnent on 2017. 5. 2..
 */

public class SearchApplication extends Application {

    private static RequestQueue requestQueue;

    private static ImageLoader imageLoader;
    private static DisplayImageOptions options;
    private static ImageLoaderConfiguration config;
    private static ImageLoadingListener listener;

    @Override
    public void onCreate() {
        super.onCreate();

        // Volley
        Cache cache = new NoCache();
        //Cache cache = new DiskBasedCache(this.getCacheDir(), 10 * 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();


        // Universial ImageLoader
        config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs()
                .build();

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher_round) // 로딩중에 나타나는 이미지
                .showImageForEmptyUri(R.drawable.ic_close) // 값이 없을때
                .showImageOnFail(R.drawable.btn_close_gray) // 에러 났을때 나타나는 이미지
                .cacheInMemory(true)
                .considerExifParams(true)
                .build();

        listener = new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                ImageView imageView = (ImageView) view;
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        };

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public static void displayImage(String url, ImageView imageView) {
        imageLoader.displayImage(url, imageView, options, listener);
    }

    public static void cancelImageLoader(ImageView imageView) {
        imageLoader.cancelDisplayTask(imageView);
    }
}
