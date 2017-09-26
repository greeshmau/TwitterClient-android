package com.gumapathi.codepath.twitteroauthclient.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.gumapathi.codepath.twitteroauthclient.Adapters.TweetAdapter;
import com.gumapathi.codepath.twitteroauthclient.Models.Tweet;
import com.gumapathi.codepath.twitteroauthclient.TwitterApplication;
import com.gumapathi.codepath.twitteroauthclient.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.gumapathi.codepath.twitteroauthclient.R.layout.activity_timeline);

        rvTweets = (RecyclerView) findViewById(com.gumapathi.codepath.twitteroauthclient.R.id.rvTweet);
        tweets = new ArrayList<>();
        tweetAdapter = new TweetAdapter(tweets);

        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(tweetAdapter);
        client = TwitterApplication.getRestClient();
        populateTimeline();
    }

    private void populateTimeline() {
        Log.i("SAMY-", "poptime");
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("SAMY-", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.i("SAMY-", response.toString());
                for (int i=0;i<response.length();i++) {
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyDataSetChanged();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                }
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
    }
}
