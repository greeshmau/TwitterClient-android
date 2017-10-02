package com.gumapathi.codepath.twitteroauthclient.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gumapathi.codepath.twitteroauthclient.Decorators.LinkifiedTextView;
import com.gumapathi.codepath.twitteroauthclient.Models.Tweet;
import com.gumapathi.codepath.twitteroauthclient.R;
import com.gumapathi.codepath.twitteroauthclient.TwitterApplication;
import com.gumapathi.codepath.twitteroauthclient.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by gumapathi on 9/26/2017.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.TweetViewHolder> {
    List<Tweet> allTweets;
    private Context mContext;

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
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(com.gumapathi.codepath.twitteroauthclient.R.layout.item_tweet, parent, false);
        TweetViewHolder viewHolder = new TweetViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TweetViewHolder viewHolder, int position) {
        Tweet thisTweet = allTweets.get(position);

        viewHolder.tvUsername.setText(thisTweet.getUser().getName());
        viewHolder.tvScreenName.setText("@" + thisTweet.getUser().getScreenName());
        viewHolder.tvBody.setText(thisTweet.getBody());
        viewHolder.tvRetweetCount.setText("");
        viewHolder.tvLikeCount.setText("");

        ImageView ivRetweet = viewHolder.ivRetweet;
        ImageButton ivLike = viewHolder.ivLike;

        if (thisTweet.getRetweetCount() > 0) {
            ivRetweet.setImageResource(0);
            viewHolder.tvRetweetCount.setText(String.valueOf(thisTweet.getRetweetCount()));
            ivRetweet.setImageResource(R.drawable.retweet_off);
        }
        if (thisTweet.getFavouritesCount() > 0) {
            ivLike.setImageResource(0);
            viewHolder.tvLikeCount.setText(String.valueOf(thisTweet.getFavouritesCount()));
            ivLike.setImageResource(R.drawable.like_off);

        }

        try {

            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            DateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
            inputFormat.setLenient(true);

            Date twDate = inputFormat.parse(thisTweet.getCreatedAt());
            String outputText = outputFormat.format(twDate);

            Date newDate = new Date();
            long timeDiff = 0;
            String suffix = "m";
            timeDiff = getDateDiff(twDate, newDate, TimeUnit.MINUTES);
            boolean hour = false;
            if(timeDiff > 60) {
                timeDiff = getDateDiff(twDate, newDate, TimeUnit.HOURS);
                suffix = "h";
                hour = true;
            }
            if(timeDiff > 24 && hour) {
                timeDiff = getDateDiff(twDate, newDate, TimeUnit.DAYS);
                suffix = "d";
            }

            viewHolder.tvTime.setText(String.valueOf(timeDiff)+suffix);
        }
        catch (ParseException e) {
            Log.i("SAMY-dtParsEx ", e.getMessage());
            e.printStackTrace();
        }
        ImageView ivProfileImage = viewHolder.ivProfileImage;
        ImageView ivPhoto = viewHolder.ivPhoto;

        ivProfileImage.setImageResource(android.R.color.transparent);
        ivPhoto.setImageResource(android.R.color.transparent);

        if(thisTweet.isFavorited()) {
            ivLike.setImageResource(R.drawable.like_on);
        }
        else {
            ivLike.setImageResource(R.drawable.like_off);
        }

        Glide.with(ivProfileImage.getContext())
                .load(thisTweet.getUser().getProfileImageURL())
                .bitmapTransform(new RoundedCornersTransformation(mContext, 40, 0))
                .into(ivProfileImage);
        String url = thisTweet.getMediaUrl();
        //Log.i("SAMY-tryset media url ", "why not " + url + "--");
        if(!url.isEmpty()) {
            Glide.with(ivPhoto.getContext())
                    .load(thisTweet.getMediaUrl())
                    .bitmapTransform(new RoundedCornersTransformation(mContext, 15, 0))
                    .into(ivPhoto);
        }
        else {
            ivPhoto.setVisibility(View.GONE);
        }
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
        public TextView tvUsername;
        public TextView tvTime;
        public TextView tvScreenName;
        public TextView tvLikeCount;
        public TextView tvRetweetCount;

        public LinkifiedTextView tvBody;

        public ImageView ivProfileImage;
        public ImageView ivPhoto;
        public ImageButton ivLike;
        public ImageView ivRetweet;

        public TweetViewHolder(View view) {
            super(view);
            ivProfileImage = (ImageView)view.findViewById((com.gumapathi.codepath.twitteroauthclient.R.id.ivProfileImage));
            tvUsername = (TextView) view.findViewById(com.gumapathi.codepath.twitteroauthclient.R.id.tvUserName);
            tvBody = (LinkifiedTextView) view.findViewById(com.gumapathi.codepath.twitteroauthclient.R.id.tvBody);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
            tvScreenName = (TextView) view.findViewById(R.id.tvScreenName);
            tvLikeCount = (TextView) view.findViewById(R.id.tvLikeCount);
            tvRetweetCount = (TextView) view.findViewById(R.id.tvRetweetCount);
            ivPhoto = (ImageView) view.findViewById(R.id.ivPhoto);
            ivRetweet = (ImageView) view.findViewById(R.id.ivRetweet);
            ivLike = (ImageButton) view.findViewById(R.id.ivLike);

            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Tweet tweet = allTweets.get(getAdapterPosition());
                    TwitterClient client = TwitterApplication.getRestClient();
                    client.favoriteTweet(tweet.getUid(),new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.i("SAMY-", response.toString());
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Log.i("SAMY-", responseString);
                            throwable.printStackTrace();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            Log.i("SAMY-", errorResponse.toString());
                            throwable.printStackTrace();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.i("SAMY-", errorResponse.toString());
                            throwable.printStackTrace();
                        }
                    });
                    ivLike.setImageResource(R.drawable.like_on);
                    Toast.makeText(v.getContext(), "Favorited the tweet", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
