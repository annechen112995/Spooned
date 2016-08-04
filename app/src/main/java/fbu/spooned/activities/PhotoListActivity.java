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
import android.widget.GridView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import fbu.spooned.R;
import fbu.spooned.adapters.PhotosGridAdapter;
import fbu.spooned.clients.FsqClient;
import fbu.spooned.fragments.FullPhotoDialogFragment;
import fbu.spooned.models.Photo;

public class PhotoListActivity extends AppCompatActivity {

    private ArrayList<Photo> photos;
    private PhotosGridAdapter aPhotos;
    GridView gvAllPhotos;
    String tipId;
    FsqClient fsqClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        gvAllPhotos = (GridView) findViewById(R.id.gvAllPhotos);

        tipId = getIntent().getStringExtra("tipId");

        photos = new ArrayList<>();
        aPhotos = new PhotosGridAdapter(this, photos);
        fsqClient = new FsqClient();

        SpannableString s = new SpannableString("All Photos");
        s.setSpan(new fbu.spooned.models.TypefaceSpan(this, "Champ.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setTitle(s);

        populatePhotos();
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

    private void populatePhotos() {

        fsqClient.getVenuePhotos(tipId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {

                    photos = Photo.fromJson(response.getJSONObject("response").getJSONObject("photos").getJSONArray("items"));

                    gvAllPhotos.setAdapter(aPhotos);
                    aPhotos.clear();
                    aPhotos.addAll(photos);

                    gvAllPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String photoUrl = photos.get(i).getPhotoUrl();
                            String name = photos.get(i).getFullName();
                            String timeStamp = photos.get(i).getTimeStamp();
                            FragmentManager fm = getSupportFragmentManager();
                            FullPhotoDialogFragment fullPhotoDialogFragment = FullPhotoDialogFragment.newInstance(photoUrl, name, timeStamp);
                            fullPhotoDialogFragment.show(fm, "fragment_full_photo");
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("debug", throwable.toString());
            }
        });

    }



}
