package com.belstu.course.mapper;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonError {
    public JSONObject getError(String scope, String message) throws JSONException {
        JSONObject error = new JSONObject();
        error.put("scope", scope);
        error.put("message", message);
        return error;
    }
}
