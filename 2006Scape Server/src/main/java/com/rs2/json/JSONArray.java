package com.rs2.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JSONArray {
    private final JsonArray arr;

    public JSONArray() {
        this.arr = new JsonArray();
    }

    public JSONArray(JsonArray a) {
        this.arr = a;
    }

    public JSONArray(String json) {
        this.arr = JsonParser.parseString(json).getAsJsonArray();
    }

    public int length() {
        return arr.size();
    }

    public int optInt(int index) {
        JsonElement e = arr.get(index);
        if (e == null || e.isJsonNull())
            return 0;
        try {
            return e.getAsInt();
        } catch (Exception ex) {
            return 0;
        }
    }

    public void put(int index, JSONObject obj) {
        if (index == arr.size()) {
            arr.add(obj.getInternal());
        } else if (index < arr.size()) {
            arr.set(index, obj.getInternal());
        } else {
            // pad with nulls then add
            while (arr.size() < index)
                arr.add((String) null);
            arr.add(obj.getInternal());
        }
    }

    public void put(int index, int value) {
        if (index == arr.size())
            arr.add(value);
        else if (index < arr.size())
            arr.set(index, new com.google.gson.JsonPrimitive(value));
        else {
            while (arr.size() < index)
                arr.add((String) null);
            arr.add(value);
        }
    }

    public void put(JSONObject obj) {
        arr.add(obj.getInternal());
    }

    public void put(JsonElement element) {
        arr.add(element);
    }

    public String toString() {
        return arr.toString();
    }

    JsonArray getInternal() {
        return arr;
    }
}
