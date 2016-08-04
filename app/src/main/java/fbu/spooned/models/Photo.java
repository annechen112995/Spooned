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
 * Created by jennytlee on 7/19/16.
 */
public class Photo {

    private String prefix;
    private String suffix;
    private int width;
    private int height;
    private int createdAt;
    private String photoUserFirst;
    private String photoUserLast;
    private String url;
    private String fullName;

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public String getTimeStamp() {
        String time = convertUnixTime(getCreatedAt());
        String timeAgo = getRelativeTimeAgo(time);
        return timeAgo;
    }

    public String getPhotoUserFirst() {
        return photoUserFirst;
    }

    public String getPhotoUserLast() {
        return photoUserLast;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhotoUrl() {
        url = getPrefix() + "cap500" + getSuffix();
        return url;
    }

    public static Photo fromJson(JSONObject jsonObject) {
        Photo photo = new Photo();

        try {
            photo.prefix = jsonObject.getString("prefix");
            photo.suffix = jsonObject.getString("suffix");
            photo.width = jsonObject.getInt("width");
            photo.height = jsonObject.getInt("height");
            photo.createdAt = jsonObject.getInt("createdAt");

            JSONObject userObject = jsonObject.getJSONObject("user");
            photo.photoUserFirst = userObject.getString("firstName");
            if (userObject.has("lastName")) {
                photo.photoUserLast = userObject.getString("lastName");
                photo.fullName = photo.getPhotoUserFirst() + " " + photo.getPhotoUserLast().charAt(0) + ".";
            } else {
                photo.photoUserLast = "";
                photo.fullName = photo.getPhotoUserFirst();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return photo;
    }

    public static ArrayList<Photo> fromJson(JSONArray jsonArray) {

        ArrayList<Photo> photos = new ArrayList<>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject photoJson;

            try {
                photoJson = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

            Photo photo = Photo.fromJson(photoJson);
            if (photo != null) {
                photos.add(photo);
            }
        }

        return photos;

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
