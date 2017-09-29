package com.gumapathi.codepath.twitteroauthclient.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gumapathi.codepath.twitteroauthclient.Models.Tweet;
import com.gumapathi.codepath.twitteroauthclient.R;
import com.gumapathi.codepath.twitteroauthclient.TwitterApplication;
import com.gumapathi.codepath.twitteroauthclient.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class PostTweetActivity extends AppCompatActivity {
    private TwitterClient client;
    EditText etTweet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_tweet);
        client = TwitterApplication.getRestClient();
    }

    public void postTweet(View view) {
        etTweet = (EditText) findViewById(R.id.etTweet);
        String tweet = etTweet.getText().toString();
        Log.i("SAMY-posting", tweet);
        postTweettoTwitter(tweet);
    }

    private void postTweettoTwitter(String tweet) {
        try {
            Toast.makeText(this,"Posting tweet",Toast.LENGTH_SHORT).show();
            final Intent intent = new Intent();
            client.postTweet(tweet, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        Tweet tweet = Tweet.fromJSON(response);
                        Log.i("SAMY-succ1", response.toString());
                        intent.putExtra("PostedTweet", Parcels.wrap(tweet));
                        Log.i("SAMY", "parceling tweet " + tweet.getBody());
                        intent.putExtra("test", "test");
                        setResult(RESULT_OK, intent);
                        Log.i("SAMY", "set resok" + String.valueOf(RESULT_OK));
                        Log.i("SAMY", "going back");
                        finish();
                    } catch (Exception e) {
                        Log.i("SAMY-exec1", e.toString());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    try {
                        Log.i("SAMY-succ2", response.toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    } catch (Exception e) {
                        Log.i("SAMY-exec1", e.toString());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.i("SAMY-err1", errorResponse.toString());
                    throwable.printStackTrace();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.i("SAMY-err2", errorResponse.toString());
                    throwable.printStackTrace();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.i("SAMY-err3", responseString);
                    throwable.printStackTrace();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
            });
        }
        catch(Exception e) {
            Log.i("SAMY-execmaster", e.toString());
            e.printStackTrace();
        }
    }
}
