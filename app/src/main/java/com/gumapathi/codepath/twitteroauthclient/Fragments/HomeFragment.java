package com.gumapathi.codepath.twitteroauthclient.Fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gumapathi.codepath.twitteroauthclient.Adapters.TweetAdapter;
import com.gumapathi.codepath.twitteroauthclient.Helpers.EndlessRecyclerViewScrollListener;
import com.gumapathi.codepath.twitteroauthclient.Models.Tweet;
import com.gumapathi.codepath.twitteroauthclient.Models.Tweet_Table;
import com.gumapathi.codepath.twitteroauthclient.R;
import com.gumapathi.codepath.twitteroauthclient.TwitterApplication;
import com.gumapathi.codepath.twitteroauthclient.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.gumapathi.codepath.twitteroauthclient.TwitterClient.TWEET_COUNT;
import static com.gumapathi.codepath.twitteroauthclient.Utils.Utils.checkForInternet;

/**
 * Created by gumapathi on 10/1/17.
 */

public class HomeFragment extends Fragment implements ComposeTweetDialogFragment.ComposeTweetDialogListener{
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    EndlessRecyclerViewScrollListener scrollListener;
    boolean startOfOldTweets = false;
    boolean endOfOldTweets = false;
    private TwitterClient client;
    private SwipeRefreshLayout swipeContainer;
    boolean noNewTweets = true;
    public static final String ARG_PAGE = "ARG_PAGE";
    ComposeTweetDialogFragment composeTweetDialogFragment;


    public static HomeFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        client = TwitterApplication.getRestClient();
        if (savedInstanceState != null) {
            Tweet postedTweet = (Tweet) Parcels.unwrap(savedInstanceState.getParcelable("PostedTweet"));
            tweets.add(0, postedTweet);
            tweetAdapter.notifyDataSetChanged();
            Log.i("SAMY", "added to adapter " + postedTweet.getBody());
            rvTweets.scrollToPosition(0);
        }
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.refreshing
                populateTimeline(0, true);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        FloatingActionButton fabCompose = (FloatingActionButton) view.findViewById(R.id.fabCompose);
        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeNewTweet(v);
            }
        });
        rvTweets = (RecyclerView) view.findViewById(com.gumapathi.codepath.twitteroauthclient.R.id.rvTweet);
        tweets = new ArrayList<>();
        tweetAdapter = new TweetAdapter(tweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(tweetAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populateTimeline(page, false);
            }
        };
        rvTweets.addOnScrollListener(scrollListener);
        populateTimeline(0, false);

        return view;
    }

    private void populateTimeline(final int page, final boolean refreshing) {
        Log.i("SAMY-", "poptime");
        long storedSinceID = 1;
        long storedMaxId = 0;
        boolean isOnline = checkForInternet();
        if(!isOnline) {
            Toast.makeText(this.getContext(),"App is offline, showing stored tweets",Toast.LENGTH_LONG).show();
        }
        noNewTweets = true;
        try {
            storedSinceID = SQLite.select(Tweet_Table.uid).from(Tweet.class).orderBy(Tweet_Table.createdAt, false).limit(1).querySingle().getUid();
            storedMaxId = SQLite.select(Tweet_Table.uid).from(Tweet.class).orderBy(Tweet_Table.createdAt, true).limit(1).querySingle().getUid();
        } catch (Exception e) {
            Log.i("SAMY-sinceID-ex", e.getMessage());
        }
        Log.i("SAMY storedSinceID", String.valueOf(storedSinceID));
        Log.i("SAMY storedMaxId", String.valueOf(storedMaxId));

        if ((page == 0 || !startOfOldTweets) && isOnline) {
            Log.i("SAMY", "new tweets " + String.valueOf(startOfOldTweets) + " page - " + String.valueOf(page));
            client.getHomeTimeline(storedSinceID, 0,new JsonHttpResponseHandler() {
                boolean cleared = false;

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.i("SAMY-", response.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.i("SAMY-refreshing", String.valueOf(refreshing));
                    if (page == 0 && !cleared && !refreshing) {
                        //SQLite.delete(Tweet.class).async().execute();
                        //SQLite.delete(User.class).async().execute();
                        cleared = true;
                        tweets.clear();
                        tweetAdapter.notifyDataSetChanged();
                    }
                    if (response.length() < TWEET_COUNT) {
                        Log.i("SAMY", "setting startOfOldTweets to true ");
                        startOfOldTweets = true;
                    }

                    try {
                        List<Tweet> newTweets = Tweet.fromJSONArray(response);
                        Log.i("SAMY", "setting all tweets initial" + String.valueOf(newTweets.size()));
                        if(newTweets.size() > 0) noNewTweets = false;

                        if(refreshing) {
                            tweets.addAll(0,newTweets);
                            tweetAdapter.notifyDataSetChanged();
                        }
                        else {
                            tweets.addAll(newTweets);
                            tweetAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.i("SAMY-eof-endOfOldTweets",String.valueOf(endOfOldTweets));
                    Log.i("SAMY-eof-noNewTweets",String.valueOf(noNewTweets));
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
        if (!endOfOldTweets || !isOnline || noNewTweets) {
            Log.i("SAMY", "old tweets " + String.valueOf(startOfOldTweets) + " page - " + String.valueOf(page));
            tweets.addAll(SQLite.select().from(Tweet.class).orderBy(Tweet_Table.createdAt, false).queryList());
            tweetAdapter.notifyDataSetChanged();
            endOfOldTweets = true;
        }
        if(endOfOldTweets && isOnline ){
            client.getHomeTimeline(0, storedMaxId,new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.i("SAMY-", response.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.i("SAMY-maxid-", response.toString());
                    try {
                        List<Tweet> newTweets = Tweet.fromJSONArray(response);
                        Log.i("SAMY", "setting all tweets " + String.valueOf(newTweets.size()));
                        tweets.addAll(newTweets);
                        tweetAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
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
        swipeContainer.setRefreshing(false);
    }


    public void composeNewTweet(View view) {
        //Toast.makeText(this, "Compose clicked", Toast.LENGTH_LONG).show();
        FragmentManager fm = getFragmentManager();
        composeTweetDialogFragment = ComposeTweetDialogFragment.newInstance("Filter");
        composeTweetDialogFragment.setTargetFragment(this,20);
        composeTweetDialogFragment.show(fm, "fragment_alert");
    }

    @Override
    public void onFinishComposeTweetDialog(Bundle bundle) {
        if (bundle != null) {
            Tweet postedTweet = (Tweet) Parcels.unwrap(bundle.getParcelable("PostedTweet"));
            tweets.add(0, postedTweet);
            tweetAdapter.notifyDataSetChanged();
            Log.i("SAMY", "added to adapter " + postedTweet.getBody());
            rvTweets.scrollToPosition(0);
        }
    }
}
