package com.example.studyzone.data.user;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.studyzone.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * a class to check if the user is registered (by: email, password) using POST request
 */
public class LoginFetcher {

    private RequestQueue _queue;
    private final static String REQUEST_URL = "http://10.0.0.23/login";

    public class LoginResponse {
        public boolean isError;
        public boolean emailExists;
        public boolean passwordMatches;

        public LoginResponse(boolean isError, boolean emailExists, boolean passwordMatches){
            this.isError = isError;
            this.emailExists = emailExists;
            this.passwordMatches = passwordMatches;
        }
    }

    public interface LoginResponseListener {
        public void onResponse(LoginResponse response);
    }

    public LoginFetcher(Context context){
        _queue = Volley.newRequestQueue(context);
    }

    private LoginResponse createErrorResponse() {
        return new LoginResponse(true, false, false);
    }

    public void dispatchRequest(final String email, final String password, final String token,
                                final LatLng location, final LoginResponseListener listener){
        JSONObject postBody = new JSONObject();
        try {
            postBody.put("email", email);
            postBody.put("password", password);
            postBody.put("token", token);
            postBody.put("latitude", location.latitude);
            postBody.put("longitude", location.longitude);
        }
        catch (JSONException e) {
            listener.onResponse(createErrorResponse());
            return;
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, REQUEST_URL, postBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("login", "login successful");
                        try {
                            LoginResponse res = new LoginResponse(false,
                                    response.getString("emailExists").equals("true"),
                                    response.getString("passwordMatches").equals("true"));
                            listener.onResponse(res);
                        }
                        catch (JSONException e) {
                            listener.onResponse(createErrorResponse());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("login", "Failed to login - " + error);
                        listener.onResponse(createErrorResponse());
                    }
                });

        _queue.add(req);
    }
}
