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
    private String body;
    private String createdAt;
    private User user;
    private String mediaUrl;
    private long id;
    private int numLikes;
    private int numRetweets;
    private boolean retweeted;
    private boolean liked;

    public static String NO_MEDIA = "NO_MEDIA";

    // empty constructor needed for Parceler library
    public Tweet() {

    }

    // returns Tweet object from function
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {

        String mediaUrl = "";
        JSONObject jsonObjectEntities = jsonObject.getJSONObject("entities");

        if (!(jsonObjectEntities.has("media"))) {
            mediaUrl = NO_MEDIA;
        } else {
            mediaUrl = String.valueOf(jsonObjectEntities.getJSONArray("media").getJSONObject(0).getString("media_url_https"));
        }

        Log.i("entities", mediaUrl);
        Tweet tweet = new Tweet();

        tweet.id =jsonObject.getLong("id");
        if(jsonObject.has("full_text")) {
            tweet.body = jsonObject.getString("full_text");
        }
        else{
            tweet.body = jsonObject.getString("text");
        }

        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.mediaUrl = mediaUrl;
        tweet.numLikes = jsonObject.getInt("favorite_count");;
        tweet.numRetweets = jsonObject.getInt("retweet_count");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.liked = jsonObject.getBoolean("favorited");
//        Log.i("likes", String.valueOf(tweet.numLikes));
//        Log.i("retweets", String.valueOf(tweet.numRetweets));

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

    // getters
    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public long getId() {
        return id;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public int getNumRetweets() {
        return numRetweets;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public void setId(long id) {
        this.id = id;
    }

    // setters
    public void setNumRetweets(int newCount) {
        numRetweets = newCount;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    // returns a list of tweets created from a JSONArray
    public static List<Tweet> fromPlaceData() {
        List<Tweet> tweets = new ArrayList<>();

        User testUser = new User();
        testUser.name = "Seun";
        testUser.profileImageUrl = "https://pbs.twimg.com/profile_images/1404334078388670466/DgO3WL4S_400x400.jpg";
        testUser.screenName = "@bigmanseun";

        Tweet test = new Tweet();
        test.createdAt = "Wed Oct 10 20:19:24 +0000 2018";
        test.mediaUrl = "";
        test.user = testUser;

        test.body = "To make room for more expression, we will now count all emojis as equal—including those with gender\u200D\u200D\u200D \u200D\u200Dand skin t… https://t.co/MkGjXf9aXm";
        for (int i = 0; i < 10; i++) {
            tweets.add(test);
        }

        return tweets;
    }
}
