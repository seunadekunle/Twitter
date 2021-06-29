package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    public static final String TAG = TimelineActivity.class.getSimpleName();
    private final int REQUEST_CODE = 20;

    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // creates new client
        client = TwitterApp.getRestClient(this);

        // find recycler view
        rvTweets = findViewById(R.id.rvTweets);

        // initialize tweets with adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);

        // recycler view setup: layout manager and attach adapter
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        populateHomeTimeline();
    }

    // populates twitter timeline with data using recyclerviews
    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {

            // if api call succeeds
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    // prevents memory issues
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    // notify adapter that list has change
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception");
                    e.printStackTrace();
                }
            }

            // if api call fails
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "onFailure" + response, throwable);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            // get tweet data from intent using Parcels
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            // update recyclerview with new tweet
            tweets.add(0, tweet);

            // update adapter that item inserted
            adapter.notifyItemInserted(0);

            // list scrolls to recent position
            rvTweets.smoothScrollToPosition(0);
        }
    }


    // handles selection for the options menu in toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOut:
                onLogOutClicked();
            default:
                goToCompose();

        }
        return true;
    }

    // inflates layout for toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflates the menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // if the logout button is clicked
    private void onLogOutClicked() {
        client.clearAccessToken();  // forgets who has logged in
        finish();
    }

    // creates intent to go to compose page
    private void goToCompose() {
        Intent i = new Intent(this, ComposeActivity.class);
        startActivityForResult(i, REQUEST_CODE);
    }
}