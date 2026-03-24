package com.example.sffacebook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

public class FacebookLoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);

        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.facebook_login_button);

        // Check if user is already logged in
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            navigateToFeedActivity();
        }

        loginButton.setOnClickListener(v -> performLogin());
    }

    private void performLogin() {
        LoginManager.getInstance().logInWithReadPermissions(
            this,
            Arrays.asList("public_profile")
        );

        LoginManager.getInstance().registerCallback(
            callbackManager,
            new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Toast.makeText(FacebookLoginActivity.this, 
                        "Login Successful!", 
                        Toast.LENGTH_SHORT).show();
                    navigateToFeedActivity();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(FacebookLoginActivity.this, 
                        "Login Cancelled", 
                        Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException exception) {
                    Toast.makeText(FacebookLoginActivity.this, 
                        "Login Error: " + exception.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                    exception.printStackTrace();
                }
            }
        );
    }

    private void navigateToFeedActivity() {
        Intent intent = new Intent(FacebookLoginActivity.this, FeedActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
