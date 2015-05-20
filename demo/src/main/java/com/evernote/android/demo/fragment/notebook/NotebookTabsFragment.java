package com.evernote.android.demo.fragment.notebook;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evernote.android.demo.R;
import com.evernote.android.demo.activity.MainActivity;
import com.evernote.android.demo.view.SlidingTabLayout;

/**
 * @author rwondratschek
 */
public class NotebookTabsFragment extends Fragment {

    private MyViewPagerAdapter mViewPagerAdapter;
    private SlidingTabLayout mTabs;
    private ViewPager mViewPager;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentActivity activity = getActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).getSupportActionBar().setTitle(R.string.notebooks);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notebook_tabs, container, false);

        mViewPagerAdapter = new MyViewPagerAdapter(getActivity());

        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(mViewPagerAdapter);

        mTabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true);

        final int toolbarTextColor = getResources().getColor(R.color.tb_text);
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return toolbarTextColor;
            }
        });

        mTabs.setViewPager(mViewPager);

        return view;
    }

    private static class MyViewPagerAdapter extends FragmentStatePagerAdapter {

        @SuppressWarnings("unused")
        private final Context mContext;

        public MyViewPagerAdapter(FragmentActivity activity) {
            super(activity.getSupportFragmentManager());
            mContext = activity;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.personal);
                case 1:
                    return mContext.getString(R.string.linked);
                default:
                    throw new IndexOutOfBoundsException();
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new NotebookContainerFragment();
                case 1:
                    return new LinkedNotebookContainerFragment();
                default:
                    throw new IndexOutOfBoundsException();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
