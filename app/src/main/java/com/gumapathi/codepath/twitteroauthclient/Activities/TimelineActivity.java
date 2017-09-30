package com.gumapathi.codepath.twitteroauthclient.Activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gumapathi.codepath.twitteroauthclient.Adapters.TweetAdapter;
import com.gumapathi.codepath.twitteroauthclient.Fragments.ComposeTweetDialogFragment;
import com.gumapathi.codepath.twitteroauthclient.Helpers.EndlessRecyclerViewScrollListener;
import com.gumapathi.codepath.twitteroauthclient.Models.Tweet;
import com.gumapathi.codepath.twitteroauthclient.Models.Tweet_Table;
import com.gumapathi.codepath.twitteroauthclient.Models.User;
import com.gumapathi.codepath.twitteroauthclient.Models.User_Table;
import com.gumapathi.codepath.twitteroauthclient.R;
import com.gumapathi.codepath.twitteroauthclient.TwitterApplication;
import com.gumapathi.codepath.twitteroauthclient.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.gumapathi.codepath.twitteroauthclient.TwitterClient.TWEET_COUNT;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetDialogFragment.ComposeTweetDialogListener {

    private TwitterClient client;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    EndlessRecyclerViewScrollListener scrollListener;
    boolean startOfOldTweets = false;
    boolean endOfOldTweets = false;
    private TextView toolbar_title;
    int ACTION_COMPOSE_CODE = 20;
    ComposeTweetDialogFragment composeTweetDialogFragment;
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.gumapathi.codepath.twitteroauthclient.R.layout.activity_timeline);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                populateTimeline(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        rvTweets = (RecyclerView) findViewById(com.gumapathi.codepath.twitteroauthclient.R.id.rvTweet);
        tweets = new ArrayList<>();
        tweetAdapter = new TweetAdapter(tweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(tweetAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populateTimeline(page);
            }
        };
        rvTweets.addOnScrollListener(scrollListener);
        client = TwitterApplication.getRestClient();
        populateTimeline(0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == ACTION_COMPOSE_CODE) {
            Tweet postedTweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("PostedTweet"));
            tweets.add(0,postedTweet);
            tweetAdapter.notifyDataSetChanged();
            Log.i("SAMY", "added to adapter");
            rvTweets.scrollToPosition(0);
        }
    }

    private void populateTimeline(final int page) {
        Log.i("SAMY-", "poptime");
        Cursor cursor = SQLite.select(Method.max(Tweet_Table.uid).as("storedSinceID")).from(Tweet.class).query();
        //async().execute(); // non-UI blocking
        long storedSinceID =  1;
        long storedMaxId = 1;
        /*try {
            storedSinceID = cursor.getLong(0);
        }
        catch (Exception e) {

        }
        cursor.close();

        cursor = SQLite.select(Method.min(Tweet_Table.uid).as("storedSinceID")).from(Tweet.class).query();
        try {
            storedMaxId = cursor.getInt(0); //get min tweet from DB
        }
        catch (Exception e) {

        }
        cursor.close();*/
        if(page == 0 || !startOfOldTweets) {
            Log.i("SAMY", "new tweets " + String.valueOf(startOfOldTweets) + " page - " + String.valueOf(page));
            client.getHomeTimeline(storedSinceID, new JsonHttpResponseHandler() {
                boolean cleared = false;

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.i("SAMY-", response.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.i("SAMY-", response.toString());
                    if(page == 0 && !cleared) {
                        SQLite.delete(Tweet.class).async().execute();
                        SQLite.delete(User.class).async().execute();
                        cleared = true;
                        tweets.clear();
                        tweetAdapter.notifyDataSetChanged();
                    }
                    if(response.length() < TWEET_COUNT){
                        Log.i("SAMY", "setting startOfOldTweets to true " + String.valueOf(response.length()));
                        startOfOldTweets = true;
                    }

                    try {
                        List<Tweet> newTweets = Tweet.fromJSONArray(response);
                        Log.i("SAMY", "setting all tweets" + String.valueOf(newTweets.size()));
                        tweets.addAll(newTweets);
                        tweetAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.i("SAMY-", responseString);
                    startOfOldTweets = true;
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.i("SAMY-", errorResponse.toString());
                    startOfOldTweets = true;
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.i("SAMY-", errorResponse.toString());
                    startOfOldTweets = true;
                    throwable.printStackTrace();
                }
            });
        }
        else if(!endOfOldTweets){
            Log.i("SAMY", "old tweets " + String.valueOf(startOfOldTweets) + " page - " + String.valueOf(page));
            tweets.addAll(SQLite.select().from(Tweet.class).queryList());
            tweetAdapter.notifyDataSetChanged();
            endOfOldTweets = true;
        }
        else {
        //request tweets from storedMaxId
        }

    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_compose) {
            //Toast.makeText(this, "Compose clicked", Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(this, PostTweetActivity.class);
//            startActivityForResult(intent, ACTION_COMPOSE_CODE);
            FragmentManager fm = getSupportFragmentManager();
            composeTweetDialogFragment = ComposeTweetDialogFragment.newInstance("Filter");
            composeTweetDialogFragment.show(fm, "fragment_alert");
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFinishComposeTweetDialog(Bundle bundle) {
        Log.i("SAMY", "came back");
        if (bundle != null) {
            Tweet postedTweet = (Tweet) Parcels.unwrap(bundle.getParcelable("PostedTweet"));
            tweets.add(0,postedTweet);
            tweetAdapter.notifyDataSetChanged();
            Log.i("SAMY", "added to adapter " + postedTweet.getBody());
            rvTweets.scrollToPosition(0);
            //Log.d("DEBUG", "USER="+mUser.getProfileImageUrl());
        }
    }
}