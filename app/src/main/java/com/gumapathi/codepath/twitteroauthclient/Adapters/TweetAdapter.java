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
import com.gumapathi.codepath.twitteroauthclient.R;
import com.raizlabs.android.dbflow.annotation.Database;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

        viewHolder.tvUsername.setText(thisTweet.getUser().getName());
        viewHolder.tvBody.setText(thisTweet.getBody());
        try {

            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            DateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
            inputFormat.setLenient(true);

            Date twDate = inputFormat.parse(thisTweet.getCreatedAt());
            String outputText = outputFormat.format(twDate);

            Date newDate = new Date();
            long timeDiff = getDateDiff(twDate, newDate, TimeUnit.MINUTES);

            viewHolder.tvTime.setText(String.valueOf(timeDiff)+"m ago");
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        ImageView ivProfileImage = viewHolder.ivProfileImage;

        Glide.with(ivProfileImage.getContext())
                .load(thisTweet.getUser().getProfileImageURL())
                .into(ivProfileImage);

    }

    /*@Override
    public void notifyDataSetChanged() {
        this.setNotifyOnChange(false);

        this.sort(new Comparator<Tweet>() {
            @Override
            public int compare(Tweet lhs, Tweet rhs) {
                return lhs.uid.compareTo(rhs.uid);
            }
        });

        this.setNotifyOnChange(true);
        super.notifyDataSetChanged();
    }*/

    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public class TweetViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvTime;

        public TweetViewHolder(View view) {
            super(view);
            ivProfileImage = (ImageView)view.findViewById((com.gumapathi.codepath.twitteroauthclient.R.id.ivProfileImage));
            tvUsername = (TextView) view.findViewById((com.gumapathi.codepath.twitteroauthclient.R.id.tvUserName));
            tvBody = (TextView) view.findViewById(com.gumapathi.codepath.twitteroauthclient.R.id.tvBody);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
        }
    }
}
