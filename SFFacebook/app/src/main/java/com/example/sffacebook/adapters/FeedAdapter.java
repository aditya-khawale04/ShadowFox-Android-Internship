package com.example.sffacebook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sffacebook.R;
import com.example.sffacebook.models.Feed;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private List<Feed> feedList;

    public FeedAdapter(List<Feed> feedList) {
        this.feedList = feedList;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_feed, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        Feed feed = feedList.get(position);
        holder.bind(feed);
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder {

        private final TextView authorNameTextView;
        private final TextView contentTextView;
        private final TextView captionTextView;
        private final TextView timestampTextView;
        private final ImageView feedImageView;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            authorNameTextView = itemView.findViewById(R.id.feed_author_name);
            contentTextView = itemView.findViewById(R.id.feed_content);
            captionTextView = itemView.findViewById(R.id.feed_caption);
            timestampTextView = itemView.findViewById(R.id.feed_timestamp);
            feedImageView = itemView.findViewById(R.id.feed_image);
        }

        public void bind(Feed feed) {
            // Set author name
            if (feed.getFrom() != null && feed.getFrom().getName() != null) {
                authorNameTextView.setText(feed.getFrom().getName());
            } else {
                authorNameTextView.setText("Unknown Author");
            }

            // Set content (message or story)
            String content = feed.getContent();
            if (content != null && !content.isEmpty()) {
                contentTextView.setText(content);
                contentTextView.setVisibility(View.VISIBLE);
            } else {
                contentTextView.setVisibility(View.GONE);
            }

            // Set caption
            if (feed.getCaption() != null && !feed.getCaption().isEmpty()) {
                captionTextView.setText(feed.getCaption());
                captionTextView.setVisibility(View.VISIBLE);
            } else {
                captionTextView.setVisibility(View.GONE);
            }

            // Set timestamp
            if (feed.getCreatedTime() != null) {
                timestampTextView.setText(formatTime(feed.getCreatedTime()));
            }

            // Load feed image
            if (feed.getPicture() != null && !feed.getPicture().isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(feed.getPicture())
                    .centerCrop()
                    .into(feedImageView);
                feedImageView.setVisibility(View.VISIBLE);
            } else {
                feedImageView.setVisibility(View.GONE);
            }
        }

        private String formatTime(String isoTime) {
            try {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
                Date date = isoFormat.parse(isoTime.replace("Z", "+0000"));
                
                SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
                return displayFormat.format(date);
            } catch (Exception e) {
                return isoTime;
            }
        }
    }
}
