package com.netease.newsprac;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.netease.newsprac.WorkingClass.RSSElements;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewsFragment extends Fragment {

    private final String URL_CNBETA = "http://rss.cnbeta.com/rss";
    private final String URL_QQ = "http://winfo.crc.com.cn/news/information/index_rss_1504.xml";

    private ListView listViewOfTopic;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public NewsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_news, container, false);

        viewPager = (ViewPager) rootView.findViewById(R.id.fragment_viewpager);
        viewPager.setAdapter(new CustomAdapter(getFragmentManager()));

        tabLayout = (TabLayout) rootView.findViewById(R.id.fragment_tab_news);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(tabSelectedListener);

        return rootView;
    }

    private class CustomAdapter extends FragmentPagerAdapter {

        private String[] fragments = {"CN-BETA", "HuaRun"};

        public CustomAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return newInstance(URL_CNBETA);
                case 1:
                    return newInstance(URL_QQ);
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

    private static final RefreshListChildFragment newInstance(String url) {
        RefreshListChildFragment fragment = new RefreshListChildFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString(RSSElements.URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }
}
