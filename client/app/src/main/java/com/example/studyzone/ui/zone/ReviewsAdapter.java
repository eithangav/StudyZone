package com.example.studyzone.ui.zone;

import android.util.Log;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import org.json.JSONArray;
import org.json.JSONException;

public class ReviewsAdapter extends RecyclerView.Adapter {

    private JSONArray reviews;

    public ReviewsAdapter(JSONArray reviews) {
        this.reviews = reviews;
    }

    @Override
    public ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        return new ReviewsViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            String reviewContent = reviews.getJSONObject(position).getString("content");
            ((ReviewsViewHolder)holder).review.setText(reviewContent);
        } catch (JSONException e) {
            Log.e("ReviewsAdapter", "Error reading reviews JSON Array");
        }
    }

    @Override
    public int getItemCount() {
        return reviews.length();
    }

}
