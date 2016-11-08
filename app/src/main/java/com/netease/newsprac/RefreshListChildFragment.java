package com.netease.newsprac;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TimingLogger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.netease.newsprac.WorkingClass.NewsRSSListAdapter;
import com.netease.newsprac.WorkingClass.NewsReaderData;
import com.netease.newsprac.WorkingClass.RSSChannel;
import com.netease.newsprac.WorkingClass.RSSElements;
import com.netease.newsprac.WorkingClass.RSSFeeder;
import com.netease.newsprac.WorkingClass.RSSItem;

import java.io.File;
import java.net.URL;

import static android.R.attr.key;
import static com.netease.newsprac.MainActivity.UNIQUE_FILE_NAME;


public class RefreshListChildFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final String TAG = getClass().getSimpleName();

    private String TEST_URL;
    private RSSChannel rssChannel;
    private LruCache<String, Bitmap> iconImageCache;
    private NewsRSSListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int offSet;



    public RefreshListChildFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.TEST_URL = getArguments().getString(RSSElements.URL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.childfragment_refresh_news, container, false);

        rssChannel = new RSSChannel();
        offSet = 0;

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        iconImageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };

        adapter = new NewsRSSListAdapter(getActivity(), rssChannel.getItems(), iconImageCache);
        ListView listViewOfNews;
        listViewOfNews = (ListView) rootView.findViewById(R.id.list_news);
        listViewOfNews.setAdapter(adapter);
        listViewOfNews.setOnItemClickListener(onItemClickListener);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.childFragment_refresh_news_listview);
        swipeRefreshLayout.setOnRefreshListener(this);


        TimingLogger t = new TimingLogger("aaa", "Test");
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        });
        t.addSplit("1");
        t.dumpToLog();


        return rootView;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);

        offSet += 10;
        new RSSFeeder(rssChannel, offSet, adapter, getContext()).execute(TEST_URL);

        swipeRefreshLayout.setRefreshing(false);
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            //rssChannel.getItem(i).setRead();
            //adapter.notifyDataSetChanged();
            Intent intent = new Intent(getActivity(), NewsDetailsActivity.class);
            intent.putExtra(RSSElements.TITLE, ((RSSItem) adapter.getItem(i)).getTitle());
            intent.putExtra(RSSElements.LINK, ((RSSItem) adapter.getItem(i)).getLink());

            //setRead(((RSSItem) adapter.getItem(i)).getLink());

            startActivity(intent);
        }
    };

    private void setRead(String key) {
        ContentResolver contentResolver = getContext().getContentResolver();
        String[] columns = {NewsReaderData.News.URL_LINK};
        String selection = NewsReaderData.News.URL_LINK + "=?";
        String[] args = {key};
        Cursor cursor = contentResolver.query(NewsReaderData.News.CONTENT_URI, columns,
                selection, args, null);
        if (cursor != null) {
            ContentValues cv = new ContentValues();
            cv.put(NewsReaderData.News.URL_LINK, key);
            cv.put(NewsReaderData.News.IS_READ, true);
            int newID = contentResolver.update(NewsReaderData.News.CONTENT_URI, cv, selection, args);
            adapter.notifyDataSetChanged();
            Log.v(TAG, "The new id for a read news is " + newID);
            cursor.close();
        }
    }

}
