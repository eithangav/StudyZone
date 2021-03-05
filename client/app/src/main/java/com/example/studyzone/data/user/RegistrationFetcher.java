package com.example.studyzone.data.user;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationFetcher {

    private RequestQueue _queue;
    private final static String REQUEST_URL = "http://localhost:3000/register";

    public class RegistrationResponse{
        public boolean isError;
        public boolean hasSucceeded;

        public RegistrationResponse(boolean isError, boolean hasSucceeded){
            this.isError = isError;
            this.hasSucceeded = hasSucceeded;
        }
    }

    public interface RegistrationResponseListener {
        public void onResponse(RegistrationResponse response);
    }

    public RegistrationFetcher(Context context){
        _queue = Volley.newRequestQueue(context);
    }

    private RegistrationResponse createErrorResponse() {
        return new RegistrationResponse(true, false);
    }

    public void dispatchRequest(final String email, final String password,
                                final RegistrationResponseListener listener){
        JSONObject postBody = new JSONObject();
        try {
            postBody.put("email", email);
            postBody.put("password", password);
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
                            RegistrationResponse res = new RegistrationResponse(false,
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
