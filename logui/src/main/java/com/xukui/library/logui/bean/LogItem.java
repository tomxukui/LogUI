package com.xukui.library.logui.bean;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class LogItem implements Serializable {

    private long time;
    private int level;
    private String tag;
    private String message;

    public LogItem(long time, int level, String tag, String message) {
        this.time = time;
        this.level = level;
        this.tag = tag;
        this.message = message;
    }

    public LogItem() {
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toJsonString() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("time", time);
            jsonObject.put("level", level);
            jsonObject.put("tag", tag);
            jsonObject.put("message", message);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public LogItem parseJson(String json) {
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                time = jsonObject.getLong("time");
                level = jsonObject.getInt("level");
                tag = jsonObject.getString("tag");
                message = jsonObject.getString("message");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return this;
    }

}