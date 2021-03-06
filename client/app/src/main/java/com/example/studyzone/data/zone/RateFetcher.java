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

/**
 * a class to update a user's rating on a specific zone in the server using POST request
 */
public class RateFetcher {

    private RequestQueue _queue;
    private String REQUEST_URL = "http://192.168.1.50:3000/zone/";

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

    public void dispatchRequest(final int id, final float crowdedRating, final float foodRating,
                                final float priceRating, final String review, final RateResponseListener listener) {
        this.REQUEST_URL += String.valueOf(id);
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
