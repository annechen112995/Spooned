package fbu.spooned.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import fbu.spooned.R;
import fbu.spooned.models.Restaurant;

public class MapPinActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, LocationListener {

    ParseUser parseUser;
    private GoogleMap mMap;
    LatLngBounds.Builder builder;
    HashMap<String, Restaurant> mapped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_pin);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        parseUser = ParseUser.getCurrentUser();
        builder = new LatLngBounds.Builder();
        mapped = new HashMap<>();

        SpannableString s = new SpannableString("Map");
        s.setSpan(new fbu.spooned.models.TypefaceSpan(this, "Champ.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setTitle(s);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        SharedPreferences preferences = getSharedPreferences(SwipeActivity.PREFS_NAME, 0);
        double currentLat = Double.longBitsToDouble(preferences.getLong("current_latitude", 0));
        double currentLng = Double.longBitsToDouble(preferences.getLong("current_longitude", 0));

        LatLng current = new LatLng(currentLat, currentLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 10f));

        // show current location (blue circle) on map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        // set zoom in out buttons
        mMap.getUiSettings().setZoomControlsEnabled(true);

        addMarkers();
    }

    private void addMarkers() {

        mMap.clear();
        ParseRelation<ParseObject> likeRelation = parseUser.getRelation("likes");
        likeRelation.getQuery().findInBackground(new FindCallback<ParseObject>() {
            int i = 0;
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    while (i < objects.size()) {
                        Restaurant restaurant = (Restaurant)
                                objects.get(i);
                        LatLng latLng = new LatLng(restaurant.getLocation().getLatitude(), restaurant.getLocation().getLongitude());
                        String title = restaurant.getName();
                        String address = restaurant.getLocation().getDisplayAddress();

                        mapped.put(title, restaurant);


                        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                        if (bounds.contains(latLng)) {
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(title)
                                    .snippet(address));
                        }

                    i++;
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

                Collection<Restaurant> restaurants = mapped.values();
                mMap.clear();
                for (Restaurant restaurant : restaurants) {
                    LatLng latLng = new LatLng(restaurant.getLocation().getLatitude(), restaurant.getLocation().getLongitude());
                    if (bounds.contains(latLng)) {
                        String title = restaurant.getName();
                        String address = restaurant.getLocation().getDisplayAddress();

                        mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(title)
                            .snippet(address));
                    }
                }

            }
        });


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                String title = marker.getTitle();
                Restaurant restaurant = mapped.get(title);

                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);

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

                startActivity(intent);

            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker mark) {
                mark.showInfoWindow();

                return true; //must be true, if not, it will execute the default code after yours
            }

        });

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
