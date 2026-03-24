package com.example.sffacebook.services;

import com.facebook.AccessToken;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FacebookService {

    private static final String FACEBOOK_API_BASE = "https://graph.facebook.com/v18.0";
    private static final String FIELDS_USER = "id,name,email,picture.width(400).height(400),link";
    private static final String FIELDS_FEED = "id,message,story,created_time,type,picture,link,caption,description,from";

    public interface UserCallback {
        void onSuccess(JsonObject userObject);
        void onError(String error);
    }

    public interface FeedCallback {
        void onSuccess(JsonObject feedObject);
        void onError(String error);
    }

    /**
     * Fetch current user information from Facebook
     */
    public static void getUserInfo(UserCallback callback) {
        String accessToken = AccessToken.getCurrentAccessToken() != null ? 
            AccessToken.getCurrentAccessToken().getToken() : null;

        if (accessToken == null) {
            callback.onError("No access token available");
            return;
        }

        new Thread(() -> {
            try {
                String url = FACEBOOK_API_BASE + "/me?fields=" + FIELDS_USER + "&access_token=" + accessToken;
                JsonObject result = makeApiCall(url);
                
                if (result != null) {
                    callback.onSuccess(result);
                } else {
                    callback.onError("Failed to fetch user data");
                }
            } catch (Exception e) {
                callback.onError("Exception: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Fetch user's feed including posts
     */
    public static void getUserFeed(FeedCallback callback) {
        String accessToken = AccessToken.getCurrentAccessToken() != null ? 
            AccessToken.getCurrentAccessToken().getToken() : null;

        if (accessToken == null) {
            callback.onError("No access token available");
            return;
        }

        new Thread(() -> {
            try {
                String url = FACEBOOK_API_BASE + "/me/feed?fields=" + FIELDS_FEED + "&limit=25&access_token=" + accessToken;
                JsonObject result = makeApiCall(url);
                
                if (result != null) {
                    callback.onSuccess(result);
                } else {
                    callback.onError("Failed to fetch feed");
                }
            } catch (Exception e) {
                callback.onError("Exception: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Fetch specific user posts with full details
     */
    public static void getPostDetails(String postId, FeedCallback callback) {
        String accessToken = AccessToken.getCurrentAccessToken() != null ? 
            AccessToken.getCurrentAccessToken().getToken() : null;

        if (accessToken == null) {
            callback.onError("No access token available");
            return;
        }

        new Thread(() -> {
            try {
                String url = FACEBOOK_API_BASE + "/" + postId + "?fields=" + FIELDS_FEED + "&access_token=" + accessToken;
                JsonObject result = makeApiCall(url);
                
                if (result != null) {
                    callback.onSuccess(result);
                } else {
                    callback.onError("Failed to fetch post details");
                }
            } catch (Exception e) {
                callback.onError("Exception: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Make HTTP API call to Facebook Graph API
     */
    private static JsonObject makeApiCall(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return JsonParser.parseString(response.toString()).getAsJsonObject();
            } else {
                // Handle error response
                BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream())
                );
                StringBuilder errorResponse = new StringBuilder();
                String line;
                
                while ((line = errorReader.readLine()) != null) {
                    errorResponse.append(line);
                }
                errorReader.close();

                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
