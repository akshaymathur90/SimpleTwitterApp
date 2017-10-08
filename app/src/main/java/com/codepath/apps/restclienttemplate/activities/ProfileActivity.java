package com.codepath.apps.restclienttemplate.activities;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.ProfileFragmentPagerAdapter;
import com.codepath.apps.restclienttemplate.adapters.TwitterFragmentPagerAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityProfileBinding;
import com.codepath.apps.restclienttemplate.fragments.TimelineFragment;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.network.TwitterApplication;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {
    public static final String TAG = "ProfileActivity";
    public static final String USER_KEY = "screen_name";
    private ActivityProfileBinding mBinding;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_profile);
        Bundle b = getIntent().getExtras();
        if(b==null){
            Log.d(TAG,"Logged in user timeline");
            fetchLoggedInUser();
        }
        else{
            Log.d(TAG,"Clicked user timeline");
            mUser = b.getParcelable(USER_KEY);
            bindData(mUser);
        }
    }

    private void fetchLoggedInUser(){
        TwitterClient client = TwitterApplication.getRestClient();
        client.getLoggedInUser(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG,"Status code--> "+statusCode);
                Log.d(TAG,"Response--> "+response.toString());
                Gson gson = new Gson();
                User user = gson.fromJson(response.toString(),User.class);
                Log.d(TAG,"User--> "+user.getScreenName());
                bindData(user);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG,"Status code--> "+statusCode);
                Log.d(TAG,"Error response--> "+responseString);
                Log.d(TAG,"Throwable-->", throwable);
            }
        });
    }

    private void bindData(User user){
        mBinding.tvScreenName.setText(String.format(getString(R.string.screenNameFormat),user.getScreenName()));
        mBinding.tvUserName.setText(user.getName());
        mBinding.tvNumFollowers.setText(String.valueOf(user.getFollowersCount()));
        mBinding.tvNumFollowing.setText(String.valueOf(user.getFriendsCount()));
        mBinding.tvUserLocation.setText(user.getLocation());
        mBinding.tvUserDesc.setText(user.getDescription());

        Glide.with(this).load(user.getProfileBannerUrl()+"/1500x500").into(mBinding.ivBackdropImage);
        String profileImageUrl = user.getProfileImageUrl();
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
        /*mFragmentManager.beginTransaction()
                .add(R.id.user_tweets_fragment_container, TimelineFragment.newInstance(
                        TimelineFragment.USER_TIMELINE,user.getScreenName())).commit();*/

        mBinding.profileViewPager.setAdapter(new ProfileFragmentPagerAdapter(getSupportFragmentManager(),
                this,user.getScreenName()));
        mBinding.profileTabLayout.setupWithViewPager(mBinding.profileViewPager);
    }




}
