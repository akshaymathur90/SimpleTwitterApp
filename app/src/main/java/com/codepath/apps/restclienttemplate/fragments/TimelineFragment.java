package com.codepath.apps.restclienttemplate.fragments;


import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.activities.LoginActivity;
import com.codepath.apps.restclienttemplate.adapters.TimelineRecyclerViewAdapter;
import com.codepath.apps.restclienttemplate.network.TwitterApplication;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.FragmentTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimelineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimelineFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    public final static String TAG = "TimelineFragment";
    public final static int REQUEST_TWEET = 0;
    private FragmentTimelineBinding mFragmentTimelineBinding;
    private LinkedList<Tweet> mList;
    private LinearLayoutManager mLinearLayoutManager;
    private TimelineRecyclerViewAdapter mAdapter;
    public final static int HOME_TIMELINE = 1;
    public final static int MENTIONS_TIMELINE = 2;
    public final static int USER_TIMELINE = 3;
    public final static String FRAGMENT_TYPE_KEY = "fragment_type";
    public final static String SCREEN_NAME_KEY = "screen_name";
    private int fragmentType;
    private String screenName;
    public TimelineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TimelineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimelineFragment newInstance(int fragmentType) {
        TimelineFragment fragment = new TimelineFragment();
        Bundle b = new Bundle();
        b.putInt(FRAGMENT_TYPE_KEY,fragmentType);
        fragment.setArguments(b);
        return fragment;
    }

    public static TimelineFragment newInstance(int fragmentType,String screenName) {
        TimelineFragment fragment = new TimelineFragment();
        Bundle b = new Bundle();
        b.putInt(FRAGMENT_TYPE_KEY,fragmentType);
        b.putString(SCREEN_NAME_KEY,screenName);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if(b!=null){
            fragmentType = b.getInt(FRAGMENT_TYPE_KEY);
            screenName = b.getString(SCREEN_NAME_KEY);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentTimelineBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline, container, false);
        mList = new LinkedList<>();
        mLinearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mAdapter = new TimelineRecyclerViewAdapter(getActivity(),mList);
        mFragmentTimelineBinding.rvTimeline.setLayoutManager(mLinearLayoutManager);
        mFragmentTimelineBinding.rvTimeline.setAdapter(mAdapter);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        mFragmentTimelineBinding.rvTimeline.addItemDecoration(itemDecoration);
        final EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Tweet tweet = mList.get(mList.size()-1);
                Log.d(TAG,"Loading maxid--> "+tweet.getId());
                fetchDataForFragment(fragmentType,tweet.getId()-1,1);
            }
        };
        mFragmentTimelineBinding.rvTimeline.addOnScrollListener(endlessRecyclerViewScrollListener);
        mFragmentTimelineBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                long since_id = 1;
                if(mList.size()>0){
                    Tweet tweet = mList.get(0);
                    since_id = tweet.getId();
                    Log.d(TAG,"Loading sinceid--> "+since_id);
                }
                endlessRecyclerViewScrollListener.resetState();
                fetchDataForFragment(fragmentType,0,since_id);
            }
        });
        mFragmentTimelineBinding.swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        fetchDataForFragment(fragmentType,0,1);
        setHasOptionsMenu(true);
        return mFragmentTimelineBinding.getRoot();
    }

    private void fetchDataForFragment(int fragmentType, long maxId, long sinceId){
        switch (fragmentType){
            case HOME_TIMELINE: fetchTimelineTweets(maxId,sinceId);
                break;
            case MENTIONS_TIMELINE: fetchMentionTimeline(maxId,sinceId);
                break;
            case USER_TIMELINE: fetchUserTimeline(maxId,sinceId,screenName);
                break;
        }
    }

    private void fetchUserTimeline(long maxId, final long sinceId, String screenName) {
        TwitterClient client = TwitterApplication.getRestClient();
        client.getUserTimelineList(maxId,sinceId,screenName, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                parseData(statusCode,response,sinceId);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handleFailedResponse(statusCode,responseString,throwable);
            }
        });
    }

    private void fetchMentionTimeline(long maxId, final long sinceId) {
        Log.d(TAG,"Fetching mentions timeline ->" + maxId +" - "+ sinceId);
        TwitterClient client = TwitterApplication.getRestClient();
        client.getMentionsTimeline(maxId,sinceId, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                parseData(statusCode,response,sinceId);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handleFailedResponse(statusCode,responseString,throwable);
            }
        });
    }

    private void parseData(int statusCode, JSONArray response, long sinceId){
        List<Tweet> tweets = new ArrayList<Tweet>();
        Gson gson = new Gson();
        Log.d(TAG,"Status Code--> "+statusCode);
        Log.d(TAG, "data--> "+response.toString());
        for(int i=0;i<response.length();i++){
            try {
                Tweet tweet = gson.fromJson(response.get(i).toString(),Tweet.class);
                tweets.add(tweet);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mFragmentTimelineBinding.swipeRefreshLayout.setRefreshing(false);
        if(tweets.size()>0 && sinceId > 1){
            mAdapter.addModeDatatoFront(tweets);
        }else{
            mAdapter.addMoreData(tweets);
        }
    }

    private void handleFailedResponse(int statusCode,String responseString, Throwable throwable){
        Log.d(TAG,"Request Failed");
        Log.d(TAG,"Status Code--> "+statusCode);
        Log.d(TAG, "Response--> "+responseString);
        throwable.printStackTrace();
    }

    private void fetchTimelineTweets(long maxId, final long sinceId) {
        TwitterClient client = TwitterApplication.getRestClient();
        client.getHomeTimelineList(maxId,sinceId, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                parseData(statusCode,response,sinceId);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handleFailedResponse(statusCode,responseString,throwable);
            }
        });
    }

    public void addTweetAtTop(Tweet tweet){
        mAdapter.addPostedTweet(tweet);
        mAdapter.notifyItemInserted(0);
        mFragmentTimelineBinding.rvTimeline.smoothScrollToPosition(0);
    }
}
