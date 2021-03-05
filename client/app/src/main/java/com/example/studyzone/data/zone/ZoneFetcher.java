package com.example.studyzone.data.zone;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ZoneFetcher {

    private RequestQueue _queue;
    private String REQUEST_URL = "http://localhost:3000/zone/";

    public class ZoneResponse {
        public boolean isError;
        public double latitude;
        public double longitude;
        public String name;
        public String description;
        public double crowdedRating;
        public double foodRating;
        public double priceRating;
        public int numRatings;
        public JSONArray reviews;

        public ZoneResponse(boolean isError, double latitude, double longitude,
                            String name, String description, double crowdedRating, double foodRating,
                            double priceRating, int numRatings, JSONArray reviews){
            this.isError = isError;
            this.latitude = latitude;
            this.longitude = longitude;
            this.name = name;
            this.description = description;
            this.crowdedRating = crowdedRating;
            this.foodRating = foodRating;
            this.priceRating = priceRating;
            this.numRatings = numRatings;
            this.reviews = reviews;

        }
    }

    public interface ZoneResponseListener {
        public void onResponse(ZoneResponse response);
    }

    public ZoneFetcher(Context context) {
        this._queue = Volley.newRequestQueue(context);
    }

    private ZoneResponse createErrorResponse() {
        return new ZoneResponse(true, 0, 0, null, null,
                0, 0, 0, 0, null);
    }

    public void dispatchRequest(final int id, final ZoneResponseListener listener){
        this.REQUEST_URL += String.valueOf(id);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, REQUEST_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ZoneResponse res = new ZoneResponse(false,
                                    response.getDouble("latitude"),
                                    response.getDouble("longitude"),
                                    response.getString("name"),
                                    response.getString("description"),
                                    response.getDouble("crowdedRating"),
                                    response.getDouble("foodRating"),
                                    response.getDouble("priceRating"),
                                    response.getInt("numRatings"),
                                    response.getJSONArray("reviews"));
                            listener.onResponse(res);
                        } catch (JSONException e) {
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
