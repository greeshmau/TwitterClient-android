package com.gumapathi.codepath.twitteroauthclient.Models;

import com.gumapathi.codepath.twitteroauthclient.Database.TweetDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gumapathi on 9/26/2017.
 */
@Table(database = TweetDatabase.class)
@Parcel(analyze={Tweet.class})
public class Tweet extends BaseModel{
    @Column
    @PrimaryKey
    long uid;

    @Column
    String body;

    @Column
    @ForeignKey(saveForeignKeyModel = false)
    User user;

    @Column
    String createdAt;

    public Tweet() {
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public static Tweet fromJSON(JSONObject json) throws JSONException {
        Tweet tweet = new Tweet();

        tweet.body = json.getString("text");
        tweet.uid = json.getLong("id");
        tweet.createdAt = json.getString("created_at");
        tweet.user = User.fromJSONWithDBSave(json.getJSONObject("user"));

        tweet.save();
        return tweet;
    }

    public static List<Tweet> fromJSONArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> allTweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length();i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            Tweet tweet = new Tweet();

            tweet.body = json.getString("text");
            tweet.uid = json.getLong("id");
            tweet.createdAt = json.getString("created_at");
            tweet.user = User.fromJSON(json.getJSONObject("user"));
            allTweets.add(tweet);
        }

        FlowManager.getDatabase(TweetDatabase.class).executeTransaction(FastStoreModelTransaction
                .insertBuilder(FlowManager.getModelAdapter(Tweet.class))
                .addAll(allTweets)
                .build());

        return allTweets;
    }
    /*
    * Hi there, I am using FastStoreModelTransaction to store list of Tweets (getModelAdapter(Tweet.class)) as suggested here - https://agrosner.gitbooks.io/dbflow/content/StoringData.html. But this doesn't store the User object in the DB.
    */
}
