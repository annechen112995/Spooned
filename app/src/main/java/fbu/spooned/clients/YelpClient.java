package fbu.spooned.clients;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Category;
import com.yelp.clientlib.entities.Coordinate;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fbu.spooned.models.Location;
import fbu.spooned.models.Restaurant;
import retrofit2.Call;
import retrofit2.Callback;

///**
// * Created by eshen on 7/6/16.
// */
public class YelpClient {

    final String CONSUMER_KEY = "wf0aAPuHyImHqW6TVelbdw";
    final String CONSUMER_SECRET = "wt4pREEFvT5ycGCUwYwFBn1K1bA";
    final String TOKEN = "swOcbmBLGqthCkSjiRSivayhLv0CjcRR";
    final String TOKEN_SECRET = "-4NcX472WWlRMoKxWzgZm_ED1sY";
    final String TAG = "YelpClient";
    YelpAPI yelpAPI;

    public YelpClient() {
        YelpAPIFactory apiFactory = new YelpAPIFactory(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
        yelpAPI = apiFactory.createAPI();
    }


    public void searchQuery(double latitude, double longitude, double radius, ArrayList<String> categories, Callback<SearchResponse> callback) {
        Map<String, String> params = new HashMap<>();

        params.put("sort", String.valueOf(2));
        params.put("radius_filter", Double.toString(radius)); // max is 40000

        String categoriesString = "";
        if (categories.isEmpty()) {
            categoriesString = "restaurants";
        }
        else {
            for (String cat: categories) {
                categoriesString += cat + ",";
            }
            categoriesString = categoriesString.substring(0, categoriesString.length() - 1);
        }
        Log.d(TAG, categoriesString);
        params.put("category_filter", categoriesString);

        CoordinateOptions coordinate = CoordinateOptions.builder()
                .latitude(latitude)
                .longitude(longitude).build();

        Call<SearchResponse> call = yelpAPI.search(coordinate, params);
        call.enqueue(callback);
    }

    public static Restaurant fromNewBusiness(final Business business) {
        String id = business.id();
        Restaurant restaurant;
        String name = business.name();
        String imageUrl = business.imageUrl();
        Coordinate coord = business.location().coordinate();

        // don't add restaurant if it doesn't have an id, name, latlng, and image
        if (id == null || name == null || imageUrl == null || coord == null) {
            return null;
        }

        double lat = coord.latitude();
        double lng = coord.longitude();
        Category categories = business.categories().get(0);
        String category = categories.name();
        double rating = business.rating();
        boolean closed = business.isClosed();
        String phone = business.phone();
        ArrayList<String> dispAdd = business.location().displayAddress();
        String displayAddress = "";
        for (int i = 0; i < dispAdd.size(); i++) {
            displayAddress = displayAddress + dispAdd.get(i) + " ";
        }
        Log.d("DISPLAYADDRESS", displayAddress);
        displayAddress = displayAddress.substring(0, displayAddress.length() - 1);
        Location location = new Location(lat, lng, displayAddress);
        String reaction = "none";
        restaurant = new Restaurant(id, name, imageUrl, category, rating, closed, phone, location, reaction);
        restaurant.saveInBackground();
        return restaurant;
    }

    public static Restaurant fromBusiness(final Business business) {
        String id = business.id();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Restaurant");
        query.whereEqualTo("id", id);
        Restaurant restaurant;
        try {
            return (Restaurant) query.getFirst();
        }
        catch (ParseException e) {
            if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                String name = business.name();
                String imageUrl = business.imageUrl();

                // don't add restaurant if it doesn't have an id, name, and image
                if (id == null || name == null || imageUrl == null) {
                    return null;
                }

                Category categories = business.categories().get(0);
                String category = categories.name();
                double rating = business.rating();
                boolean closed = business.isClosed();
                String phone = business.phone();
                double lat = business.location().coordinate().latitude();
                double lng = business.location().coordinate().longitude();
                ArrayList<String> dispAdd = business.location().displayAddress();
                String displayAddress = "";
                for (int i = 0; i < dispAdd.size(); i++) {
                    displayAddress = displayAddress + dispAdd.get(i) + " ";
                }
                Log.d("DISPLAYADDRESS", displayAddress);
                displayAddress = displayAddress.substring(0, displayAddress.length() - 1);
                Location location = new Location(lat, lng, displayAddress);
                String reaction = "none";
                restaurant = new Restaurant(id, name, imageUrl, category, rating, closed, phone, location, reaction);
                restaurant.saveInBackground();
                return restaurant;
            }
            else {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static ArrayList<Restaurant> fromListOfBusinesses(ArrayList<Business> businesses) {
        ArrayList<Restaurant> restaurants = new ArrayList<>();
        for (Business b: businesses) {
            Restaurant r = fromBusiness(b);
            if (r != null) {
                restaurants.add(r);
            }
        }
        return restaurants;
    }

    public void findBusiness(String id, Callback<Business> businessCallback) {
        Call<Business> call = yelpAPI.getBusiness(id);
        call.enqueue(businessCallback);
    }
}
