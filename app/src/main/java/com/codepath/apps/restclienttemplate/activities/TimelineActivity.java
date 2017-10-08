package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TwitterFragmentPagerAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.fragments.ComposeTweetFragment;
import com.codepath.apps.restclienttemplate.fragments.TimelineFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.network.TwitterApplication;
import com.codepath.apps.restclienttemplate.network.TwitterClient;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.PostTweetListener{
    FragmentManager mFragmentManager;
    private ActivityTimelineBinding mBinding;
    public final static String TAG = "TimelineActivity";
    private TwitterFragmentPagerAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_timeline);

        setSupportActionBar(mBinding.activityToolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(R.string.app_name);
            actionBar.setLogo(R.drawable.ic_twitter_logo_whiteonimage);
        }
        mFragmentManager = getSupportFragmentManager();

        mAdapter = new TwitterFragmentPagerAdapter(getSupportFragmentManager());
        mBinding.viewPager.setAdapter(mAdapter);
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);

        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ComposeTweetFragment composeTweetFragment = ComposeTweetFragment.newInstance();
                composeTweetFragment.setPostTweetListener(TimelineActivity.this);
                composeTweetFragment.show(getSupportFragmentManager(),ComposeTweetFragment.TAG);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_logout:
                TwitterClient twitterClient = TwitterApplication.getRestClient();
                twitterClient.clearAccessToken();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            case R.id.menu_profile:
                Intent profileIntent = new Intent(this, ProfileActivity.class);
                startActivity(profileIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(Boolean isReply, Tweet tweet) {
        Log.d(TAG,"Received tweet in parent activity");
        if(isReply){
            Snackbar.make(mBinding.getRoot(),R.string.label_reply_posted,Snackbar.LENGTH_LONG).show();
        }else{
            TimelineFragment fragment = (TimelineFragment) mAdapter.getRegisteredFragment(0);
            if(fragment!=null){
                fragment.addTweetAtTop(tweet);
                mBinding.viewPager.setCurrentItem(0,true);
            }
        }

    }
}
