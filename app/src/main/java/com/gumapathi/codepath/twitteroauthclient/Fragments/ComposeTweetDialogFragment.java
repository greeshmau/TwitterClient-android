package com.gumapathi.codepath.twitteroauthclient.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
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

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * Created by gumapathi on 9/20/2017.
 */

public class ComposeTweetDialogFragment extends DialogFragment implements View.OnClickListener {
    EditText etTweet;
    Button btnPostTweet;
    TextView tvCharCount;
    private TwitterClient client;

    public interface ComposeTweetDialogListener {
        void onFinishComposeTweetDialog(Bundle bundle);
    }

    public ComposeTweetDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ComposeTweetDialogFragment newInstance(String title) {
        ComposeTweetDialogFragment frag = new ComposeTweetDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        client = TwitterApplication.getRestClient();
        return inflater.inflate(R.layout.compose_tweet, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etTweet = (EditText) view.findViewById(R.id.etTweet);
        tvCharCount = (TextView) view.findViewById(R.id.tvCharCount);
        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCharCount.setText(String.valueOf(140 - s.length()));
            }
        });
        btnPostTweet = (Button) view.findViewById(R.id.btnPostTweet);
        btnPostTweet.setOnClickListener(this);
        etTweet.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }


    private void postTweetToTwitter(String tweet) {
        final Bundle bundle = new Bundle();
        final ComposeTweetDialogListener listener = (ComposeTweetDialogListener) getActivity();

        try {
            client.postTweet(tweet, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        Tweet tweet = Tweet.fromJSON(response);
                        Log.i("SAMY-succ1", response.toString());
                        bundle.putParcelable("PostedTweet", Parcels.wrap(tweet));
                        Log.i("SAMY", "parceling tweet " + tweet.getBody());
                        Log.i("SAMY", "set resok" + String.valueOf(RESULT_OK));
                        Log.i("SAMY", "going back");
                        listener.onFinishComposeTweetDialog(bundle);
                        dismiss();
                    } catch (Exception e) {
                        Log.i("SAMY-exec1", e.toString());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    try {
                        Log.i("SAMY-succ2", response.toString());
                        dismiss();
                    } catch (Exception e) {
                        Log.i("SAMY-exec1", e.toString());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.i("SAMY-err1", errorResponse.toString());
                    throwable.printStackTrace();
                    dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.i("SAMY-err2", errorResponse.toString());
                    throwable.printStackTrace();
                    dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.i("SAMY-err3", responseString);
                    throwable.printStackTrace();
                    dismiss();
                }
            });
        }
        catch(Exception e) {
            Log.i("SAMY-execmaster", e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
                String tweet = etTweet.getText().toString();
                Log.i("SAMY-posting", tweet);
                postTweetToTwitter(tweet);
    }
}
