package fbu.spooned.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fbu.spooned.R;
import fbu.spooned.activities.DetailActivity;
import fbu.spooned.activities.SwipeActivity;
import fbu.spooned.models.Restaurant;

/**
 * Created by jennytlee on 7/12/16.
 */

public class SwipeDeckAdapter extends ArrayAdapter<Restaurant> {

    final private int REQUEST_CODE = 200;
    final private String TAG = "SwipeDeckAdapter";

    public static final String PREFS_NAME = "SharedPreferences";

    int i;

    List<ParseObject> friendUsers;
    ArrayList<String> friendsList = new ArrayList<>();

    Typeface regType = Typeface.createFromAsset(getContext().getAssets(),"Champ.ttf");
    Typeface boldType = Typeface.createFromAsset(getContext().getAssets(),"ChampBold.ttf");

    public static class ViewHolder {

        TextView tvRestName;
        TextView tvCat;
        TextView tvMutual;
        ImageView ivThumbnail;
        ImageView ivLiked;
        ImageView ivDisliked;
        TextView tvDist;

        public ViewHolder(View view) {
            tvRestName = (TextView) view.findViewById(R.id.tvRestName);
            tvCat = (TextView) view.findViewById(R.id.tvCat);
            tvMutual = (TextView) view.findViewById(R.id.tvMutual);
            ivThumbnail = (ImageView) view.findViewById(R.id.ivThumbnail);
            ivLiked = (ImageView) view.findViewById(R.id.ivLiked);
            ivDisliked = (ImageView) view.findViewById(R.id.ivDisliked);
            tvDist = (TextView) view.findViewById(R.id.tvDist);
        }
    }

    public SwipeDeckAdapter(Context context, List<Restaurant> venues) {
        super(context, R.layout.item_card, venues);
        getFriends();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Restaurant restaurant = getItem(position);
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_card, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //getFriends();

        viewHolder.tvRestName.setTypeface(boldType);
        viewHolder.tvCat.setTypeface(regType);
        viewHolder.tvMutual.setTypeface(regType);

        viewHolder.tvRestName.setText(restaurant.getName());
        viewHolder.tvCat.setText(restaurant.getCategories());

        if (restaurant.getReaction().equals("like")) {
            viewHolder.ivLiked.setVisibility(View.VISIBLE);
            viewHolder.ivDisliked.setVisibility(View.INVISIBLE);
        } else if (restaurant.getReaction().equals("dislike")){
            viewHolder.ivLiked.setVisibility(View.INVISIBLE);
            viewHolder.ivDisliked.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.ivLiked.setVisibility(View.INVISIBLE);
            viewHolder.ivDisliked.setVisibility(View.INVISIBLE);
        }

        if (friendsList.size() != 0) {
            viewHolder.tvMutual.setVisibility(View.VISIBLE);
            viewHolder.tvMutual.setText(friendsList.size() + " Mutual Spoons c:");
        } else {
            viewHolder.tvMutual.setVisibility(View.INVISIBLE);
        }

        viewHolder.tvDist.setTypeface(boldType);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        double chosenLat = Double.longBitsToDouble(preferences.getLong("chosen_latitude", 0));
        double chosenLng = Double.longBitsToDouble(preferences.getLong("chosen_longitude", 0));

        Location chosenLocation = new Location("chosenLocation");
        chosenLocation.setLatitude(chosenLat);
        chosenLocation.setLongitude(chosenLng);

        Location restLocation = new Location("restLocation");
        restLocation.setLatitude(restaurant.getLocation().getLatitude());
        restLocation.setLongitude(restaurant.getLocation().getLongitude());

        double distance = (long) chosenLocation.distanceTo(restLocation);
        Log.d("DEBUG LOCATION", restaurant.getName() + ": " + String.valueOf(distance) + chosenLocation.toString());

        double distanceInMiles = distance * 0.000621371;
        if (distanceInMiles <= 25) {
            viewHolder.tvDist.setText(String.format("%.1f", distanceInMiles) + " miles away");
        } else {
            viewHolder.tvDist.setVisibility(View.INVISIBLE);
        }

        String imageUrl = restaurant.getImageUrl();

        Glide.with(getContext()).load(imageUrl).placeholder(R.drawable.placeholder).centerCrop().into(viewHolder.ivThumbnail);

            convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG", "clicked");

                Intent intent = new Intent (v.getContext(), DetailActivity.class);

                intent.putExtra("id", restaurant.getId());
                intent.putExtra("name", restaurant.getName());
                intent.putExtra("categories", restaurant.getCategories());
                intent.putExtra("closed", restaurant.isClosed());
                intent.putExtra("phone", restaurant.getPhone());
                intent.putExtra("display_address", restaurant.getLocation().getDisplayAddress());
                intent.putExtra("rating", restaurant.getRating());
                intent.putExtra("latitude", restaurant.getLocation().getLatitude());
                intent.putExtra("longitude", restaurant.getLocation().getLongitude());
                intent.putExtra("yelpPhoto", restaurant.getImageUrl());
                intent.putExtra("reaction", restaurant.getReaction());

                ((SwipeActivity) v.getContext()).startActivityForResult(intent, REQUEST_CODE);
            }
        });

        return convertView;
    }

    public void getFriends() {

        final GraphRequest request = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray objects, GraphResponse response) {
                        if (objects != null) {

                            for (int i = 0; i < objects.length(); i++) {
                                try {
                                    JSONObject data = objects.getJSONObject(i);
                                    friendsList.add(data.getString("name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            // Construct a ParseUser query that will find friends whose
                            // facebook IDs are contained in the current user's friend list.
                            ParseQuery friendQuery = ParseUser.getQuery();
                            friendQuery.whereContainedIn("fbId", friendsList);

                            // findObjects will return a list of ParseUsers that are friends with
                            // the current user
                            try {
                                friendUsers = friendQuery.find();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void getMutual() {
        if (friendsList.size() != 0) {
            for (i = 0; i <friendsList.size(); i++) {
                //ParseUser parseUser = ParseUser.;
            }
        } else {

        }

        /* ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("objectId",userId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    //List contain object with specific user id.

                } else {
                    // error
                }
            }
        }); */
    }
}
