package com.example.sffacebook;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize Facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check if user is already logged in
        navigateBasedOnLoginStatus();
    }

    private void navigateBasedOnLoginStatus() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        
        if (accessToken != null && !accessToken.isExpired()) {
            // User is already logged in, proceed to Feed
            Intent intent = new Intent(MainActivity.this, FeedActivity.class);
            startActivity(intent);
            finish();
        } else {
            // User needs to login
            Intent intent = new Intent(MainActivity.this, FacebookLoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
