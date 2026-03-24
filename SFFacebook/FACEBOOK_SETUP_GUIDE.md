# Facebook Integration Setup Guide

## Overview
This app integrates Facebook SDK to authenticate users, fetch their profile information (name and profile picture), and display their Facebook feeds.

## Features Implemented
✅ Facebook Login Authentication
✅ Fetch User Profile (Name, Email, Profile Picture)
✅ Display User Feed with Posts
✅ Support for Feed Media (Images)
✅ Timestamp formatting
✅ Feed Author Information
✅ Profile Picture Loading with Glide

## Project Structure
```
app/src/main/
├── java/com/example/sffacebook/
│   ├── MainActivity.java                    # Entry point, handles SDK init & navigation
│   ├── FacebookLoginActivity.java           # Login screen with Facebook button
│   ├── FeedActivity.java                    # Main feed display and user profile
│   ├── models/
│   │   ├── User.java                        # User model with profile data
│   │   └── Feed.java                        # Feed/Post model
│   ├── services/
│   │   └── FacebookService.java             # Facebook API service
│   └── adapters/
│       └── FeedAdapter.java                 # RecyclerView adapter for feeds
├── res/layout/
│   ├── activity_main.xml                    # Splash screen
│   ├── activity_facebook_login.xml          # Login page
│   ├── activity_feed.xml                    # Main feed page
│   └── item_feed.xml                        # Feed item template
└── AndroidManifest.xml                      # App configuration

```

## Setup Instructions

### Step 1: Create Facebook App
1. Go to [Facebook Developers](https://developers.facebook.com/)
2. Create a new app → Select "Consumer" as app type
3. Add "Facebook Login" product
4. Go to Settings → Basic and note your:
   - **App ID**
   - **App Secret**
5. Create a **Client Token** from Settings → Client Tokens

### Step 2: Get Your Key Hash (Android)
For development, generate your debug key hash using the provided Python script:

**Run the key hash generator:**
```bash
python get_key_hash.py
```

**Your Debug Key Hash:**
```
38l4a+GKKBjY01oLpjRcFizEf4c=
```

Save this value - you'll need it in the next step!

Alternatively, use Android Studio:
1. Go to **Gradle → Tasks → android → signingReport**
2. Find the SHA1 hash in the output
3. Convert it using online tools or tools included in the project

### Step 3: Configure Your App
1. In Facebook App Dashboard:
   - Go to Facebook Login → Settings
   - Add your key hash
   - Set Valid OAuth Redirect URIs to `https://www.facebook.com/connect/login_success.html`

2. Add your App ID and Client Token to `app/src/main/res/values/strings.xml`:
```xml
<string name="facebook_app_id">YOUR_FACEBOOK_APP_ID</string>
<string name="facebook_client_token">YOUR_FACEBOOK_CLIENT_TOKEN</string>
```

### Step 4: Update AndroidManifest.xml (Already Done)
The manifest includes:
- `facebook_app_id` and `facebook_client_token` meta-data
- Required permissions (INTERNET, ACCESS_NETWORK_STATE)
- Facebook Login activities
- Browser intent filters for OAuth

### Step 5: Build and Run
1. Sync Gradle files in Android Studio
2. Run the app on an Android device or emulator
3. Click "Login with Facebook"
4. Authorize the app
5. View your profile and feeds!

## Dependencies Added
```gradle
// Facebook SDK
implementation("com.facebook.android:facebook-android-sdk:16.3.0")

// Retrofit (for API calls)
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// Glide (for image loading)
implementation("com.github.bumptech.glide:glide:4.16.0")

// RecyclerView
implementation("androidx.recyclerview:recyclerview:1.3.2")
```

## API Permissions Required
Your app uses the only universally valid permission:
- `public_profile` - Access to name, picture, and basic profile information (no additional configuration needed)

## Code Architecture

### MainActivity.java
- Initializes Facebook SDK
- Checks login status
- Routes to Login or Feed activity

### FacebookLoginActivity.java
- Displays login button
- Handles Facebook login callback
- Navigates to FeedActivity on success

### FeedActivity.java
- Fetches user profile using `FacebookService`
- Displays user name and profile picture
- Loads and displays user's feed
- Uses RecyclerView for feed items

### FacebookService.java
- Makes API calls to Facebook Graph API
- `getUserInfo()` - Fetches user details
- `getUserFeed()` - Fetches user's posts
- Uses callbacks for async operations

### FeedAdapter.java
- Binds feed data to RecyclerView items
- Loads feed images using Glide
- Formats timestamps
- Displays author information

## Models

### User.java
- Stores user profile data
- Fields: id, name, email, picture (with nested picture URL), profileLink

### Feed.java
- Represents a Facebook post/feed item
- Fields: id, message, story, created_time, type, picture, link, caption, description, from (author info)

## Troubleshooting

### App crashes on login
- Ensure Facebook App ID and Client Token are correctly set in strings.xml
- Check that your key hash is registered in Facebook App Dashboard
- Verify permissions in AndroidManifest.xml

### Profile picture not loading
- Check internet connection
- Ensure Glide dependency is installed
- Verify user has public profile picture

### Scope Validation Issues
If you see "Invalid Scopes" error:
- The app is configured to use only `public_profile` which is universally valid
- Email data will show as "N/A" when not accessible
- No app review required for public_profile scope
- This is the most stable configuration for Facebook Login

### No access token error
- Ensure user successfully logged in
- Check if access token has expired
- Try logging out and logging in again

## Publishing to Play Store
- Update strings.xml with production Facebook App credentials
- Use production key hash before uploading APK
- Disable debug logging in FacebookService

## Next Steps
- Add logout functionality
- Implement feed pagination (load more posts)
- Add refresh functionality
- Add likes/comments display
- Implement user search
- Add feed filtering by type (photos, videos, etc.)
