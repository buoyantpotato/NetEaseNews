package com.netease.newsprac.WorkingClass;

import android.graphics.Bitmap;

/**
 * Created by linzhou on 16-7-25.
 */

public class RSSItem {

    private String title;
    private String link;
    private String pubDate;
    private String description;
    private String other;
    private String iconImgURL;
    private boolean isRead;

    public RSSItem() {
        this.title = "";
        this.link = "";
        this.pubDate = "";
        this.description = "";
        this.other = "";
        isRead = false;
    }

    public RSSItem(String title, String link, String pubDate, String description) {
        this.title = title;
        this.link = link;
        this.pubDate = pubDate;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getDescription() {
        return description;
    }

    public String getIconImgURL() {
        return iconImgURL;
    }

    public boolean isRead() {
        return this.isRead;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public void setIconImgURL(String bitmapURL) {
        this.iconImgURL = bitmapURL;
    }

    public void setRead() {
        this.isRead = true;
    }
}
