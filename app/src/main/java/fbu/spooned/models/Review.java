package fbu.spooned.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jennytlee on 7/13/16.
 */
public class Review extends com.yelp.clientlib.entities.Review {

    private String id;
    private double rating;
    private long timestamp;
    private String excerpt;

    private com.yelp.clientlib.entities.User user;


    @Override
    public String id() {
        return id;
    }

    @Override
    public String excerpt() {
        return excerpt;
    }

    @Override
    public Double rating() {
        return rating;
    }

    @Override
    public String ratingImageUrl() {
        return null;
    }

    @Override
    public String ratingImageLargeUrl() {
        return null;
    }

    @Override
    public String ratingImageSmallUrl() {
        return null;
    }

    @Override
    public Long timeCreated() {
        return timestamp;

    }

    public Date timeStamp() {
        java.util.Date time = new java.util.Date( (long) timestamp*1000);
        return time;
    }

    @Override
    public com.yelp.clientlib.entities.User user() {
        return user;
    }


    public static Review fromJSON(JSONObject jsonObject) {
        Review review = new Review();

        try {

            review.user = User.fromJSON(jsonObject.getJSONObject("user"));
            review.excerpt = jsonObject.getString("excerpt");
            review.id = User.fromJSON(jsonObject.getJSONObject("user")).id();
            review.timestamp = jsonObject.getLong("time_created");
            review.rating = jsonObject.getDouble("rating");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return review;
    }

    public static ArrayList<Review> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Review> reviews = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject reviewJson = jsonArray.getJSONObject(i);
                Review review = Review.fromJSON(reviewJson);
                if (review != null) {
                    reviews.add(review);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        return reviews;
    }
/*
    private String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

            if (Character.isDigit(relativeDate.charAt(0))) {
                StringBuilder sb = new StringBuilder(relativeDate);

                if (!Character.isDigit(relativeDate.charAt(1))) {
                    sb.deleteCharAt(1);
                    relativeDate = sb.toString().substring(0, 2);
                } else {
                    sb.deleteCharAt(2);
                    relativeDate = sb.toString().substring(0, 3);
                }
            }



        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
*/
}
