package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class ComposeActivity extends AppCompatActivity {

    private static final int MAX_TWEET_LENGTH = 280;

    EditText etCompose;
    Button btnTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);

        // set on click listener
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // retrieve text from editText
                String newTweet = etCompose.getText().toString();

                Snackbar snackBar;

                // if tweet is empty
                if (newTweet.isEmpty()) {
                    snackBar = Snackbar.make(view, getString(R.string.too_short_tweet), Snackbar.LENGTH_SHORT);
                    snackBar.show();
                    return;
                }

                // tweet is too long
                if(newTweet.length() > MAX_TWEET_LENGTH){
                    snackBar = Snackbar.make(view, getString(R.string.too_long_tweet), Snackbar.LENGTH_SHORT);
                    snackBar.show();

                    return;
                }
                // make API call to Twitter

            }
        });
    }
}