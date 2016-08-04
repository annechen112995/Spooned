package fbu.spooned.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import fbu.spooned.EndlessRecyclerViewScrollListener;
import fbu.spooned.R;
import fbu.spooned.adapters.SavedRestaurantAdapter;
import fbu.spooned.fragments.DeleteAllDialogFragment;
import fbu.spooned.models.Restaurant;



public class SavedActivity extends AppCompatActivity implements DeleteAllDialogFragment.DeleteAllDialogFragmentListener {

//    SmartFragmentStatePagerAdapter adapterViewPager;
//    ViewPager vpPager;

    ParseUser parseUser;
    private SavedRestaurantAdapter aRestaurants;
    private SavedRestaurantAdapter aSearched;
    private RecyclerView rvSaved;
    private EditText etSearch;
    private ImageView ivSearch;
    private Button btnAllSaved;
    private FloatingActionButton fabMap;
    ArrayList<Restaurant> restaurants;
    ProgressBar progressBar;
    private static final String TAG = "SavedListFragment";

    public ArrayList<String> likes;
    private ArrayList<Restaurant> filteredRestaurants;
    ArrayList<Restaurant> searched;

    int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        rvSaved = (RecyclerView) findViewById(R.id.rvSaved);
        etSearch = (EditText) findViewById(R.id.etSearch);
        ivSearch = (ImageView) findViewById(R.id.ivSearch);
        btnAllSaved = (Button) findViewById(R.id.btnAllSaved);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        likes = getIntent().getStringArrayListExtra("likes");


        Typeface regType = Typeface.createFromAsset(this.getAssets(),"Champ.ttf");
        etSearch.setTypeface(regType);
        etSearch.setImeActionLabel("Go", KeyEvent.KEYCODE_ENTER);
        btnAllSaved.setTypeface(regType);

        restaurants = new ArrayList<>();

        //Construct the adapter from data source
        aRestaurants = new SavedRestaurantAdapter(this, restaurants);
        parseUser = ParseUser.getCurrentUser();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvSaved.setLayoutManager(layoutManager);

        rvSaved.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                currentPage++;
                getLikes(currentPage);
            }
        });

        //Connect adapter to view
        rvSaved.setAdapter(aRestaurants);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                ParseRelation likeRelation = parseUser.getRelation("likes");
                Restaurant restaurant = restaurants.get(viewHolder.getAdapterPosition());
                likeRelation.remove(restaurant);
                parseUser.saveInBackground();
                Toast.makeText(getApplicationContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                restaurants.remove(restaurant);
                aRestaurants.notifyDataSetChanged();

                currentPage++;
                getLikes(currentPage);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvSaved);

        ivSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onSearch();
            }
        });

        etSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    onSearch();
                    return true;
                }
                return false;
            }
        });


        btnAllSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvSaved.setAdapter(aRestaurants);
                etSearch.setText("");
                btnAllSaved.setVisibility(View.INVISIBLE);
            }
        });

        aRestaurants.setOnItemClickListener(new SavedRestaurantAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Log.d("DEBUG", "clicked");

                Intent intent = new Intent (SavedActivity.this, DetailActivity.class);

                Restaurant restaurant = restaurants.get(position);

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

        SpannableString s = new SpannableString("Favorites");
        s.setSpan(new fbu.spooned.models.TypefaceSpan(this, "Champ.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setTitle(s);

        fabMap = (FloatingActionButton) findViewById(R.id.fabMap);
        fabMap.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorPrimary)));
        fabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MapPinActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(i);
            }
        });

        getLikes(0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_saved, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_delete:
                FragmentManager fm = getSupportFragmentManager();
                DeleteAllDialogFragment deleteDialog = new DeleteAllDialogFragment();
                deleteDialog.show(fm, "fragment_delete_all");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteAll() {
        final ParseRelation likeRelation = parseUser.getRelation("likes");

        likeRelation.getQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for(ParseObject object : objects) {
                        likeRelation.remove(object);
                        parseUser.saveInBackground();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
        restaurants.clear();
        aRestaurants.notifyDataSetChanged();

    }

    public ArrayList<Restaurant> filterRestaurants(String query) {
        progressBar.setVisibility(View.VISIBLE);
        filteredRestaurants = new ArrayList<>();
        ParseQuery restQuery = ParseQuery.getQuery("Restaurant");
        if (query.length() > 0) {
            // convert to title case
            String queryString = query.substring(0, 1).toUpperCase() + query.substring(1);
            Log.d(TAG, queryString);
            try {
                List objects = restQuery.whereContains("name", queryString).find();
                Log.d(TAG, objects.toString());
                if (!objects.isEmpty()) {
                    for (Object o : objects) {
                        Restaurant r = (Restaurant) o;
                        String id = r.getId();
                        if (likes.contains(id)) {
                            Log.d(TAG, r.getName());
                            filteredRestaurants.add(r);
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        progressBar.setVisibility(View.INVISIBLE);
        return filteredRestaurants;

    }

    public void onSearch() {
        searched = filterRestaurants(etSearch.getText().toString());
        aSearched = new SavedRestaurantAdapter(getApplicationContext(), searched);
        rvSaved.setAdapter(aSearched);
        btnAllSaved.setVisibility(View.VISIBLE);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
    }

    public void getLikes(int offset) {
        Log.d(TAG, "getting likes");
        progressBar.setVisibility(View.VISIBLE);
        ParseRelation<ParseObject> likeRelation = parseUser.getRelation("likes");
        ParseQuery likeQuery = likeRelation.getQuery();
        likeQuery.setSkip(offset * 10);
        likeQuery.setLimit(10);
        likeQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null) {
                    Log.d("DEBUG", "error :-(");
                }
                else {
                    for (ParseObject object: objects)
                    {
                        Restaurant r = (Restaurant) object;
                        r.setReaction("like");
                        restaurants.add(r);
                        aRestaurants.notifyDataSetChanged();
                        Log.d("SavedActivity", ((Restaurant) object).getName());
                    }
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });


    }

    @Override
    public void onClickButton(boolean decision) {
        if (decision) {
            deleteAll();
        }
    }
}
