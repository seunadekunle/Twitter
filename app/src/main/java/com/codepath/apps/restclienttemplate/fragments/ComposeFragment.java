package com.codepath.apps.restclienttemplate.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComposeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeFragment extends DialogFragment {

    public static final String TAG = "ComposeFragment";
    private static final int MAX_TWEET_LENGTH = 280;

    EditText etCompose;
    Button btnTweet;

    TwitterClient client;
    ComposeFragmentListener listener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "title";

    // defines listener interface with method passing back result
    public interface ComposeFragmentListener {
        void onFinishedComposeFragment(Tweet newTweet);
    }

    public ComposeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ComposeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComposeFragment newInstance(String param1) {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        client = TwitterApp.getRestClient(getActivity().getApplicationContext());

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Compose");
        getDialog().setTitle(title);

        btnTweet = view.findViewById(R.id.btnTweet);
        // Get field from view
        etCompose = view.findViewById(R.id.etCompose);
        // Show soft keyboard automatically and request focus to field
        etCompose.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


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
                Log.d(TAG, "tweet to tweet = " + tweetText);
                client.publishTweet(tweetText, new JsonHttpResponseHandler() {

                    // if call succeeds
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        try {
                            Log.d(TAG, "json received = " + json.toString());
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Tweet " + tweet);

                            // gets data and closes fragment
                            listener.onFinishedComposeFragment(tweet);
                            getDialog().dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // if call fails
                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet " + response, throwable);
                        getDialog().dismiss();
                    }
                });


            }

        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container);
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        if(context instanceof ComposeFragmentListener){
            Log.d(TAG, "context is a ComposeFragListener");
            listener = (ComposeFragmentListener) context;
        }
        else{
            Log.d(TAG, "The context that called this does not implement ComposeFragmentListener!");
        }
    }
}