package com.gumapathi.codepath.twitteroauthclient.Models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gumapathi on 9/26/2017.
 */

public class Tweet {
    public String body;
    public long uid;
    public User user;
    public String createdAt;

    public static Tweet fromJSON(JSONObject json) throws JSONException {
        Tweet tweet = new Tweet();

        tweet.body = json.getString("text");
        tweet.uid = json.getLong("id");
        tweet.createdAt = json.getString("created_at");
        tweet.user = User.fromJSON(json.getJSONObject("user"));

        return tweet;
    }
}
