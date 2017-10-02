package com.gumapathi.codepath.twitteroauthclient.Fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.gumapathi.codepath.twitteroauthclient.R;
import com.gumapathi.codepath.twitteroauthclient.TwitterApplication;
import com.gumapathi.codepath.twitteroauthclient.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.gumapathi.codepath.twitteroauthclient.Utils.Utils.checkForInternet;

/**
 * Created by santoshag on 10/1/17.
 */

public class FavoriteFragment extends Fragment {
    TweetAdapter favAdapter;
    ArrayList<Tweet> favorites;
    RecyclerView rvTweets;
    EndlessRecyclerViewScrollListener scrollListener;
    boolean startOfOldTweets = false;
    boolean endOfOldTweets = false;
    private TwitterClient client;
    private SwipeRefreshLayout swipeContainer;
    boolean noNewTweets = true;
    public static final String ARG_PAGE = "ARG_PAGE";

    public static FavoriteFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        FavoriteFragment fragment = new FavoriteFragment();
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


        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.refreshing
                populatefavoriteTimeline(0, true);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        FloatingActionButton fabCompose = (FloatingActionButton) view.findViewById(R.id.fabCompose);
        fabCompose.setVisibility(View.GONE);
        rvTweets = (RecyclerView) view.findViewById(com.gumapathi.codepath.twitteroauthclient.R.id.rvTweet);
        favorites = new ArrayList<>();
        favAdapter = new TweetAdapter(favorites);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(favAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populatefavoriteTimeline(page, false);
            }
        };
        rvTweets.addOnScrollListener(scrollListener);
        populatefavoriteTimeline(0, false);
        return view;
    }

    private void populatefavoriteTimeline(final int page, final boolean refreshing) {
        boolean isOnline = checkForInternet();
        if (!isOnline) {
            Toast.makeText(this.getContext(), "App is offline, cannot show favorite tweets", Toast.LENGTH_LONG).show();
        } else {
            client.getFavoriteTweets(new JsonHttpResponseHandler() {
                boolean cleared = false;

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.i("SAMY-", response.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.i("SAMY-refreshing", String.valueOf(refreshing));
                    favorites.clear();
                    favAdapter.notifyDataSetChanged();
                    try {
                        List<Tweet> newTweets = Tweet.fromJSONArray(response);
                        if (refreshing) {
                            favorites.addAll(0, newTweets);
                            favAdapter.notifyDataSetChanged();
                        } else {
                            favorites.addAll(newTweets);
                            favAdapter.notifyDataSetChanged();
                        }
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
            swipeContainer.setRefreshing(false);
        }
    }
}