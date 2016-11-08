package com.netease.newsprac;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.getExternalStorageDirectory;
import static android.os.Environment.isExternalStorageRemovable;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = getContext().getClass().getSimpleName();
    public static final String UNIQUE_FILE_NAME = "NetEaseNews";

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        viewPager.setAdapter(new CustomAdapter(getSupportFragmentManager()));

        tabLayout = (TabLayout) findViewById(R.id.main_tab_menu);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(tabSelectedListener);


        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_container, new NewsFragment())
                    .commit();
        }*/

        //getDiskCacheDir(this, UNIQUE_FILE_NAME);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class CustomAdapter extends FragmentPagerAdapter {

        private String[] fragments = {"News", "Weather"};

        public CustomAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new NewsFragment();
                case 1:
                    return new WeatherFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments[position];
        }
    }

    private TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            viewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            viewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            viewPager.setCurrentItem(tab.getPosition());
        }
    };

    public static File getDiskCacheDir(Context context, String nickName) {
        final String path =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ? context.getExternalCacheDir().getPath() :
                        context.getCacheDir().getPath();

        Log.v(TAG, context.getExternalCacheDir().getPath());
        Log.v(TAG, context.getCacheDir().getPath());

        return new File(path + File.separator + nickName);
    }

    /*public static Context getContext() {
        return getContext();
    }*/

    /*public static int getAppVersionNum() {
        try {
            PackageManager manager = getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getContext().getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 1;
        }
    }*/
}
