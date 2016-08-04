package fbu.spooned.models;

import android.net.ParseException;
import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by jennytlee on 7/13/16.
 */
public class Tip {

    private String tipId;
    private int createdAt;
    private String text;
    private String firstName;
    private String lastInitial;
    private String userPrefix;
    private String userSuffix;
    private String userPhotoUrl;
    private int agreeCount;
    private int disagreeCount;

    public String getTipId() {
        return tipId;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public String getText() {
        return text;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastInitial() {
        return lastInitial;
    }

    public String getUserPrefix() {
        return userPrefix;
    }

    public String getUserSuffix() {
        return userSuffix;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public int getAgreeCount() {
        return agreeCount;
    }

    public int getDisagreeCount() {
        return disagreeCount;
    }

    public String getTimestamp() {

        String time = convertUnixTime(getCreatedAt());
        String timeAgo = getRelativeTimeAgo(time);
        return timeAgo;

    }

    public static Tip fromJson(JSONObject jsonObject) {
        Tip tip = new Tip();

        try {

            tip.tipId = jsonObject.getString("id");
            tip.createdAt = jsonObject.getInt("createdAt");
            tip.text = jsonObject.getString("text");
            tip.agreeCount = jsonObject.getInt("agreeCount");
            tip.disagreeCount = jsonObject.getInt("disagreeCount");
            tip.firstName = jsonObject.getJSONObject("user").getString("firstName");

            if (jsonObject.getJSONObject("user").has("lastName")) {
                tip.lastInitial = jsonObject.getJSONObject("user").getString("lastName").charAt(0) + ".";
            } else {
                tip.lastInitial = "";
            }

            JSONObject photoObject = jsonObject.getJSONObject("user").getJSONObject("photo");
            if (photoObject.getString("suffix").equals("/blank_boy.png") || photoObject.getString("suffix").equals("/blank_girl.png")) {
                tip.userPhotoUrl = "http://i.imgur.com/71s33BS.png";
            } else {

                tip.userPrefix = photoObject.getString("prefix");
                tip.userSuffix = photoObject.getString("suffix");

                tip.userPhotoUrl = tip.userPrefix + "cap500" + tip.userSuffix;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tip;
    }

    public static ArrayList<Tip> fromJson(JSONArray jsonArray) {

        ArrayList<Tip> tips = new ArrayList<>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject tipJson;

            try {
                tipJson = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

            Tip tip = Tip.fromJson(tipJson);
            if (tip != null) {
                tips.add(tip);
            }
        }

        return tips;

    }

    private String convertUnixTime(int unix) {

        Date date = new Date(unix*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy"); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4")); // give a timezone reference for formating (see comment at the bottom
        String formattedDate = sdf.format(date);
        return formattedDate;
    }


    private String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

}
