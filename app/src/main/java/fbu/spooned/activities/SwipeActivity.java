package fbu.spooned.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daprlabs.cardstack.SwipeDeck;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.ButterKnife;
import fbu.spooned.R;
import fbu.spooned.adapters.SwipeDeckAdapter;
import fbu.spooned.clients.YelpClient;
import fbu.spooned.fragments.LocationPickerFragment;
import fbu.spooned.models.Restaurant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SwipeActivity extends AppCompatActivity
        implements LocationPickerFragment.LocationPickerDialogListener, LocationListener{

    final private String TAG = "SwipeActivity";
    final private int REQUEST_CODE_START_UPDATES = 0;
    final private int REQUEST_CODE_STOP_UPDATES = 1;
    final private int REQUEST_CODE_LAST_KNOWN = 2;
    final private int UPDATE_MILLISECONDS = 0;
    final private int UPDATE_DISTANCE = 0;
    final private int REQUEST_CODE = 200;
    final private int FILTER_REQUEST_CODE = 1;
    private static final int FIVE_MINUTES = 1000 * 60 * 5;
    public static final String PREFS_NAME = "SharedPreferences";

    // restaurants in swipe deck
    ArrayList<Restaurant> restaurants;


    // ids of user's disliked and liked restaurants
    ArrayList<String> dislikes;
    ArrayList<String> likes;

    // selected restaurant categories for Yelp query
    ArrayList<String> categories;

    SwipeDeck cardStack;
    SwipeDeckAdapter adapter;
    ImageButton ibLike;
    ImageButton ibDislike;
    ImageButton ibFilter;
    ImageView ivOut;
    TextView tvReset;
    LinearLayout llReset;
    ProgressBar progressBar;
    ImageView left_image;
    ImageView right_image;

    ParseUser parseUser;
    Location currentLocation;
    Location chosenLocation;
    LocationManager locationManager;
    YelpClient yelpClient;

    int radius; //miles
    String locName; // name of chosen location, to display in location picker fragment


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, requestCode + "");
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case REQUEST_CODE_START_UPDATES:
                    startLocationUpdates();
                    break;
                case REQUEST_CODE_STOP_UPDATES:
                    stopLocationUpdates();
                    break;
                case REQUEST_CODE_LAST_KNOWN:
                    requestLastKnownLocation();
                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
        else {
            // Permission Denied
            Log.d(TAG, "permission denied");
        }
        hideProgressBar();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);
        ButterKnife.bind(this);

        yelpClient = new YelpClient();
        parseUser = ParseUser.getCurrentUser();

        categories = new ArrayList<>();

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Fonts
        Typeface boldType = Typeface.createFromAsset(this.getAssets(),"ChampBold.ttf");

        ibLike = (ImageButton) findViewById(R.id.ibLike);
        ibDislike = (ImageButton) findViewById(R.id.ibDislike);
        ivOut = (ImageView) findViewById(R.id.ivOut);
        ibFilter = (ImageButton) findViewById(R.id.ibFilter);
        ImageButton ibLocation = (ImageButton) findViewById(R.id.ibLocation);

        tvReset = (TextView) findViewById(R.id.tvReset);
        tvReset.setTypeface(boldType);

        llReset = (LinearLayout) findViewById(R.id.llReset);
        left_image = (ImageView) findViewById(R.id.left_image);
        right_image = (ImageView) findViewById(R.id.right_image);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        showProgressBar();

        setClickListeners();

        cardStack = (SwipeDeck) findViewById(R.id.swipe_deck);

        restaurants = new ArrayList<>();

        loadLikes();
        loadDislikes();

        adapter = new SwipeDeckAdapter(this, restaurants);
        cardStack.setAdapter(adapter);

        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                Restaurant r = restaurants.get(position);
                Log.i(TAG, r.getName() + " was swiped left, position in adapter: " + position);

                r.setReaction("dislike");
                left_image.setAlpha(1.0f);
                ObjectAnimator disappear = ObjectAnimator.ofFloat(left_image, "alpha", 0.0f);
                disappear.start();

                addToHistory(r, false);

            }

            @Override
            public void cardSwipedRight(int position) {
                Restaurant r = restaurants.get(position);
                Log.i(TAG, r.getName() + " was swiped right, position in adapter: " + position);

                r.setReaction("like");

                right_image.setAlpha(1.0f);
                ObjectAnimator disappear = ObjectAnimator.ofFloat(right_image, "alpha", 0.0f);
                disappear.start();

                addToHistory(r, true);
            }

            @Override
            public void cardActionDown() {}

            @Override
            public void cardActionUp() {}

            @Override
            public void cardsDepleted() {
                Log.i(TAG, "no more cards");
                llReset.setVisibility(View.VISIBLE);
                hideProgressBar();
            }
        });

        ibLocation.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                showLocationPicker();
            }
        });

        setInitialLocations();
    }

    private void showLocationPicker() {
        FragmentManager fm = getSupportFragmentManager();
        LocationPickerFragment locationPickerFragment = LocationPickerFragment.newInstance(chosenLocation, currentLocation, radius, locName);
        locationPickerFragment.show(fm, "fragment_location_picker");

    }

    public void showProgressBar() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    public void setClickListeners() {
        ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardStack.swipeTopCardRight(1000);
            }
        });

        ibDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardStack.swipeTopCardLeft(1000);
            }
        });

        ibFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SwipeActivity.this, FilterActivity.class);
                intent.putStringArrayListExtra("categories", categories);
                startActivityForResult(intent, FILTER_REQUEST_CODE);
            }
        });
    }
    public void onSavedClick(View view) {
        Intent i = new Intent(SwipeActivity.this, SavedActivity.class);
        i.putStringArrayListExtra("likes", likes);
        startActivity(i);
    }


    public double mileToMeter(int mi) {
        return ((double) mi) * 1609.34;
    }

    public void addToHistory(Restaurant r, boolean liked) {
        ParseRelation<ParseObject> likeRelation = parseUser.getRelation("likes");
        ParseRelation<ParseObject> dislikeRelation = parseUser.getRelation("dislikes");
        String rId = r.getId();
        if (liked) {
            dislikes.remove(rId);
            if (!likes.contains(rId)) {
                likes.add(rId);
            }
            r.setReaction("like");
            dislikeRelation.remove(r);
            likeRelation.add(r);
        }
        else {
            likes.remove(rId);
            if (!dislikes.contains(rId)){
                dislikes.add(rId);
            }
            r.setReaction("dislike");
            likeRelation.remove(r);
            dislikeRelation.add(r);
        }
        parseUser.saveInBackground();
    }

    public boolean liked(Restaurant restaurant) {
        return likes.contains(restaurant.getId());
    }

    public boolean disliked(Restaurant restaurant) {
        return dislikes.contains(restaurant.getId());
    }

    public void loadDislikes() {
        dislikes = new ArrayList<>();
        ParseRelation<ParseObject> dislikeRelation = parseUser.getRelation("dislikes");

        try {
            List<ParseObject> dislikesResults = dislikeRelation.getQuery().find();
            for (ParseObject s: dislikesResults) {
                dislikes.add(((Restaurant) s).getId());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d(TAG, dislikes.size() + "");
    }
    public void loadLikes() {
        likes = new ArrayList<>();
        ParseRelation<ParseObject> dislikeRelation = parseUser.getRelation("likes");
        try {
            List<ParseObject> likesResults = dislikeRelation.getQuery().find();
            for (ParseObject s: likesResults) {
                likes.add(((Restaurant) s).getId());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG, likes.size() + "");
    }


    public void setLocations(Location currentLocation, Location chosenLocation) {
        this.currentLocation = currentLocation;
        this.chosenLocation = chosenLocation;
        updateSharedPrefs();
    }


    public void updateRestaurants(Location loc) {
        showProgressBar();
        Log.d(TAG, loc.getLatitude() + "," + loc.getLongitude());
        restaurants.clear();
        adapter.notifyDataSetChanged();
        cardStack.setAdapter(adapter);
        llReset.setVisibility(View.INVISIBLE);

        // Yelp client
        yelpClient.searchQuery(loc.getLatitude(), loc.getLongitude(),
                mileToMeter(radius), categories, new Callback<SearchResponse>() {
                    @Override
                    public void onResponse(Call<SearchResponse> call, final Response<SearchResponse> response) {
                        SearchResponse searchResponse = response.body();
                        final ArrayList<Business> businesses = searchResponse.businesses();
                        if (businesses.isEmpty()) {
                            Log.d(TAG, "no businesses found");
                            cardStack.setAdapter(adapter);
                            llReset.setVisibility(View.VISIBLE);
                            hideProgressBar();
                        }
                        for (int i = 0; i < businesses.size(); i ++) {
                            final int j = i;
                            final Business b = businesses.get(i);
                            String id = b.id();
                            Log.d(TAG, "id: " + id);
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Restaurant");
                            query.whereEqualTo("id", id);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (objects.size() > 0) {
                                        Restaurant restaurant = (Restaurant) objects.get(0);
                                        Log.d(TAG, restaurant.getName());
                                        if (liked(restaurant)) {
                                            restaurant.setReaction("like");
                                            Log.d(TAG, "you've liked " + restaurant.getName());
                                        }
                                        else if (disliked(restaurant)) {
                                            restaurant.setReaction("dislike");
                                            Log.d(TAG, "you've disliked " + restaurant.getName());
                                        }
                                        else {
                                            restaurant.setReaction("none");
                                            restaurants.add(restaurant);
                                            Log.d(TAG, "you haven't seen " + restaurant.getName());
                                        }

                                        if (j == businesses.size() - 1 && restaurants.isEmpty()) {
                                            llReset.setVisibility(View.VISIBLE);
                                            hideProgressBar();
                                            Log.d(TAG, "you've seen all the restaurants!");
                                        }
                                    }
                                    else {
                                        Restaurant restaurant = YelpClient.fromNewBusiness(b);
                                        if (restaurant != null) {
                                            restaurant.setReaction("none");
                                            restaurants.add(restaurant);
                                            Log.d(TAG, "you're getting a brand new restaurant!! " + restaurant.getName());
                                        }
                                    }

                                    adapter.notifyDataSetChanged();
                                    hideProgressBar();
                                }
                            });
                        }
                        cardStack.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Call<SearchResponse> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    @Override
    public void onFinishPickDialog(Location currentLocation, Location chosenLocation, int radius, String locName) {
        // do nothing if no settings were changed
        if (this.chosenLocation == chosenLocation
                && this.radius == radius) {
            Log.d(TAG, "no settings changed");
            return;
        }
        // if the user chose a location elsewhere, stop updating their current location
        else if (currentLocation != chosenLocation) {
            Log.d(TAG, "different location chosen!");
            stopLocationUpdates();
        }
        else {
            Log.d(TAG, "current location chosen!");
            this.chosenLocation = currentLocation;
            startLocationUpdates();
        }
        this.chosenLocation = chosenLocation;
        this.radius = radius;
        this.locName = locName;
        updateSharedPrefs();
        updateRestaurants(chosenLocation);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {}
    public void onProviderEnabled(String provider) {}
    public void onProviderDisabled(String provider) {}
    public void onLocationChanged(Location location) {
        Log.d(TAG, "current location: " + currentLocation);
        Log.d(TAG, "new location " + location);
        // Called when a new location is found by the network location provider.
        if (isBetterLocation(location, currentLocation)) {
            Log.d(TAG, "found a better location!");
            setLocations(location, location);
            updateRestaurants(location);
        }
    }
    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_START_UPDATES);
        }
        else {
            Log.d(TAG, "started location updates");
            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_MILLISECONDS, UPDATE_DISTANCE, this);
        }
    }

    public void stopLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(SwipeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_STOP_UPDATES);
        }
        else {
            Log.d(TAG, "stopped location updates");
            locationManager.removeUpdates(this);
        }
    }

    public void requestLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(SwipeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LAST_KNOWN);
        }
        else {
            Location lastKnownLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            setLocations(lastKnownLocation, lastKnownLocation);
            updateRestaurants(lastKnownLocation);
        }
    }

    // from android docs
    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        Log.d(TAG, Long.toString(timeDelta));
        boolean isSignificantlyNewer = timeDelta > FIVE_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -FIVE_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }
        if (location.distanceTo(currentBestLocation) > mileToMeter(5)) {
            return true;
        }
        return false;

//        // Check whether the new location fix is more or less accurate
//        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
//        boolean isLessAccurate = accuracyDelta > 0;
//        boolean isMoreAccurate = accuracyDelta < 0;
//        boolean isSignificantlyLessAccurate = accuracyDelta > 200;
//
//        // Check if the old and new location are from the same provider
//        boolean isFromSameProvider = isSameProvider(location.getProvider(),
//                currentBestLocation.getProvider());
//
//        // Determine location quality using a combination of timeliness and accuracy
//        if (isMoreAccurate) {
//            return true;
//        } else if (isNewer && !isLessAccurate) {
//            return true;
//        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
//            return true;
//        }
//        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // returning from detailactivity

        final android.os.Handler mHandler = new android.os.Handler();

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data.getIntExtra("swipe", 0) == 1) {

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cardStack.swipeTopCardRight(1000);
                    }
                }, 300);

            } else {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cardStack.swipeTopCardLeft(1000);
                    }
                }, 300);
            }
        }

        if (requestCode == FILTER_REQUEST_CODE && resultCode == FilterActivity.RESULT_OK) {
            ArrayList<String> new_categories = data.getStringArrayListExtra("categories");
            if (!categories.equals(new_categories)) {
                categories = new_categories;
                updateRestaurants(chosenLocation);
                updateSharedPrefs();
            }
            Log.d(TAG, categories.toString());
        }
    }

    public void updateSharedPrefs() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("chosen_latitude", Double.doubleToLongBits(chosenLocation.getLatitude()));
        editor.putLong("chosen_longitude", Double.doubleToLongBits(chosenLocation.getLongitude()));
        editor.putLong("current_latitude", Double.doubleToLongBits(currentLocation.getLatitude()));
        editor.putLong("current_longitude", Double.doubleToLongBits(currentLocation.getLongitude()));
        editor.putInt("radius", radius);
        editor.putString("location_name", locName);
        editor.putStringSet("categories", new HashSet<String>(categories));
        editor.apply();
    }

    public void setInitialLocations() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        double cachedChosenLatitude = Double.longBitsToDouble(settings.getLong("chosen_latitude", 0));
        double cachedChosenLongitude = Double.longBitsToDouble(settings.getLong("chosen_longitude", 0));
        double cachedCurrentLatitude = Double.longBitsToDouble(settings.getLong("current_latitude", 0));
        double cachedCurrentLongitude = Double.longBitsToDouble(settings.getLong("current_longitude", 0));
        locName = settings.getString("location_name", "Current Location");
        radius = settings.getInt("radius", 5);
        categories = new ArrayList<>(settings.getStringSet("categories", new HashSet<String>()));
        if (!(cachedChosenLatitude == 0 && cachedChosenLongitude == 0)) {
            Location lastChosenLocation = new Location("");
            lastChosenLocation.setLatitude(cachedChosenLatitude);
            lastChosenLocation.setLongitude(cachedChosenLongitude);
            Location lastCurrentLocation = new Location("");
            lastCurrentLocation.setLatitude(cachedCurrentLatitude);
            lastCurrentLocation.setLongitude(cachedCurrentLongitude);
            setLocations(lastCurrentLocation, lastChosenLocation);
            Log.d(TAG, "using cached location from shared preferences: " + chosenLocation.getLatitude() + ", " + chosenLocation.getLongitude());
            Log.d(TAG, "current location from shared preferences: " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
            if (currentLocation.getLatitude() == chosenLocation.getLatitude() && currentLocation.getLongitude() == chosenLocation.getLongitude()) {
                startLocationUpdates();
            }
            updateRestaurants(lastChosenLocation);
        }
        else {
            Log.d(TAG, "using last known location");
            requestLastKnownLocation();
            startLocationUpdates();
        }
    }

}
