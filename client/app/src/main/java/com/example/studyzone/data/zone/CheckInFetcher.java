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

public class CheckInFetcher {

    private RequestQueue _queue;
    private final static String REQUEST_URL = "http://localhost:3000/checkin";

    public class CheckInResponse {
        public boolean isError;
        public boolean hasSucceeded;

        public CheckInResponse(boolean isError, boolean hasSucceeded) {
            this.isError = isError;
            this.hasSucceeded = hasSucceeded;
        }
    }

    public interface CheckInResponseListener {
        public void onResponse(CheckInResponse response);
    }

    public CheckInFetcher(Context context) {
        _queue = Volley.newRequestQueue(context);
    }

    private CheckInResponse createErrorResponse() {
        return new CheckInResponse(true, false);
    }

    public void dispatchRequest(final int zoneId, final String userEmail, final CheckInResponseListener listener) {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put("id", zoneId);
            postBody.put("email", userEmail);
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
                            CheckInResponse res = new CheckInResponse(false,
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
