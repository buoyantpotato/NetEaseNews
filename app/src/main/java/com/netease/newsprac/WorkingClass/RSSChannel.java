package com.netease.newsprac.WorkingClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by linzhou on 16-7-25.
 */

public class RSSChannel {

    private HashMap<Integer, String> newsRecorder = new HashMap<>();
    private int newsIndex = 0;

    private HashMap<String, String> infoElement;
    private List<RSSItem> items;
    //The latest news item will have the largest index in the list

    public RSSChannel() {
        infoElement = new HashMap<>();
        items = new ArrayList<>();
    }

    public void addItem(RSSItem newItem) {
        items.add(newItem);
        newsIndex++;
        newsRecorder.put(newsIndex, newItem.getLink());
    }

    public void addItem(int pos, RSSItem newItem) {
        items.add(pos, newItem);
        newsIndex++;
        newsRecorder.put(newsIndex, newItem.getLink());
    }

    public boolean isRepetitive(RSSItem item) {
        if (newsRecorder.containsValue(item.getLink())) {
            return true;
        }
        else {
            return false;
        }
    }

    public int getCount() {
        return (items == null) ? 0 : this.items.size();
    }

    public void deleteItem(int i) {
        items.remove(i);
    }

    public List<RSSItem> getItems() {
        return items;
    }

    public RSSItem getItem(int i) {
        if (i >= items.size()) {
            throw new IndexOutOfBoundsException("No such item!");
        }
        return items.get(i);
    }

    public void setTitle(String title) {
        infoElement.put(RSSElements.TITLE, title);
    }

    public void setLink(String link) {
        infoElement.put(RSSElements.LINK, link);
    }

    public void setPubDate(String pubDate) {
        infoElement.put(RSSElements.PUBDATE, pubDate);
    }

    public void setDescription(String description) {
        infoElement.put(RSSElements.DESCRIPTION, description);
    }

    public void setOther(String other) {
        infoElement.put(RSSElements.OTHER, other);
    }
}
