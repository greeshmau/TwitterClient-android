package com.gumapathi.codepath.twitteroauthclient.Activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gumapathi.codepath.twitteroauthclient.Adapters.MyFragmentPagerAdapter;
import com.gumapathi.codepath.twitteroauthclient.Fragments.ComposeTweetDialogFragment;
import com.gumapathi.codepath.twitteroauthclient.R;
import com.gumapathi.codepath.twitteroauthclient.TwitterApplication;
import com.gumapathi.codepath.twitteroauthclient.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by gumapathi on 10/1/17.
 */

public class TabbedLayoutActivity extends AppCompatActivity  {
    private int[] imageResId = {
            R.drawable.ic_home_black_24dp,
            R.drawable.like_black};
    ImageView ivProfilePhoto;
    private TwitterClient client;
    ViewPager viewPager;
    ComposeTweetDialogFragment composeTweetDialogFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_layout);
        client = TwitterApplication.getRestClient();

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(),
                TabbedLayoutActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < imageResId.length; i++) {
            tabLayout.getTabAt(i).setIcon(imageResId[i]);
        }
        setupProfileImage();

    }

    private void setupProfileImage() {
        client.getUserProfile(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String profileImageUrl = response.getString("profile_image_url");
                    ivProfilePhoto = (ImageView) findViewById(R.id.ivProfilePhoto);
                    Glide.with(getApplicationContext())
                            .load(profileImageUrl)
                            .bitmapTransform(new RoundedCornersTransformation(getApplicationContext(), 60, 0))
                            .into(ivProfilePhoto);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

    }
}

