package com.codepath.apps.restclienttemplate.activities;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

public class TweetDetailActivity extends AppCompatActivity {

    public static final String TWEET_KEY = "tweet";
    public static final String TAG = "TweetDetailActivity";
    private ActivityTweetDetailBinding mBinding;
    private Tweet mTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_tweet_detail);
        setSupportActionBar(mBinding.detailActivityToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mTweet = getIntent().getParcelableExtra(TWEET_KEY);

        Log.d(TAG,mTweet.getText());
        bindData();

    }

    private void bindData(){
        mBinding.tvScreenName.setText(String.format(getString(R.string.screenNameFormat),
                mTweet.getUser().getScreenName()));
        mBinding.tvUserName.setText(mTweet.getUser().getName());
        mBinding.tvTweetBody.setText(mTweet.getText());
        mBinding.tvNumFav.setText(String.valueOf(mTweet.getFavoriteCount()));
        mBinding.tvNumRetweets.setText(String.valueOf(mTweet.getRetweetCount()));
        String profileImageUrl = mTweet.getUser().getProfileImageUrl();
        String biggerImageUrl = profileImageUrl.replace("normal","bigger");
        Glide.with(this).load(biggerImageUrl).asBitmap().centerCrop().into(new BitmapImageViewTarget(mBinding.ivProfileImage) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                mBinding.ivProfileImage.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
