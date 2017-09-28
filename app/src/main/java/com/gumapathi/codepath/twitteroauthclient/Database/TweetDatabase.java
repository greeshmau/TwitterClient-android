package com.gumapathi.codepath.twitteroauthclient.Database;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = TweetDatabase.NAME, version = TweetDatabase.VERSION)
public class TweetDatabase {

    public static final String NAME = "TweetDatabase";

    public static final int VERSION = 1;
}
