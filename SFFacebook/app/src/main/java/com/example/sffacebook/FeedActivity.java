package com.example.sffacebook;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sffacebook.adapters.FeedAdapter;
import com.example.sffacebook.models.Feed;
import com.example.sffacebook.services.FacebookService;
import com.facebook.login.LoginManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    private ImageView profilePictureImageView;
    private TextView userNameTextView;
    private TextView userEmailTextView;
    private RecyclerView feedRecyclerView;
    private ProgressBar progressBar;
    private LinearLayout userProfileLayout;
    private FeedAdapter feedAdapter;
    private List<Feed> feedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        initializeViews();
        setupRecyclerView();
        loadUserProfile();
        loadUserFeed();
    }

    private void initializeViews() {
        profilePictureImageView = findViewById(R.id.profile_picture);
        userNameTextView = findViewById(R.id.user_name);
        userEmailTextView = findViewById(R.id.user_email);
        feedRecyclerView = findViewById(R.id.feed_recyclerview);
        progressBar = findViewById(R.id.progress_bar);
        userProfileLayout = findViewById(R.id.user_profile_layout);
    }

    private void setupRecyclerView() {
        feedList = new ArrayList<>();
        feedAdapter = new FeedAdapter(feedList);
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedRecyclerView.setAdapter(feedAdapter);
    }

    private void loadUserProfile() {
        progressBar.setVisibility(View.VISIBLE);
        
        FacebookService.getUserInfo(new FacebookService.UserCallback() {
            @Override
            public void onSuccess(JsonObject userObject) {
                runOnUiThread(() -> {
                    displayUserProfile(userObject);
                    progressBar.setVisibility(View.GONE);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(FeedActivity.this, "Error loading profile: " + error, 
                        Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    handleLogout();
                });
            }
        });
    }

    private void loadUserFeed() {
        progressBar.setVisibility(View.VISIBLE);
        
        FacebookService.getUserFeed(new FacebookService.FeedCallback() {
            @Override
            public void onSuccess(JsonObject feedObject) {
                runOnUiThread(() -> {
                    displayFeed(feedObject);
                    progressBar.setVisibility(View.GONE);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(FeedActivity.this, "Error loading feed: " + error, 
                        Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    private void displayUserProfile(JsonObject userObject) {
        try {
            // Extract user data
            String name = userObject.has("name") ? userObject.get("name").getAsString() : "N/A";
            String email = userObject.has("email") ? userObject.get("email").getAsString() : "N/A";
            String profilePicUrl = null;

            // Extract profile picture URL
            if (userObject.has("picture")) {
                JsonObject pictureObj = userObject.getAsJsonObject("picture");
                if (pictureObj.has("data")) {
                    JsonObject dataObj = pictureObj.getAsJsonObject("data");
                    if (dataObj.has("url")) {
                        profilePicUrl = dataObj.get("url").getAsString();
                    }
                }
            }

            // Update UI
            userNameTextView.setText("Name: " + name);
            userEmailTextView.setText("Email: " + email);

            // Load profile picture using Glide
            if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                Glide.with(this)
                    .load(profilePicUrl)
                    .centerCrop()
                    .into(profilePictureImageView);
            }

            userProfileLayout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Toast.makeText(this, "Error parsing user data: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void displayFeed(JsonObject feedObject) {
        try {
            feedList.clear();

            if (feedObject.has("data")) {
                JsonArray dataArray = feedObject.getAsJsonArray("data");

                for (int i = 0; i < dataArray.size(); i++) {
                    JsonObject feedItem = dataArray.get(i).getAsJsonObject();
                    Feed feed = new Feed();

                    // Parse feed data
                    if (feedItem.has("id")) feed.setId(feedItem.get("id").getAsString());
                    if (feedItem.has("message")) feed.setMessage(feedItem.get("message").getAsString());
                    if (feedItem.has("story")) feed.setStory(feedItem.get("story").getAsString());
                    if (feedItem.has("created_time")) feed.setCreatedTime(feedItem.get("created_time").getAsString());
                    if (feedItem.has("type")) feed.setType(feedItem.get("type").getAsString());
                    if (feedItem.has("picture")) feed.setPicture(feedItem.get("picture").getAsString());
                    if (feedItem.has("caption")) feed.setCaption(feedItem.get("caption").getAsString());
                    if (feedItem.has("description")) feed.setDescription(feedItem.get("description").getAsString());

                    // Parse author info
                    if (feedItem.has("from")) {
                        JsonObject fromObj = feedItem.getAsJsonObject("from");
                        Feed.From from = new Feed.From();
                        if (fromObj.has("id")) from.setId(fromObj.get("id").getAsString());
                        if (fromObj.has("name")) from.setName(fromObj.get("name").getAsString());
                        feed.setFrom(from);
                    }

                    feedList.add(feed);
                }

                feedAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error parsing feed: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void handleLogout() {
        LoginManager.getInstance().logOut();
        finish();
    }
}
