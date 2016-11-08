package com.netease.newsprac.WorkingClass;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by linzhou on 16-8-4.
 */

public interface NewsReaderData {
    public static final String AUTHORITY = "com.netease.newsprac";
    public static final String DB_NAME = "NewsReaderDatabase";

    public static final int VERSION = 1;

    public interface News extends BaseColumns {

        public static final String TABLE_NAME = "isRead";

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/" + TABLE_NAME);


        public static final String ID = "_id";
        public static final String URL_LINK = "link";
        public static final String IS_READ = "is_read";

    }

}
