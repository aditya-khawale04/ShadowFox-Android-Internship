# Facebook Integration - Implementation Reference

## Quick Reference Guide

### 1. Making API Calls to Facebook

#### Fetch User Profile
```java
FacebookService.getUserInfo(new FacebookService.UserCallback() {
    @Override
    public void onSuccess(JsonObject userObject) {
        String name = userObject.get("name").getAsString();
        String email = userObject.get("email").getAsString();
        // Extract profile picture URL
        String pictureUrl = userObject
            .getAsJsonObject("picture")
            .getAsJsonObject("data")
            .get("url").getAsString();
    }

    @Override
    public void onError(String error) {
        Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
    }
});
```

#### Fetch User Feed
```java
FacebookService.getUserFeed(new FacebookService.FeedCallback() {
    @Override
    public void onSuccess(JsonObject feedObject) {
        JsonArray feedArray = feedObject.getAsJsonArray("data");
        // Process feed items
        for (int i = 0; i < feedArray.size(); i++) {
            JsonObject feedItem = feedArray.get(i).getAsJsonObject();
            // Parse and display
        }
    }

    @Override
    public void onError(String error) {
        Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
    }
});
```

### 2. Image Loading with Glide

```java
// Load profile picture
Glide.with(context)
    .load(profilePictureUrl)
    .centerCrop()
    .into(profileImageView);

// Load feed image with placeholder
Glide.with(context)
    .load(feedImageUrl)
    .placeholder(R.drawable.ic_launcher_background)
    .error(R.drawable.ic_launcher_background)
    .centerCrop()
    .into(feedImageView);
```

### 3. RecyclerView Setup

```java
// Initialize adapter and RecyclerView
List<Feed> feedList = new ArrayList<>();
FeedAdapter adapter = new FeedAdapter(feedList);
RecyclerView recyclerView = findViewById(R.id.feed_recyclerview);
recyclerView.setLayoutManager(new LinearLayoutManager(context));
recyclerView.setAdapter(adapter);

// Update data
feedList.clear();
feedList.addAll(newFeeds);
adapter.notifyDataSetChanged();

// Observe scroll for pagination
recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int visibleItemCount = manager.getChildCount();
        int totalItemCount = manager.getItemCount();
        int firstVisibleItem = manager.findFirstVisibleItemPosition();
        
        if (visibleItemCount + firstVisibleItem >= totalItemCount - 5) {
            // Load more items
            loadMoreFeeds();
        }
    }
});
```

### 4. Facebook Login

```java
// Initialize CallbackManager
CallbackManager callbackManager = CallbackManager.Factory.create();

// Perform login - using only public_profile (universally valid scope)
LoginManager.getInstance().logInWithReadPermissions(
    activity,
    Arrays.asList("public_profile")
);

// Register callback
LoginManager.getInstance().registerCallback(
    callbackManager,
    new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            // Navigate to feed
            startActivity(new Intent(context, FeedActivity.class));
        }

        @Override
        public void onCancel() {
            Toast.makeText(context, "Login Cancelled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(FacebookException exception) {
            Toast.makeText(context, "Error: " + exception.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }
    }
);

// Handle result
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    callbackManager.onActivityResult(requestCode, resultCode, data);
    super.onActivityResult(requestCode, resultCode, data);
}
```

### 5. Check Login Status

```java
// Check if user is logged in
AccessToken accessToken = AccessToken.getCurrentAccessToken();
boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

if (isLoggedIn) {
    // User is logged in
}

// Get access token
String token = AccessToken.getCurrentAccessToken().getToken();
```

## Data Models

### User Object Structure
```json
{
  "id": "123456789",
  "name": "John Doe",
  "email": "john@example.com",
  "picture": {
    "data": {
      "url": "https://...jpg",
      "height": 400,
      "width": 400
    }
  },
  "link": "https://facebook.com/johndoe"
}
```

### Feed Item Structure
```json
{
  "id": "123456789_987654321",
  "message": "Post content here",
  "story": "John Doe shared a link",
  "created_time": "2024-01-15T10:30:00+0000",
  "type": "status",
  "picture": "https://...jpg",
  "caption": "Link caption",
  "description": "Link description",
  "from": {
    "id": "123456789",
    "name": "John Doe"
  }
}
```

## Common Customizations

### Add Logout Button
```xml
<Button
    android:id="@+id/logout_button"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Logout" />
```

```java
logoutButton.setOnClickListener(v -> {
    LoginManager.getInstance().logOut();
    startActivity(new Intent(context, FacebookLoginActivity.class));
    finish();
});
```

### Refresh Feed
```java
private void refreshFeed() {
    progressBar.setVisibility(View.VISIBLE);
    loadUserFeed();
}

refreshLayout.setOnRefreshListener(() -> {
    refreshFeed();
    refreshLayout.setRefreshing(false);
});
```

### Add Pagination
```java
private int currentPage = 0;

private void loadMoreFeeds() {
    String accessToken = AccessToken.getCurrentAccessToken().getToken();
    String url = "https://graph.facebook.com/v18.0/me/feed?fields=...&limit=25&after=" + paginationCursor;
    // Make API call
}
```

## API Fields Reference

### User Fields
- `id` - User ID
- `name` - User's name
- `email` - User's email
- `picture{url,height,width}` - Profile picture
- `link` - Facebook profile URL
- `cover` - Cover photo
- `birthday` - Date of birth
- `hometown` - Hometown info
- `location` - Current city

### Feed Fields
- `id` - Post ID
- `message` - Post content
- `story` - Story text (for shared posts)
- `created_time` - Post creation time
- `updated_time` - Last update time
- `type` - Post type (status, link, video, etc.)
- `picture` - Associated image
- `link` - Link in post
- `caption` - Link caption
- `description` - Link description
- `from{id,name}` - Author info
- `likes.summary(total_count)` - Like count
- `comments.summary(total_count)` - Comment count
- `shares` - Share count

## Error Handling Best Practices

```java
// Always check for null before accessing
if (userObject != null && userObject.has("name")) {
    String name = userObject.get("name").getAsString();
}

// Handle network errors gracefully
try {
    // Make API call
} catch (IOException e) {
    // Network error
    callback.onError("Network error: " + e.getMessage());
} catch (JsonSyntaxException e) {
    // JSON parsing error
    callback.onError("Parse error: " + e.getMessage());
}

// Display user-friendly error messages
if (error != null) {
    if (error.contains("Network")) {
        Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
    } else if (error.contains("Permission")) {
        Toast.makeText(context, "Please grant required permissions", Toast.LENGTH_SHORT).show();
    } else {
        Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
    }
}
```

## Performance Tips

1. **Load images progressively** - Use placeholder images while loading
2. **Implement pagination** - Load feeds in batches of 25
3. **Cache access tokens** - Store locally for session
4. **Optimize API calls** - Only request needed fields
5. **Use threading** - All API calls run on background threads
6. **Implement view recycling** - RecyclerView automatically handles this

## Security Considerations

1. **Never log tokens** - Don't print access tokens to logs
2. **Use HTTPS** - All Facebook API calls use HTTPS
3. **Validate URLs** - Verify image and link URLs before loading
4. **Permissions** - Only request necessary permissions
5. **Token expiration** - Always check token validity
6. **Obfuscate app** - Use ProGuard for release builds
