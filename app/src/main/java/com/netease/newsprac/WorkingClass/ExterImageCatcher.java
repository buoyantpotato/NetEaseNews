package com.netease.newsprac.WorkingClass;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.renderscript.ScriptGroup;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.util.TimingLogger;
import android.widget.ImageView;

import com.netease.newsprac.MainActivity;
import com.netease.newsprac.RefreshListChildFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.attr.bitmap;
import static android.R.attr.data;
import static android.R.attr.key;
import static android.R.attr.maxHeight;
import static android.R.attr.maxWidth;

/**
 * Created by linzhou on 16-8-3.
 */

public class ExterImageCatcher extends AsyncTask<RSSItem, Void, Bitmap> {

    private final String TAG = getClass().getSimpleName();
    private static final int maxHeight = 120;
    private static final int maxWidth = 120;

    private DiskLruImageCache diskLruCache;
    private LruCache<String, Bitmap> memoryCache;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10;
    //private ImageView imageView;
    //TimingLogger timings = new TimingLogger(TAG, "Load from External");

    private returnOutput delegate;
    public interface returnOutput {
        void processOutput(Bitmap bitmap);
    }


    public ExterImageCatcher(File cacheFile, LruCache<String, Bitmap> memoryCache,
                             returnOutput delegate) throws IOException {
        diskLruCache = new DiskLruImageCache(cacheFile,
                DISK_CACHE_SIZE, Bitmap.CompressFormat.PNG, 100);
        this.delegate = delegate;
        this.memoryCache = memoryCache;
    }

    @Override
    protected Bitmap doInBackground(RSSItem... items) {
        Log.v(TAG, "1");
        String imgURL = loadImgURL(items[0]);

        if (imgURL != null) {
            Bitmap imageFound = fetchImageFromDisk(imgURL);
            if (imageFound == null) {
                imageFound = fetchImageFromNetwork(imgURL);
                Log.v(TAG, "3");
            }

            addImageToDiskCache(imgURL, imageFound);
            Log.v(TAG, "4");

            return imageFound;
        }
        else return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        //imageView.setImageBitmap(bitmap);
        this.delegate.processOutput(bitmap);
    }

    private Bitmap fetchImageFromDisk(String key) {
        if (diskLruCache != null) {
            return diskLruCache.getBitmap(key);
        }

        return null;
    }

    private Bitmap fetchImageFromNetwork(String key) {
        try {
            URL url = new URL(key);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream is = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            if (bitmap != null && (bitmap.getHeight() > maxHeight
                    || bitmap.getWidth() > maxWidth)) {
                bitmap = resizeBitmap(bitmap, maxHeight, maxWidth);
            }
            return bitmap;
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    private Bitmap resizeBitmap(Bitmap img, int reqHeight, int reqWidth) {
        float ratio = Math.min(
                reqHeight / img.getHeight(),
                reqWidth / img.getWidth());

        int newHeight = Math.round(img.getHeight() * ratio);
        int newWidth = Math.round(img.getWidth() * ratio);

        return Bitmap.createScaledBitmap(img, newWidth, newHeight, false);
    }

    private void addImageToDiskCache(String key, Bitmap bitmap) {

        if (memoryCache.get(key) == null) {
            memoryCache.put(key, bitmap);
        }

        if (diskLruCache != null && diskLruCache.getBitmap(key) == null) {
            diskLruCache.put(key, bitmap);
        }
    }

    private String loadImgURL(RSSItem item) {
        try {
            URL url = new URL(item.getLink());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream httpInputStream = connection.getInputStream();
            BufferedReader httpBufferedReader =
                    new BufferedReader(new InputStreamReader(httpInputStream));

            //Pattern imgPattern = Pattern.compile("<img.*?/>");
            Pattern srcPattern = Pattern.compile(
                    "<img.*src=([\"|'])(.*?)\\1.*>", Pattern.CASE_INSENSITIVE);
            final String iconImgPre = "http://static.cnbetacdn.com/topics"; /* Can be fitted into a List*/
            String line;

                /* Screen html files line by line till finding the url for icon images */
            while ((line = httpBufferedReader.readLine()) != null) {
                Matcher srcMatcher = srcPattern.matcher(line);
                if (srcMatcher.find()) {
                    String path = srcMatcher.group(2);
                    if (path.contains(iconImgPre)) {
                        item.setIconImgURL(path);
                        return path;
                    }
                }
            }
            return null;
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }
}
