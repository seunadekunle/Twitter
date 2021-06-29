package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
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

        // binds ui elements to variables
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivProfileImage = itemView.findViewById(R.id.ivProfile);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
        }

        // fills ul elements with data
        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            tvTimeStamp.setText(getRelativeTimeAgo(tweet.createdAt));
            Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);

            if (tweet.mediaUrl != "")
                Glide.with(context).load(tweet.mediaUrl).into(ivMedia);
        }

        // helper function to return relative timestamp
        // gotten from https://gist.github.com/nesquena/f786232f5ef72f6e10a7
        public String getRelativeTimeAgo(String rawJsonDate) {
            String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
            SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
            sf.setLenient(true);

            String relativeDate = "";
            try {
                long dateMillis = sf.parse(rawJsonDate).getTime();
                relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return relativeDate;
        }
    }


}
