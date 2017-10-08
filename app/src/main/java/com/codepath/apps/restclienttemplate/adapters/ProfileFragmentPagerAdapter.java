package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.restclienttemplate.fragments.TimelineFragment;

/**
 * Created by akshaymathur on 10/7/17.
 */

public class ProfileFragmentPagerAdapter extends FragmentPagerAdapter{
    final int PAGE_COUNT = 1;
    private String tabTitles[] = new String[] { "Tweets"};
    private Context context;
    private String screenName;

    public ProfileFragmentPagerAdapter(FragmentManager fm, Context context, String screenName) {
        super(fm);
        this.context = context;
        this.screenName = screenName;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return TimelineFragment.newInstance(TimelineFragment.USER_TIMELINE,screenName);
            default: return TimelineFragment.newInstance(TimelineFragment.HOME_TIMELINE);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
