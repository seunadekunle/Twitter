package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {
    public String body;
    public String createdAt;
    public User user;
    public String mediaUrl;

    // empty constructor needer for Parceler library
    public Tweet() {

    }

    // returns Tweet object from function
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {

        String mediaUrl = "";
        JSONObject jsonObject1 = jsonObject.getJSONObject("entities");

        if (!jsonObject1.has("media")) {
            mediaUrl = "";
        }
        else {
            mediaUrl = String.valueOf(jsonObject1.getJSONArray("media").getJSONObject(0).getString("media_url"));
            mediaUrl = mediaUrl.substring(7);
            mediaUrl = "https://" + mediaUrl;
        }

        Log.i("entities", mediaUrl);
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.mediaUrl = mediaUrl;

        return tweet;
    }


    // returns a list of tweets created from a JSONArray
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }

        return tweets;
    }
}
