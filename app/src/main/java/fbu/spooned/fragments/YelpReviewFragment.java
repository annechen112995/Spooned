package fbu.spooned.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yelp.clientlib.entities.Business;

import java.util.ArrayList;

import fbu.spooned.R;
import fbu.spooned.adapters.ReviewsArrayAdapter;
import fbu.spooned.clients.YelpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jennytlee on 7/13/16.
 */
public class YelpReviewFragment extends Fragment {

    private ArrayList<com.yelp.clientlib.entities.Review> reviews;
    private ReviewsArrayAdapter aReviews;
    ListView lvReviews;
    String id;
    YelpClient client;

    public static YelpReviewFragment newInstance(String id) {
        YelpReviewFragment yelpReviewFragment = new YelpReviewFragment();
        Bundle args = new Bundle();
        args.putString("id", id);
        yelpReviewFragment.setArguments(args);
        return yelpReviewFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = getArguments().getString("id", "");

        reviews = new ArrayList<>();
        aReviews = new ReviewsArrayAdapter(getActivity(), reviews);


        grabReviews(id);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);
        lvReviews = (ListView) view.findViewById(R.id.lvReviews);
        lvReviews.setAdapter(aReviews);
        return view;
    }

    private void grabReviews(String id) {

        client = new YelpClient();
        client.findBusiness(id, new Callback<Business>() {
            @Override
            public void onResponse(Call<Business> call, Response<Business> response) {
                reviews = response.body().reviews();
                Log.d("DEBUG", response.toString());
                aReviews.addAll(reviews);
            }

            @Override
            public void onFailure(Call<Business> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

}
