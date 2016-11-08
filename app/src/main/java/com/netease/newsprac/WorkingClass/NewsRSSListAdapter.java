package com.netease.newsprac.WorkingClass;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.util.TimingLogger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.newsprac.MainActivity;
import com.netease.newsprac.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.security.AccessController.getContext;

/**
 * Created by linzhou on 16-7-27.
 */

public class NewsRSSListAdapter extends BaseAdapter {

    private final String TAG = getClass().getSimpleName();

    private Activity activity;
    private List<RSSItem> newsList;
    private LruCache<String, Bitmap> memoryCache;
    private File diskCacheFile;


    public NewsRSSListAdapter(Activity activity, List<RSSItem> list,
                              LruCache<String, Bitmap> imgCache) {
        this.activity = activity;
        this.newsList = list;
        this.memoryCache = imgCache;
        diskCacheFile = MainActivity.getDiskCacheDir(
            activity.getApplicationContext(), MainActivity.UNIQUE_FILE_NAME);
    }

    @Override
    public int getCount() {
        return this.newsList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.newsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_news, null);
        }

        /* Wait for updating by viewGroup*/
        TextView titleView = (TextView) view.findViewById(R.id.list_item_news_maintitle);
        TextView subInfoView = (TextView) view.findViewById(R.id.list_item_news_subinfo);
        ImageView iconImageView = (ImageView) view.findViewById(R.id.list_item_news_image);
        ImageView checkReaadImage = (ImageView) view.findViewById(R.id.list_item_checkRead);

        RSSItem item = newsList.get(i);
        String title = item.getTitle();
        String pubDate = item.getPubDate();
        String iconImgURL = item.getIconImgURL();

        if (title != null) {
            titleView.setText(title);
        }

        if (pubDate != null) {
            subInfoView.setText(pubDate);
        }

        fetchImage(item, iconImageView);


        /*if (isThisItemReaad(newsList.get(i).getLink())) {
            checkReaadImage.setBackgroundResource(0);
        }*/

        return view;
    }

    private void fetchImage(RSSItem item, final ImageView imageView) {

        final String key = item.getIconImgURL();

        if (key == null) {
            fetchExternal(item, imageView);
            return;
        }

        else {
            Bitmap imgFromMemory = memoryCache.get(key);
            if (imgFromMemory != null) {
                imageView.setImageBitmap(imgFromMemory);
            }
            else {
                fetchExternal(item, imageView);
            }
        }
    }

    private void fetchExternal(final RSSItem item, final ImageView imageView) {

        imageView.setImageResource(R.drawable.news_iconimage_placeholder);

            /* The module to load images from disk first, if not found, from network then */
        try {
            new ExterImageCatcher(diskCacheFile, memoryCache, new ExterImageCatcher.returnOutput() {

                @Override
                public void processOutput(Bitmap imgFromDiskOrWeb) {
                    if (imgFromDiskOrWeb != null) {
                        imageView.setImageBitmap(imgFromDiskOrWeb);
                        memoryCache.put(item.getIconImgURL(), imgFromDiskOrWeb);
                    }
                }

            }).execute(item);

        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private boolean isThisItemReaad(String key) {
        ContentResolver contentResolver = activity.getContentResolver();

        String[] columns = {NewsReaderData.News.URL_LINK};
        String selection = NewsReaderData.News.URL_LINK + "=?";
        String[] args = {key};
        Cursor cursor = contentResolver.query(NewsReaderData.News.CONTENT_URI, columns,
                selection, args, null);
        if (cursor != null) {
            String isRead = cursor.getString(cursor.getColumnIndex(NewsReaderData.News.IS_READ));
            Log.v(TAG, "isRead: " + isRead);
        }
        return true;
    }
}
