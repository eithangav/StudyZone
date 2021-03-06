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

/**
 * a class to fetch all zones' location and names from the server using GET request
 * in order to show them in the map
 */
public class MarkersFetcher {

    private RequestQueue _queue;
    private final static String REQUEST_URL = "http://10.0.0.23/zones";

    public class MarkersResponse {
        public boolean isError;
        public JSONArray markers;

        public MarkersResponse(boolean isError, JSONArray markers) {
            this.isError = isError;
            this.markers = markers;
        }
    }

    public interface MarkersResponseListener {
        public void onResponse(MarkersResponse response);
    }

    public MarkersFetcher(Context context) {
        _queue = Volley.newRequestQueue(context);
    }

    private MarkersResponse createErrorResponse() {
        return new MarkersResponse(true, null);
    }

    public void dispatchRequest(final MarkersResponseListener listener) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, REQUEST_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            MarkersResponse res = new MarkersResponse(false,
                                    response.getJSONArray("markers"));
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
