package com.rs2.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JSONObject {
    private final JsonObject obj;

    public JSONObject() {
        this.obj = new JsonObject();
    }

    public JSONObject(String json) {
        this.obj = JsonParser.parseString(json).getAsJsonObject();
    }

    JSONObject(JsonObject o) {
        this.obj = o;
    }

    public boolean has(String key) {
        return obj.has(key) && !obj.get(key).isJsonNull();
    }

    public String getString(String key) {
        return obj.get(key).getAsString();
    }

    public double getDouble(String key) {
        return obj.get(key).getAsDouble();
    }

    public boolean getBoolean(String key) {
        return obj.get(key).getAsBoolean();
    }

    public int getInt(String key) {
        return obj.get(key).getAsInt();
    }

    public JSONArray optJSONArray(String key) {
        if (!has(key))
            return null;
        JsonArray a = obj.getAsJsonArray(key);
        return new JSONArray(a);
    }

    public JSONObject put(String key, String value) {
        obj.addProperty(key, value);
        return this;
    }

    public JSONObject put(String key, Number value) {
        obj.addProperty(key, value);
        return this;
    }

    public JSONObject put(String key, boolean value) {
        obj.addProperty(key, value);
        return this;
    }

    public JSONObject put(String key, JSONArray array) {
        obj.add(key, array.getInternal());
        return this;
    }

    public JSONObject put(String key, JSONObject other) {
        obj.add(key, other.getInternal());
        return this;
    }

    public String toString() {
        return obj.toString();
    }

    JsonObject getInternal() {
        return obj;
    }
}
