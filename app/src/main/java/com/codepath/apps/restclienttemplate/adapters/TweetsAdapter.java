package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.activities.TimelineActivity;
import com.codepath.apps.restclienttemplate.fragments.ComposeFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final String TAG = "TweetsAdapter";

    Context context;
    List<Tweet> tweets;
    TwitterClient client;
    OpenComposeListener listener;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
        this.client = TwitterApp.getRestClient(this.context);
        listener = (OpenComposeListener) this.context;
    }

    // interface for using reply button
    public interface OpenComposeListener{
        void goToComposeFragment(String screenName, long id);
    }

    // For each row inflate layout and create ViewHolder
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Log.i("test", "createdView");
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Pass in context and list of tweets
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        // get data at position
        Tweet tweet = tweets.get(position);

        // bind data to ui elements
        holder.bind(tweet);
    }

    // Returns item count
    @Override
    public int getItemCount() {
        return tweets.size();
    }


    // clears tweet array and notifies adapter
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // define viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        ImageView ivMedia;

        TextView tvBody;
        TextView tvScreenName;
        TextView tvTimeStamp;
        TextView retweetCount;
        TextView likeCount;

        ImageButton replyBtn;
        ImageButton retweetBtn;
        ImageButton likeBtn;

        // binds ui elements to variables
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivProfileImage = itemView.findViewById(R.id.ivProfile);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            replyBtn = itemView.findViewById(R.id.replyBtn);
            retweetBtn = itemView.findViewById(R.id.retweetBtn);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            retweetCount = itemView.findViewById(R.id.retweetCount);
            likeCount = itemView.findViewById(R.id.likeCount);
        }

        // fills ul elements with data
        public void bind(Tweet tweet) {
            Glide.with(context).load(tweet.getUser().profileImageUrl)
                    .centerCrop()
                    .transform(new RoundedCornersTransformation(100, 0))
                    .into(ivProfileImage);

            tvBody.setText(tweet.getBody());
            tvScreenName.setText(tweet.getUser().name);
            tvTimeStamp.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
            retweetCount.setText(shortenNum(tweet.getNumRetweets()));
            likeCount.setText(shortenNum(tweet.getNumLikes()));

            // if tweet media url is valid
            if (!tweet.getMediaUrl().equals(Tweet.NO_MEDIA)){
                Glide.with(context)
                        .load(tweet.getMediaUrl())
                        .override(1000, 700)
                        .centerCrop()
                        .transform(new RoundedCornersTransformation(40, 0))
                        .into(ivMedia);

            }

            // click listener for bottom buttons
            replyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("tag", "reply clicked");
                    // calls interface implemented in main activity
                    listener.goToComposeFragment(tweet.getUser().screenName, tweet.getId());
                }
            });

            // change state of retweet button based if tweet is like or not
            if (tweet.isRetweeted()){
                retweetBtn.setSelected(true);
            }

            retweetBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // if retweet button is selected
                    if (retweetBtn.isSelected()) {
                        retweetBtn.setSelected(false);

                        // call remove retweet api endpoint
                        client.removeRetweet(tweet.getId(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {

                                if (tweet.getNumRetweets() >= 0) {
                                    tweet.setNumRetweets(tweet.getNumRetweets() - 1);
                                    retweetCount.setText(shortenNum(tweet.getNumRetweets()));
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "onFailure to retweet " + statusCode, throwable);
                            }
                        });
                    }

                    // if retweet button is unselected
                    else {
                        retweetBtn.setSelected(true);

                        // call add retweet api endpoint
                        client.addRetweet(tweet.getId(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {

                                // update retweet count ui
                                tweet.setNumRetweets(tweet.getNumRetweets() + 1);
                                retweetCount.setText(shortenNum(tweet.getNumRetweets()));
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "onFailure to retweet " + statusCode, throwable);
                            }
                        });
                    }
                }
            });

            // change state of like button based if tweet is like or not
            if (tweet.isLiked()){
                likeBtn.setSelected(true);
            }

            likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("tag", "reply clicked");

                    // if like button is selected
                    if (likeBtn.isSelected()) {
                        likeBtn.setSelected(false);

                        client.removeLikes(tweet.getId(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                tweet.setNumLikes(tweet.getNumLikes() - 1);
                                likeCount.setText(shortenNum(tweet.getNumLikes()));
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "onFailure to remove like " + statusCode, throwable);
                            }
                        });
                    }
                    // if like button isn't selected
                    else {
                        likeBtn.setSelected(true);

                        client.addLikes(tweet.getId(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                // update likeCount ui
                                tweet.setNumLikes(tweet.getNumLikes() + 1);
                                likeCount.setText(shortenNum(tweet.getNumLikes()));
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "onFailure to like tweet " + statusCode, throwable);
                            }
                        });
                    }
                }
            });
        }


        // helper function to return relative timestamp
        // gotten from https://gist.github.com/nesquena/f786232f5ef72f6e10a7
        public String getRelativeTimeAgo(String rawJsonDate) {
            String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
            SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
            sf.setLenient(true);

            try {
                long time = sf.parse(rawJsonDate).getTime();
                long now = System.currentTimeMillis();

                final long diff = now - time;
                if (diff < MINUTE_MILLIS) {
                    return "just now";
                } else if (diff < 2 * MINUTE_MILLIS) {
                    return "a minute ago";
                } else if (diff < 50 * MINUTE_MILLIS) {
                    return diff / MINUTE_MILLIS + " m";
                } else if (diff < 90 * MINUTE_MILLIS) {
                    return "an hour ago";
                } else if (diff < 24 * HOUR_MILLIS) {
                    return diff / HOUR_MILLIS + " h";
                } else if (diff < 48 * HOUR_MILLIS) {
                    return "yesterday";
                } else {
                    return diff / DAY_MILLIS + " d";
                }
            } catch (ParseException e) {
                Log.i("TAG", "getRelativeTimeAgo failed");
                e.printStackTrace();
            }

            return "";
        }
    }

    // helper function to shorten numbers
    // gotten from https://stackoverflow.com/questions/41859525/how-to-go-about-formatting-1200-to-1-2k-in-android-studio
    public String shortenNum(int longNum) {
        String numberString = "";
        if (Math.abs(longNum / 1000000) > 1) {
            numberString = String.valueOf(longNum / 1000000) + "M";

        } else if (Math.abs(longNum / 1000) > 1) {
            numberString = String.valueOf(longNum / 1000) + "K";

        } else {
            numberString = String.valueOf(longNum);

        }

        return numberString;
    }

}
