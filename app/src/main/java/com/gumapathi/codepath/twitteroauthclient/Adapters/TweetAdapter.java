package com.gumapathi.codepath.twitteroauthclient.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gumapathi.codepath.twitteroauthclient.Models.Tweet;

import java.util.List;

/**
 * Created by gumapathi on 9/26/2017.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.TweetViewHolder> {
    List<Tweet> allTweets;

    public TweetAdapter(List<Tweet> allTweets) {
        this.allTweets = allTweets;
    }

    @Override
    public int getItemCount() {
        return this.allTweets.size();
    }

    @Override
    public TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(com.gumapathi.codepath.twitteroauthclient.R.layout.item_tweet, parent, false);
        TweetViewHolder viewHolder = new TweetViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TweetViewHolder viewHolder, int position) {
        Tweet thisTweet = allTweets.get(position);

        viewHolder.tvUsername.setText(thisTweet.user.name);
        viewHolder.tvBody.setText(thisTweet.body);
        ImageView ivProfileImage = viewHolder.ivProfileImage;

        Glide.with(ivProfileImage.getContext())
                .load(thisTweet.user.profileImageURL)
                .into(ivProfileImage);

    }

    public class TweetViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;

        public TweetViewHolder(View view) {
            super(view);
            ivProfileImage = (ImageView)view.findViewById((com.gumapathi.codepath.twitteroauthclient.R.id.ivProfileImage));
            tvUsername = (TextView) view.findViewById((com.gumapathi.codepath.twitteroauthclient.R.id.tvUserName));
            tvBody = (TextView) view.findViewById(com.gumapathi.codepath.twitteroauthclient.R.id.tvBody);
        }
    }
}
