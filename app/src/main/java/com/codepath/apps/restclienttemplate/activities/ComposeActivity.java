package com.codepath.apps.restclienttemplate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = ComposeActivity.class.getSimpleName();
    private static final int MAX_TWEET_LENGTH = 280;

    EditText etCompose;
    Button btnTweet;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        getSupportActionBar().setElevation(0);

        client = TwitterApp.getRestClient(this);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);

        // set on click listener
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // retrieve text from editText
                String tweetText = etCompose.getText().toString();

                Snackbar snackBar;

                // if tweet is empty
                if (tweetText.isEmpty()) {
                    snackBar = Snackbar.make(view, getString(R.string.too_short_tweet), Snackbar.LENGTH_SHORT);
                    snackBar.show();
                    return;
                }

                // if tweet is too long
                if (tweetText.length() > MAX_TWEET_LENGTH) {
                    snackBar = Snackbar.make(view, getString(R.string.too_long_tweet), Snackbar.LENGTH_SHORT);
                    snackBar.show();
                    return;
                }

                // make API call to Twitter
                client.publishTweet(tweetText, new JsonHttpResponseHandler() {

                    // if call succeeds
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Tweet " + tweet);

                            // create new intent, put data, and set the result for it
                            Intent i = new Intent();
                            i.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, i);

                            // close activity
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // if call fails
                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet "+statusCode, throwable);
                    }
                });
            }
        });
    }
}