package fbu.spooned.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import fbu.spooned.R;
import fbu.spooned.clients.FsqClient;
import fbu.spooned.fragments.PhotosFragment;
import fbu.spooned.fragments.TipListFragment;
import fbu.spooned.fragments.YelpReviewFragment;
import fbu.spooned.models.FsqVenue;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    String id;
    AsyncHttpClient locuClient;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private static final String BASE_URL = "https://api.locu.com/v1_0/venue/search/?name=";
    private static final String API_KEY = "&api_key=8e6c13b459f45e1653bd1a5386aaef0bfcf08999";
    private static final String TAG = "DetailActivity";
    FsqClient fsqClient;

    ArrayList<FsqVenue> venues;

    String thumbnailUrl;

    ImageView ivHeader;

    String webUrl;
    Button btnWeb;
    String phoneNum;
    String rawNum;
    double rating;

    TextView tvName;
    TextView tvHours;
    TextView tvPhoneNum;
    TextView tvType;
    RatingBar ratingBar;
    RatingBar pricingBar;
    TextView tvAddress;
    Button btnMoreReviews;
    Button btnAllPhotos;
    ImageView ivLikedDetail;
    TextView tvDistance;
    ImageView ivDislikedDetail;
    ImageButton ibMarker;

    TextView tvNoVenue;

    FrameLayout flPhotos;
    FrameLayout flReviews;

    FloatingActionButton fabRight;
    FloatingActionButton fabLeft;

    LatLng latlng;
    String venueName;
    double lat;
    double lng;
    int price;

    NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // fonts
        Typeface regType = Typeface.createFromAsset(this.getAssets(), "Champ.ttf");
        Typeface boldType = Typeface.createFromAsset(this.getAssets(), "ChampBold.ttf");

        SpannableString s = new SpannableString("About");
        s.setSpan(new fbu.spooned.models.TypefaceSpan(this, "Champ.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setTitle(s);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment_container);
        mapFragment.getMapAsync(this);


        ivHeader = (ImageView) findViewById(R.id.ivHeader);
        tvName = (TextView) findViewById(R.id.tvName);
        tvName.setTypeface(boldType);
        tvHours = (TextView) findViewById(R.id.tvOpen);
        tvHours.setTypeface(regType);
        tvType = (TextView) findViewById(R.id.tvType);
        tvType.setTypeface(regType);
        tvPhoneNum = (TextView) findViewById(R.id.tvPhoneNum);
        tvPhoneNum.setTypeface(regType);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        pricingBar = (RatingBar) findViewById(R.id.pricingBar);

        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvAddress.setTypeface(regType);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvDistance.setTypeface(boldType);

        btnWeb = (Button) findViewById(R.id.btnWeb);
        btnWeb.setTypeface(regType);
        btnMoreReviews = (Button) findViewById(R.id.btnMoreReviews);
        btnMoreReviews.setTypeface(regType);
        btnAllPhotos = (Button) findViewById(R.id.btnAllPhotos);
        btnAllPhotos.setTypeface(regType);
        ibMarker = (ImageButton) findViewById(R.id.ibMarker);
        Drawable background = ibMarker.getBackground();
        background.setAlpha(190);

        scrollView = (NestedScrollView) findViewById(R.id.scrollView);
        flPhotos = (FrameLayout) findViewById(R.id.flPhotos);
        flReviews = (FrameLayout) findViewById(R.id.flReviews);

        tvNoVenue = (TextView) findViewById(R.id.tvNoVenue);
        tvNoVenue.setTypeface(regType);

        id = getIntent().getStringExtra("id");

        locuClient = new AsyncHttpClient();
        fsqClient = new FsqClient();

        venueName = getIntent().getStringExtra("name");
        tvName.setText(venueName);

        tvType.setText(getIntent().getStringExtra("categories"));
        thumbnailUrl = getIntent().getStringExtra("imageUrl");
        rawNum = getIntent().getStringExtra("phone");
        phoneNum = formatPhoneNumber(rawNum);

        rating = getIntent().getDoubleExtra("rating", 0.0);
        ratingBar.setRating((float) rating);
        tvAddress.setText(getIntent().getStringExtra("display_address"));
        lat = getIntent().getDoubleExtra("latitude", 0.0);
        lng = getIntent().getDoubleExtra("longitude", 0.0);
        latlng = new LatLng(lat, lng);

        if (getIntent().getBooleanExtra("closed", true)) {
            tvHours.setText("Closed");
            tvHours.setTextColor(Color.parseColor("#B33E17"));
            tvPhoneNum.setVisibility(View.INVISIBLE);
            } else {
            tvHours.setText("Open Now");
            tvHours.setTextColor(Color.parseColor("#3EB317"));

            tvPhoneNum.setText(phoneNum);
        }

        SharedPreferences preferences = getSharedPreferences(SwipeActivity.PREFS_NAME, 0);
        double currentLat = Double.longBitsToDouble(preferences.getLong("current_latitude", 0));
        double currentLng = Double.longBitsToDouble(preferences.getLong("current_longitude", 0));

        Location currentLocation = new Location("currentLocation");
        currentLocation.setLatitude(currentLat);
        currentLocation.setLongitude(currentLng);

        Location restLocation = new Location("restLocation");
        restLocation.setLatitude(lat);
        restLocation.setLongitude(lng);

        Log.d(TAG, "rest: " + restLocation.getLatitude() + ", " + restLocation.getLongitude());
        Log.d(TAG, "current: " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude());

        double distance = (long) currentLocation.distanceTo(restLocation);
        Log.d("DEBUG LOCATION", String.valueOf(distance) + currentLocation.toString());

        double distanceInMiles = distance * 0.000621371;
        tvDistance.setText(String.format("%.1f", distanceInMiles) + " miles away");

        ivLikedDetail = (ImageView) findViewById(R.id.ivLikedDetail);
        ivDislikedDetail = (ImageView) findViewById(R.id.ivDislikedDetail);
        String reaction = getIntent().getStringExtra("reaction");

        if (reaction == null) {
            ivLikedDetail.setVisibility(View.INVISIBLE);
            ivDislikedDetail.setVisibility(View.INVISIBLE);
        } else if (reaction.equals("like")) {
            ivLikedDetail.setVisibility(View.VISIBLE);
            ivDislikedDetail.setVisibility(View.INVISIBLE);
        } else if (reaction.equals("dislike")) {
            ivLikedDetail.setVisibility(View.INVISIBLE);
            ivDislikedDetail.setVisibility(View.VISIBLE);
        } else {
        }

        String yelpPhoto = getIntent().getStringExtra("yelpPhoto");
        Picasso.with(this).load(yelpPhoto).placeholder(R.drawable.logo).fit().centerCrop().into(ivHeader);

        getWebsite();
        getPrice();


        getTipsPhotos();

        fabRight = (FloatingActionButton) findViewById(R.id.fabRight);
        fabRight.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorPrimary)));
        fabRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("swipe", 1);
                setResult(SwipeActivity.RESULT_OK, i);
                finish();
            }
        });

        fabLeft = (FloatingActionButton) findViewById(R.id.fabLeft);
        fabLeft.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorAccent)));
        fabLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("swipe", 2);
                setResult(SwipeActivity.RESULT_OK, i);
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(final GoogleMap map) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("DEBUG", "Gotta check for permission");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));

        map.getUiSettings().setZoomControlsEnabled(true);

        map.addMarker(new MarkerOptions()
                .title(venueName)
                .position(latlng));

        ibMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));
            }
        });
    }


    private String buildUrl(String query) {
        return BASE_URL + query + API_KEY;
    }

    public void getPrice() {
        fsqClient.searchVenues(lat, lng, venueName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    venues = FsqVenue.fromJson(response.getJSONObject("response").getJSONArray("venues"));
                    if (!venues.isEmpty()) {
                        FsqVenue venue = venues.get(0);
                        String id = venue.getVenueId();

                        fsqClient.getVenueById(id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                try {

                                    JSONObject venue = response.getJSONObject("response").getJSONObject("venue");

                                    if (response.has("price")) {
                                        JSONObject priceObject = venue.getJSONObject("price");
                                        price = priceObject.getInt("tier");
                                        pricingBar.setRating(price);
                                        return;
                                    } else {
                                        pricingBar.setRating(1);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                            }
                        });
                    } else {
                        pricingBar.setRating(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DetailActivity", responseString);
            }
        });
    }

    private void getWebsite() {
        String tempName = tvName.getText().toString();
        Log.d(TAG, tempName);

        locuClient.get(buildUrl(tempName), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject locuResponse) {
                try {
                    JSONArray object = locuResponse.getJSONArray("objects");
                    if (object.length() == 0) {
                        btnWeb.setEnabled(false);
                        btnWeb.setText("website unavailable");
                    } else {
                        webUrl = object.getJSONObject(0).getString("website_url");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

    }


    public void fetchWebsite(View view) {

        Intent wv = new Intent(getApplicationContext(), WebsiteActivity.class);
        wv.putExtra("webUrl", webUrl);
        startActivity(wv);

    }


    public void getTipsPhotos() {
        fsqClient.searchVenues(lat, lng, venueName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    venues = FsqVenue.fromJson(response.getJSONObject("response").getJSONArray("venues"));

                    if (venues.isEmpty()) {
                        flPhotos.setVisibility(View.INVISIBLE);
                        btnAllPhotos.setVisibility(View.INVISIBLE);
                        flReviews.setVisibility(View.INVISIBLE);
                        btnMoreReviews.setVisibility(View.INVISIBLE);
                        tvNoVenue.setVisibility(View.VISIBLE);
                    } else {

                        FsqVenue venue = venues.get(0);

                        String tipId = venue.getVenueId();

                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        TipListFragment tipListFragment = TipListFragment.newInstance(tipId);
                        ft.replace(R.id.flReviews, tipListFragment);
                        ft.commit();

                        FragmentTransaction photoFt = getSupportFragmentManager().beginTransaction();
                        PhotosFragment photosFragment = PhotosFragment.newInstance(tipId);
                        photoFt.replace(R.id.flPhotos, photosFragment);
                        photoFt.commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                findYelpReview();
            }
        });

    }

    private void findYelpReview() {
        FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
        YelpReviewFragment yelpReviewFragment = YelpReviewFragment.newInstance(id);
        ft2.replace(R.id.flReviews, yelpReviewFragment);
        ft2.commit();
    }


    public void getMoreReviews(View view) {

        fsqClient.searchVenues(lat, lng, venueName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    venues = FsqVenue.fromJson(response.getJSONObject("response").getJSONArray("venues"));
                    FsqVenue venue = venues.get(0);
                    String tipId = venue.getVenueId();

                    Intent i = new Intent(getApplicationContext(), TipListActivity.class);
                    i.putExtra("tipId", tipId);
                    startActivity(i);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Intent i = new Intent(getApplicationContext(), TipListActivity.class);
                i.putExtra("tipId", id);
                startActivity(i);
            }
        });

    }

    public void getAllPhotos(View view) {

        fsqClient.searchVenues(lat, lng, venueName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    venues = FsqVenue.fromJson(response.getJSONObject("response").getJSONArray("venues"));
                    FsqVenue venue = venues.get(0);
                    String tipId = venue.getVenueId();

                    Intent i = new Intent(getApplicationContext(), PhotoListActivity.class);
                    i.putExtra("tipId", tipId);
                    startActivity(i);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Intent i = new Intent(getApplicationContext(), TipListActivity.class);
                i.putExtra("tipId", id);
                startActivity(i);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch reference to the share action provider
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                setupFacebookShareIntent();
                return true;
            }
        });
        // Return true to display menu
        return true;
    }

    public void setupFacebookShareIntent() {
        ShareDialog shareDialog;
        FacebookSdk.sdkInitialize(getApplicationContext());
        shareDialog = new ShareDialog(this);

        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle(venueName)
                .setContentDescription(String.valueOf(tvType.getText()))
                .setContentUrl(Uri.parse(webUrl))
                .build();

        shareDialog.show(linkContent);
    }

    private String formatPhoneNumber(String phone) {

        StringBuilder formatted = new StringBuilder(phone);

        if (phone.length() == 10) {
            formatted.insert(3, "-");
            formatted.insert(phone.length()-3, "-");
        } else {
            return phone;
        }

        return formatted.toString();
    }

    public void callNumber(String phoneNum) {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNum));
        if (callIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(callIntent);
        }

    }
}
