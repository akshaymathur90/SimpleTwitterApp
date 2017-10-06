package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.restclienttemplate.fragments.MentionsFragment;
import com.codepath.apps.restclienttemplate.fragments.TimelineFragment;

/**
 * Created by akshaymathur on 10/6/17.
 */

public class TwitterFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "HOME", "MENTIONS",};
    private Context context;

    public TwitterFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return TimelineFragment.newInstance(TimelineFragment.HOME_TIMELINE);
            case 1: return TimelineFragment.newInstance(TimelineFragment.MENTIONS_TIMELINE);
            default: return TimelineFragment.newInstance(TimelineFragment.HOME_TIMELINE);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
