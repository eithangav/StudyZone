package com.example.studyzone.data.zone;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RateFetcher {

    private RequestQueue _queue;
    private final static String REQUEST_URL = "http://localhost:3000/";

    public class RateResponse {
        public boolean isError;
        public boolean hasSucceeded;

        public RateResponse(boolean isError, boolean hasSucceeded) {
            this.isError = isError;
            this.hasSucceeded = hasSucceeded;
        }
    }

    public interface RateResponseListener {
        public void onResponse(RateResponse response);
    }

    public RateFetcher(Context context) {
        _queue = Volley.newRequestQueue(context);
    }

    private RateResponse createErrorResponse() {
        return new RateResponse(true, false);
    }

    public void dispatchRequest(final int id, final double crowdedRating, final double foodRating,
                                final double priceRating, final String review, final RateResponseListener listener) {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put("id", id);
            postBody.put("crowdedRating", crowdedRating);
            postBody.put("foodRating", foodRating);
            postBody.put("priceRating", priceRating);
            postBody.put("review", review);
        }
        catch (JSONException e) {
            listener.onResponse(createErrorResponse());
            return;
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, REQUEST_URL, postBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            RateResponse res = new RateResponse(false,
                                    response.getString("status").equals("Success"));
                            listener.onResponse(res);
                        }
                        catch (JSONException e) {
                            listener.onResponse(createErrorResponse());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onResponse(createErrorResponse());
            }
        });

        _queue.add(req);
    }
}
