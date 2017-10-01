package com.gumapathi.codepath.twitteroauthclient;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "a9AblNyQfKuhgVP0VJeUYrBd9";
	public static final String REST_CONSUMER_SECRET = "EvVot2cvXVY3fGdssgDwCGZbvO8zFelCKl7WyDU8uCREyi38os";
	public static final int TWEET_COUNT = 100;

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}

	public void getHomeTimeline(long sinceID, long maxID, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", TWEET_COUNT);
		if(sinceID != 0)
			params.put("since_id", sinceID);
		if(maxID != 0)
			params.put("max_id", maxID);
		client.get(apiUrl, params, handler);
	}

	public void postTweet(String status, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("display_coordinates", "false");
		params.put("status", status);
		client.post(apiUrl, params, handler);
	}

	public void getFavoriteTweets(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/favorites/list.json");
		RequestParams params = new RequestParams();
		params.put("count", TWEET_COUNT);
		client.get(apiUrl, params, handler);
	}

	public void favoriteTweet(long tweet_id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/favorites/create.json");
		RequestParams params = new RequestParams();
		params.put("id", tweet_id);
		client.post(apiUrl, params, handler);
	}

	public void getUserProfile(AsyncHttpResponseHandler  repsonseHandler){
		String apiUrl = getApiUrl("account/verify_credentials.json");
		//specify the params
		RequestParams params = new RequestParams();
		getClient().get(apiUrl, null, repsonseHandler);
	}
}
