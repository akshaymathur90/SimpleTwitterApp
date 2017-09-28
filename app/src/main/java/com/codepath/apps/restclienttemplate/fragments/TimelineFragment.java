package com.codepath.apps.restclienttemplate.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
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
public class TimelineFragment extends Fragment implements ComposeTweetFragment.PostTweetListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    public final static String TAG = "TimelineFragment";
    public final static int REQUEST_TWEET = 0;
    private FragmentTimelineBinding mFragmentTimelineBinding;
    private LinkedList<Tweet> mList;
    private LinearLayoutManager mLinearLayoutManager;
    private TimelineRecyclerViewAdapter mAdapter;

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
    public static TimelineFragment newInstance() {
        TimelineFragment fragment = new TimelineFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentTimelineBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline, container, false);
        mFragmentTimelineBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ComposeTweetFragment composeTweetFragment = ComposeTweetFragment.newInstance();
                composeTweetFragment.setTargetFragment(TimelineFragment.this,REQUEST_TWEET);
                composeTweetFragment.show(getFragmentManager(),ComposeTweetFragment.TAG);

            }
        });
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
                fetchTimelineTweets(tweet.getId()-1,1);
            }
        };
        mFragmentTimelineBinding.rvTimeline.addOnScrollListener(endlessRecyclerViewScrollListener);
        mFragmentTimelineBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Tweet tweet = mList.get(0);
                Log.d(TAG,"Loading sinceid--> "+tweet.getId());
                endlessRecyclerViewScrollListener.resetState();
                fetchTimelineTweets(0,tweet.getId());
            }
        });
        mFragmentTimelineBinding.swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        fetchTimelineTweets(0,1);
        return mFragmentTimelineBinding.getRoot();
    }

    private void fetchTimelineTweets(long maxId,final long sinceId) {
        TwitterClient client = TwitterApplication.getRestClient();
        client.getHomeTimelineList(maxId,sinceId, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
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

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG,"Request Failed");
                Log.d(TAG,"Status Code--> "+statusCode);
                Log.d(TAG, "Response--> "+responseString);
                throwable.printStackTrace();

            }
        });
    }

    @Override
    public void onSuccess(Tweet tweet) {
        Log.d(TAG,"Received tweet in parent fragment");
        mAdapter.addPostedTweet(tweet);
        mAdapter.notifyDataSetChanged();
    }
}
