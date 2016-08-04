package fbu.spooned.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import fbu.spooned.R;
import fbu.spooned.adapters.CategoryAdapter;

public class FilterActivity extends AppCompatActivity {

    private static final String TAG = "FilterActivity";

    TextView tvFilterLabel;
    Button btnRemove;

    private HashMap<String, String> restaurantCategories;
    private ArrayAdapter<String> autocompleteAdapter;
    private AutoCompleteTextView autocompleteCategories;
    private String item;
    private Button btnApply;
    private List<String> suggestions;
    private ArrayList<String> selectedCategoriesNames;
    private GridView gvCategories;
    private ArrayAdapter<String> categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        SpannableString s = new SpannableString("Filter restaurants");
        s.setSpan(new fbu.spooned.models.TypefaceSpan(this, "Champ.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setTitle(s);

        // fonts
        Typeface regType = Typeface.createFromAsset(this.getAssets(),"Champ.ttf");
        Typeface boldType = Typeface.createFromAsset(this.getAssets(),"ChampBold.ttf");

        tvFilterLabel = (TextView) findViewById(R.id.tvFilterLabel);
        tvFilterLabel.setTypeface(boldType);
        btnApply = (Button) findViewById(R.id.btnApply);
        btnApply.setTypeface(regType);
        btnRemove = (Button) findViewById(R.id.btnRemove);
        btnRemove.setTypeface(regType);

        restaurantCategories = loadHashMap();
        ArrayList<String> selectedCategoriesAliases = getIntent().getStringArrayListExtra("categories");
        selectedCategoriesNames = new ArrayList<>();
        for (String alias: selectedCategoriesAliases) {
            if (restaurantCategories.containsValue(alias)) {
                selectedCategoriesNames.add(getKeyFromValue(restaurantCategories, alias));
            }
        }

        gvCategories = (GridView) findViewById(R.id.gvCategories);
        categoryAdapter = new CategoryAdapter(this, selectedCategoriesNames);
        gvCategories.setAdapter(categoryAdapter);

        if (restaurantCategories != null) {
            autocompleteCategories = (AutoCompleteTextView) findViewById(R.id.autocomplete_category);
            autocompleteCategories.setTypeface(regType);
            suggestions = new ArrayList<>();
            suggestions.addAll(restaurantCategories.keySet());
            autocompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, suggestions);
            autocompleteCategories.setAdapter(autocompleteAdapter);
            autocompleteCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    item = autocompleteAdapter.getItem(i);
                    addCategory();
                }
            });
            populateHintText();
        }

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmit();
            }
        });

    }

    public String getKeyFromValue(HashMap<String, String> map, String value) {
        for (String k: map.keySet()){
            if (map.get(k).equals(value)) {
                return k;
            }
        }
        return "";
    }

    public void onSubmit() {
        Intent i = new Intent();
        ArrayList<String> selectedCategoriesAliases = new ArrayList<>();
        for (String cat: selectedCategoriesNames) {
            selectedCategoriesAliases.add(restaurantCategories.get(cat));
        }
        i.putStringArrayListExtra("categories", selectedCategoriesAliases);
        setResult(RESULT_OK, i);
        finish();
    }
    @Override
    public void onBackPressed() {
        onSubmit();
        super.onBackPressed();
    }

    public void populateHintText() {
        String hintText = getHintText(suggestions);
        autocompleteCategories.setHint(hintText);
    }

    public String getHintText(List<String> options) {
        Random r = new Random();
        String hintText = "";
        ArrayList<Integer> chosenIndices = new ArrayList<>();
        for (int i = 0; i < 2; i ++) {
            int randomIndex = r.nextInt(options.size());
            while (chosenIndices.contains(randomIndex)) {
                randomIndex = r.nextInt(options.size());
            }
            hintText += options.get(randomIndex);
            chosenIndices.add(randomIndex);
        }
        hintText = options.get(chosenIndices.get(0))
                + ", " + options.get(chosenIndices.get(1))
                + "...";
        return hintText;
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


    public HashMap<String, String> loadHashMap() {
        Log.d(TAG, "ues");
        HashMap<String, String> restaurant_categories = new HashMap<>();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(getAssets().open("restaurant_categories.txt")));
            String line = "";
            while ((line = in.readLine()) != null) {
                String parts[] = line.split(": ");
                restaurant_categories.put(parts[0], parts[1]);
            }
            in.close();
            Log.d(TAG, restaurant_categories.toString());
            return restaurant_categories;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeAllCategories(View view) {
        selectedCategoriesNames.clear();
        categoryAdapter.notifyDataSetChanged();
    }

    public void addCategory() {
        Log.d(TAG, "clicked");
        String selected =  autocompleteCategories.getText().toString();
        if (selectedCategoriesNames.contains(selected)) {
            Toast.makeText(this, "You already selected \"" + selected + "\"", Toast.LENGTH_SHORT).show();
        }
        else {
            String alias = restaurantCategories.get(selected);
            Log.d(TAG, selected + ", " + alias);
            selectedCategoriesNames.add(selected);
            categoryAdapter.notifyDataSetChanged();
        }

        autocompleteCategories.setText("");
        autocompleteCategories.setFocusable(true);
        populateHintText();
    }
}
