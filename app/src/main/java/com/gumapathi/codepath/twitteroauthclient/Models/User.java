package com.gumapathi.codepath.twitteroauthclient.Models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gumapathi on 9/26/2017.
 */

public class User {
    public String name;
    public long uid;
    public String screenName;
    public String profileImageURL;

    public static User fromJSON(JSONObject json) throws JSONException {
        User user = new User();

        user.name = json.getString("name");
        user.uid = json.getLong("id");
        user.screenName = json.getString("screen_name");
        user.profileImageURL = json.getString("profile_image_url");

        return user;
    }
}
