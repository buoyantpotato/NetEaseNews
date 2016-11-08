package com.netease.newsprac.WorkingClass;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;

import com.netease.newsprac.RefreshListChildFragment;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static java.net.Proxy.Type.HTTP;

/**
 * Created by linzhou on 16-7-26.
 */

public class RSSFeeder extends AsyncTask<String, List<RSSItem>, Void> {

    private final String TAG = getClass().getSimpleName();
    private final int INCREMENT_EACH_SWIPE = 10;

    private final int maxHeight = 120;
    private final int maxWidth = 120;

    private RSSChannel rssChannel;
    private int offSet;
    private NewsRSSListAdapter adapter;
    private ContentResolver contentResolver;

    public RSSFeeder(RSSChannel channel, int offset, NewsRSSListAdapter adapter, Context viewContext) {
        this.rssChannel = channel;
        this.offSet = offset;
        this.adapter = adapter;
        this.contentResolver = viewContext.getContentResolver();
    }

    public int getCount() {
        return this.rssChannel.getCount();
    }

    @Override
    protected Void doInBackground(String... urls) {
        // analyse the XML and publish the title text info to UI thread
        processXML(getXML(urls[0]));
        return null;
    }

    @Override
    protected void onProgressUpdate(List<RSSItem>... values) {
        super.onProgressUpdate(values);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPostExecute(Void what) {
        super.onPostExecute(what);
    }

    private Document getXML(String url) {
        try {
            InputStream xmlInputStream = getURLContent(url);
            DocumentBuilder documentBuilder = DocumentBuilderFactory
                    .newInstance().newDocumentBuilder();
            return documentBuilder.parse(xmlInputStream);
        }
        catch (Exception e) {
            Log.e(TAG, "getXML");
            return null;
        }
    }

    private void processXML(Document xml) {
        if (xml != null) {
            Element root = xml.getDocumentElement();
            Node rootNode = root.getChildNodes().item(1); // The rootNode represents the channel
                                                        // The "1" indicates the <channel> line
            NodeList rootNodeList = rootNode.getChildNodes();
            updateChannel(rootNodeList);
            publishProgress(rssChannel.getItems());
        }
        else {
            Log.e(TAG, "The XML document file is empty.");
        }
    }

    private RSSItem buildItem(NodeList nodeList) {
        RSSItem newItem = new RSSItem();
        for (int j = 0; j < nodeList.getLength(); j++) {
            String twigNodeName = nodeList.item(j).getNodeName().toLowerCase();
            String twigNodeContent = nodeList.item(j).getTextContent();
            switch (twigNodeName) {
                case RSSElements.TITLE:
                    newItem.setTitle(twigNodeContent);
                    break;
                case RSSElements.LINK:
                    newItem.setLink(twigNodeContent);
                    break;
                case RSSElements.PUBDATE:
                    newItem.setPubDate(twigNodeContent);
                    break;
                case RSSElements.DESCRIPTION:
                    newItem.setDescription(twigNodeContent);
                    break;
                default:
                    newItem.setOther(twigNodeContent);
                    break;
            }
        }
        return newItem;
    }

    private void updateChannel(NodeList nodeList) {
        for (int i = nodeList.getLength() - 1; i >= 0 ; i--) {  // We read the item in a reserve order
            Node childNode = nodeList.item(i);       // The childNode represents each item in the channel
            String childNodeName = childNode.getNodeName().toLowerCase();
            String childNodeContent = childNode.getTextContent();
            switch (childNodeName) {
                case RSSElements.TITLE:
                    rssChannel.setTitle(childNodeContent);
                    break;
                case RSSElements.LINK:
                    rssChannel.setLink(childNodeContent);
                    break;
                case RSSElements.PUBDATE:
                    rssChannel.setPubDate(childNodeContent);
                    break;
                case RSSElements.DESCRIPTION:
                    rssChannel.setDescription(childNodeContent);
                    break;

                case RSSElements.ITEM:
                    NodeList childNodeList = childNode.getChildNodes();
                    RSSItem newItem = buildItem(childNodeList);
                    if (!rssChannel.isRepetitive(newItem)) {
                        rssChannel.addItem(0, newItem); // The latest news will be added in the end of list
                        //updateIsReadHistory(newItem.getLink());
                    }
                    break;

                default:
                    rssChannel.setOther(childNodeContent);
                    break;
            }
        }
    }

    private void updateIsReadHistory(String key) {
        String[] columns = {NewsReaderData.News.URL_LINK};
        String selection = NewsReaderData.News.URL_LINK + "=?";
        String[] args = {key};
        Cursor cursor = contentResolver.query(NewsReaderData.News.CONTENT_URI, columns,
                selection, args, null);

        if (cursor == null) {
            ContentValues cv = new ContentValues();
            cv.put(NewsReaderData.News.URL_LINK, key);
            cv.put(NewsReaderData.News.IS_READ, false);
            Uri uri = contentResolver.insert(NewsReaderData.News.CONTENT_URI, cv);
            Log.v(TAG, "A new URI " + uri + " has been added into database.");
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    private InputStream getURLContent(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection.getInputStream();
    }
}
