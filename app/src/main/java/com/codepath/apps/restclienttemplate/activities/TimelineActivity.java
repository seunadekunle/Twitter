package com.codepath.apps.restclienttemplate.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.fragments.ComposeFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity implements ComposeFragment.ComposeFragmentListener, TweetsAdapter.OpenComposeListener {

    public static final String TAG = TimelineActivity.class.getSimpleName();
    private final int REQUEST_CODE = 20;

    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar pbData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // removes shadow and sets the background color of the action bar
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.extra_light_grey)));

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.twitter_icon);

        // creates new client
        client = TwitterApp.getRestClient(this);

        // finds recycler view
        rvTweets = findViewById(R.id.rvTweets);
        // gets swipelayouy
        swipeRefreshLayout = findViewById(R.id.swipeContainer);
        // get progressbar
        pbData = findViewById(R.id.pbLoading);

        // initialize tweets with adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);

        // recycler view setup: layout manager and attach adapter
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // refreshes the recycler view
                populateHomeTimeline();
//                populateHomeWithPlace();
            }
        });

        populateHomeTimeline();
//        populateHomeWithPlace();
    }


    // populates twitter timeline with data using recyclerview
    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {

            // if api call succeeds
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    // clears out tweets array
                    adapter.clear();
                    // prevents memory issues
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));

                    // notify adapter that list has change
                    adapter.notifyDataSetChanged();
                    // signal that the refreshing is completed
                    swipeRefreshLayout.setRefreshing(false);

                    // hide progress bar
                    pbData.setVisibility(View.INVISIBLE);
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
//
//            // get tweet data from intent using Parcels
//            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
//
//        }
//    }


    // handles selection for the options menu in toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logOut) {
            onLogOutClicked();
        } else {
            goToCompose("Compose", "", 0, false);
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

        // start new intent and clear the stack
        Intent intent = new Intent(TimelineActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // goes to compose fragment
    private void goToCompose(String titleName, String screenName, long id, boolean reply) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeFragment createTweetFragment = ComposeFragment.newInstance(titleName, screenName, id, reply);
        createTweetFragment.show(fm, "fragment_compose");
    }


    @Override
    public void onFinishedComposeFragment(Tweet newTweet) {
        // update recyclerview with new tweet
        tweets.add(0, newTweet);

        // update adapter that item inserted
        adapter.notifyItemInserted(0);

        // list scrolls to recent position
        rvTweets.smoothScrollToPosition(0);
    }

    @Override
    public void goToComposeFragment(String screenName, long id) {
        goToCompose("Reply", screenName, id, true);
    }
}