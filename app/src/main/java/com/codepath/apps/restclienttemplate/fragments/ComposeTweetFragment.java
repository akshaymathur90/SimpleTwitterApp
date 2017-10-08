package com.codepath.apps.restclienttemplate.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.network.TwitterApplication;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.FragmentComposeTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComposeTweetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeTweetFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = "ComposeTweet";
    public static final String TWEET_KEY = "tweet";
    private boolean mIsReply = false;
    private FragmentComposeTweetBinding mFragmentComposeTweetBinding;
    private PostTweetListener mListener;
    private Tweet mTweet;
    public interface PostTweetListener{
        void onSuccess(Boolean isReply, Tweet tweet);
    }
    public void setPostTweetListener(PostTweetListener listener){
        mListener = listener;
    }
    public ComposeTweetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ComposeTweetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComposeTweetFragment newInstance() {
        ComposeTweetFragment fragment = new ComposeTweetFragment();
        return fragment;
    }
    public static ComposeTweetFragment newInstance(Tweet tweet) {
        ComposeTweetFragment fragment = new ComposeTweetFragment();
        Bundle b = new Bundle();
        b.putParcelable(TWEET_KEY,tweet);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.AppTheme_NoActionBar);

        Bundle bundle = getArguments();
        if(bundle!=null){
            Log.d(TAG,"*** IN REPLY MODE ***");
            mIsReply = true;
            mTweet = bundle.getParcelable(TWEET_KEY);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentComposeTweetBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_compose_tweet,container,false);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mFragmentComposeTweetBinding.myToolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
            actionBar.setTitle("");
        }
        setHasOptionsMenu(true);

        return mFragmentComposeTweetBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentComposeTweetBinding.etTweetText.requestFocus();
        mFragmentComposeTweetBinding.tvCharCount.setText(getString(R.string.default_tweet_char_limit));
        if(mIsReply){
            Log.d(TAG,"Setting username in tweet body");
            mFragmentComposeTweetBinding.etTweetText.setText(String.format(getActivity()
                    .getString(R.string.screenNameFormat),mTweet.getUser().getScreenName()));
        }
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mFragmentComposeTweetBinding.etTweetText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG,"tweet length--> "+charSequence.length());
                int rem = 140-charSequence.length();
                mFragmentComposeTweetBinding.tvCharCount.setText(String.valueOf(rem));
                int textColor = rem<0? android.R.color.holo_red_dark : R.color.material_grey;
                mFragmentComposeTweetBinding.tvCharCount.setTextColor(ContextCompat.getColor(getActivity(),textColor));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mFragmentComposeTweetBinding.btnSendTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetText = mFragmentComposeTweetBinding.etTweetText.getText().toString();

                if(tweetText.length()<=140){
                    if(mIsReply){
                        postReply(tweetText);
                    }else{
                        postNewTweet(tweetText);
                    }
                }
            }
        });

    }

    private void postReply(String tweetText){
        TwitterClient client = TwitterApplication.getRestClient();
        client.postReplyToTweet(tweetText,mTweet.getId(),new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG,"Tweet Replied");
                Log.d(TAG,"Response--> "+response.toString());
                Gson gson = new Gson();
                Tweet tweet = gson.fromJson(response.toString(),Tweet.class);
                mListener.onSuccess(mIsReply,tweet);
                dismiss();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG,"Tweet reply FAILED");
                Log.d(TAG,"Status code--> "+statusCode);
                Log.d(TAG,"Response--> "+errorResponse.toString());
            }
        });
    }

    private void postNewTweet(String tweetText){
        TwitterClient client = TwitterApplication.getRestClient();
        client.postNewTweet(tweetText,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG,"Tweet Posted");
                Log.d(TAG,"Response--> "+response.toString());
                Gson gson = new Gson();
                Tweet tweet = gson.fromJson(response.toString(),Tweet.class);
                mListener.onSuccess(mIsReply,tweet);
                dismiss();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG,"Tweet posting FAILED");
                Log.d(TAG,"Status code--> "+statusCode);
                Log.d(TAG,"Response--> "+errorResponse.toString());
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_profile).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home: dismiss();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }
}
