package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.activities.ProfileActivity;
import com.codepath.apps.restclienttemplate.activities.TimelineActivity;
import com.codepath.apps.restclienttemplate.databinding.TweetRowItemBinding;
import com.codepath.apps.restclienttemplate.fragments.ComposeTweetFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by akshaymathur on 9/27/17.
 */

public class TimelineRecyclerViewAdapter extends RecyclerView.Adapter {

    private LinkedList<Tweet> mTweetList;
    private Context mContext;

    public TimelineRecyclerViewAdapter(Context context,LinkedList<Tweet> tweetList){
        mContext = context;
        mTweetList = tweetList;
    }

    public void addMoreData(List<Tweet> tweetList){
        int initialSize = mTweetList.size();
        mTweetList.addAll(tweetList);
        notifyItemRangeChanged(initialSize,tweetList.size());
    }
    public void addPostedTweet(Tweet tweet){
        mTweetList.addFirst(tweet);
    }
    public void addModeDatatoFront(List<Tweet> tweetList){
        int i= tweetList.size()-1;
        for(;i>=0;i--){
            addPostedTweet(tweetList.get(i));
        }
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tweet_row_item,parent,false);
        TweetViewHolder viewHolder = new TweetViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Tweet tweet = mTweetList.get(position);
        ((TweetViewHolder) holder).bind(tweet);
    }

    @Override
    public int getItemCount() {
        return mTweetList.size();
    }

    public class TweetViewHolder extends RecyclerView.ViewHolder{
        private TweetRowItemBinding mTweetRowItemBinding;
        public TweetViewHolder(View itemView){
            super(itemView);
            mTweetRowItemBinding = DataBindingUtil.bind(itemView);
        }

        public void bind(final Tweet tweet){

            mTweetRowItemBinding.tvScreenName.setText(String.format(mContext
                    .getString(R.string.screenNameFormat),tweet.getUser().getScreenName()));
            mTweetRowItemBinding.tvTweetBody.setText(tweet.getText());
            mTweetRowItemBinding.tvUserName.setText(tweet.getUser().getName());
            mTweetRowItemBinding.tvFavNum.setText(String.valueOf(tweet.getFavoriteCount()));
            mTweetRowItemBinding.tvRetweetNumber.setText(String.valueOf(tweet.getRetweetCount()));
            mTweetRowItemBinding.tvTimeSince.setText(String.valueOf(tweet.getRelativeTimeAgo()));

            int favDrawable = tweet.getFavorited() ? R.drawable.ic_favorite_red_24dp : R.drawable.ic_favorite_border_black_24dp;
            int favColor = tweet.getFavorited() ? R.color.material_red : R.color.material_grey;
            mTweetRowItemBinding.ivFavIcon.setImageDrawable(ContextCompat.getDrawable(mContext,favDrawable));
            mTweetRowItemBinding.tvFavNum.setTextColor(ContextCompat.getColor(mContext,favColor));
            int retweetDrawable = tweet.getRetweeted() ? R.drawable.ic_repeat_green_24dp : R.drawable.ic_repeat_black_24dp;
            int retweetColor = tweet.getFavorited() ? R.color.material_green : R.color.material_grey;
            mTweetRowItemBinding.ivRetweetIcon.setImageDrawable(ContextCompat.getDrawable(mContext,retweetDrawable));
            mTweetRowItemBinding.tvRetweetNumber.setTextColor(ContextCompat.getColor(mContext,retweetColor));
            String profileImageUrl = tweet.getUser().getProfileImageUrl();
            String biggerImageUrl = profileImageUrl.replace("normal","bigger");
            Glide.with(mContext).load(biggerImageUrl).asBitmap().centerCrop().into(new BitmapImageViewTarget(mTweetRowItemBinding.ivUserThumbnail) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    mTweetRowItemBinding.ivUserThumbnail.setImageDrawable(circularBitmapDrawable);
                }
            });
            mTweetRowItemBinding.ivUserThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ProfileActivity.class);
                    intent.putExtra(ProfileActivity.USER_KEY,tweet.getUser());
                    mContext.startActivity(intent);
                }
            });
            mTweetRowItemBinding.ivReplyIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    replyToTweet(tweet);
                }
            });
        }

        private void replyToTweet(Tweet tweet){
            ComposeTweetFragment composeTweetFragment = ComposeTweetFragment.newInstance(tweet);
            composeTweetFragment.setPostTweetListener((TimelineActivity) mContext);
            composeTweetFragment.show(((TimelineActivity) mContext).getSupportFragmentManager(),ComposeTweetFragment.TAG);
        }
    }
}
