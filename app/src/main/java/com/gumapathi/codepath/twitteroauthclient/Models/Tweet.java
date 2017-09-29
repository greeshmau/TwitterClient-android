package com.gumapathi.codepath.twitteroauthclient.Models;

import com.gumapathi.codepath.twitteroauthclient.Database.TweetDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

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
        tweet.user = User.fromJSON(json.getJSONObject("user"));

        tweet.save();
        return tweet;
    }
}
