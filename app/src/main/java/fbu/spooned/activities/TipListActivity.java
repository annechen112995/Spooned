package fbu.spooned.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import fbu.spooned.EndlessScrollListener;
import fbu.spooned.R;
import fbu.spooned.adapters.TipArrayAdapter;
import fbu.spooned.clients.FsqClient;
import fbu.spooned.fragments.FullTipDialogFragment;
import fbu.spooned.models.Tip;

public class TipListActivity extends AppCompatActivity {

    private ArrayList<Tip> tips;
    private TipArrayAdapter aTips;
    ListView lvAllReviews;
    String tipId;
    FsqClient fsqClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_list);

        lvAllReviews = (ListView) findViewById(R.id.lvAllReviews);

        lvAllReviews.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                populateMoreReviews(100);
                return true;
            }
        });

        tipId = getIntent().getStringExtra("tipId");

        tips = new ArrayList<>();
        aTips = new TipArrayAdapter(this, tips);
        fsqClient = new FsqClient();

        SpannableString s = new SpannableString("All Reviews");
        s.setSpan(new fbu.spooned.models.TypefaceSpan(this, "Champ.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setTitle(s);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateReviews();

    }

    private void populateReviews() {

        fsqClient.getTipsById(tipId, 0, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    tips = Tip.fromJson(response.getJSONObject("response").getJSONObject("tips").getJSONArray("items"));

                    lvAllReviews.setAdapter(aTips);

                    aTips.clear();
                    aTips.addAll(tips);

                    lvAllReviews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String tipId = tips.get(i).getTipId();
                            FragmentManager fm = getSupportFragmentManager();
                            FullTipDialogFragment fullTipDialogFragment = FullTipDialogFragment.newInstance(tipId);
                            fullTipDialogFragment.show(fm, "fragment_full_tip");

                        }
                    });

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

    private void populateMoreReviews(int offset) {

        fsqClient.getTipsById(tipId, offset, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    tips = Tip.fromJson(response.getJSONObject("response").getJSONObject("tips").getJSONArray("items"));
                    Log.d("DEBUG", tips.toString());


                    lvAllReviews.setAdapter(aTips);

                    aTips.clear();
                    aTips.addAll(tips);

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

}
