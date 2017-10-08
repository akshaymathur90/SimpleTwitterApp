package com.codepath.apps.restclienttemplate.activities;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.network.TwitterApplication;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

public class TweetDetailActivity extends AppCompatActivity {

    public static final String TWEET_KEY = "tweet";
    public static final String TAG = "TweetDetailActivity";
    private ActivityTweetDetailBinding mBinding;
    private Tweet mTweet;
    private boolean mIsRetweeted;
    private boolean mIsFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tweet_detail);
        setSupportActionBar(mBinding.detailActivityToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mTweet = getIntent().getParcelableExtra(TWEET_KEY);

        Log.d(TAG, mTweet.getText());
        mIsRetweeted = mTweet.getRetweeted();
        updateRetweetDrawables(mIsRetweeted);
        mIsFavorite = mTweet.getFavorited();
        updateFavDrawables(mIsFavorite);
        bindData();
        mBinding.ivRetweetIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsRetweeted) retweet();
            }
        });

        mBinding.ivFavIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsFavorite) favouriteTweet();
                else unFavouriteTweet();
            }
        });

    }

    private void bindData() {
        mBinding.tvScreenName.setText(String.format(getString(R.string.screenNameFormat),
                mTweet.getUser().getScreenName()));
        mBinding.tvUserName.setText(mTweet.getUser().getName());
        mBinding.tvTweetBody.setText(mTweet.getText());
        mBinding.tvNumFav.setText(String.valueOf(mTweet.getFavoriteCount()));
        mBinding.tvNumRetweets.setText(String.valueOf(mTweet.getRetweetCount()));
        String profileImageUrl = mTweet.getUser().getProfileImageUrl();
        String biggerImageUrl = profileImageUrl.replace("normal", "bigger");
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

    private void retweet() {
        TwitterClient client = TwitterApplication.getRestClient();
        try {
            client.postRetweet(mTweet.getId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(TAG, "Retweeted Status code == " + statusCode);
                    Log.d(TAG, "Retweeted response--> " + response.toString());
                    if (statusCode == 200) {
                        mIsRetweeted = true;
                        updateRetweetDrawables(true);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d(TAG, "Retweeted Status code == " + statusCode);
                    Log.d(TAG, "Error ", throwable);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void favouriteTweet() {
        TwitterClient client = TwitterApplication.getRestClient();
        client.postFavourite(mTweet.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "Favourite Status code == " + statusCode);
                Log.d(TAG, "Favourite response--> " + response.toString());
                if (statusCode == 200) {
                    mIsFavorite = true;
                    updateFavDrawables(true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "Favourite Status code == " + statusCode);
                Log.d(TAG, "Error ", throwable);
            }
        });
    }

    private void unFavouriteTweet() {
        TwitterClient client = TwitterApplication.getRestClient();
        client.postUnFavourite(mTweet.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "UnFavourite Status code == " + statusCode);
                Log.d(TAG, "UnFavourite response--> " + response.toString());
                if (statusCode == 200) {
                    mIsFavorite = false;
                    updateFavDrawables(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "UnFavourite Status code == " + statusCode);
                Log.d(TAG, "Error ", throwable);
            }
        });
    }

    private void updateRetweetDrawables(boolean isRetweeted) {
        int retweetDrawable = isRetweeted ? R.drawable.ic_repeat_green_24dp : R.drawable.ic_repeat_black_24dp;
        mBinding.ivRetweetIcon.setImageDrawable(ContextCompat.getDrawable(this, retweetDrawable));
        int retweetCount = isRetweeted ? mTweet.getRetweetCount() + 1 : mTweet.getRetweetCount() - 1;
        mBinding.tvNumRetweets.setText(String.valueOf(retweetCount));
    }

    private void updateFavDrawables(boolean isFavorite) {
        int favDrawable = isFavorite ? R.drawable.ic_favorite_red_24dp : R.drawable.ic_favorite_border_black_24dp;
        mBinding.ivFavIcon.setImageDrawable(ContextCompat.getDrawable(this, favDrawable));
        int favCount = isFavorite ? mTweet.getFavoriteCount() + 1 : mTweet.getFavoriteCount() - 1;
        mBinding.tvNumFav.setText(String.valueOf(favCount));
    }

    /*private void unRetweet(){
        TwitterClient client = TwitterApplication.getRestClient();
        try {
            client.postRetweet(mTweet.getId(), new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(TAG, "UnRetweet Status code == "+statusCode);
                    Log.d(TAG, "UnRetweet response--> "+response.toString());
                    if(statusCode == 200){
                        mIsRetweeted = false;
                        updateRetweetDrawables(false);
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d(TAG, "UnRetweet Status code == "+statusCode);
                    Log.d(TAG, "Error ",throwable);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
