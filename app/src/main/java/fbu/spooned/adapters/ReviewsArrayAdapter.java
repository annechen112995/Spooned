package fbu.spooned.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import fbu.spooned.R;

/**
 * Created by jennytlee on 7/13/16.
 */
public class ReviewsArrayAdapter extends ArrayAdapter<com.yelp.clientlib.entities.Review> {

    TextView tvUsername;
    //TextView tvUserId;
    TextView tvExcerpt;
    TextView tvRating;
    ImageView ivProfile;
    TextView tvTimestamp;

    public ReviewsArrayAdapter(Context context, ArrayList<com.yelp.clientlib.entities.Review> reviews) {
        super(context, android.R.layout.simple_list_item_1, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        com.yelp.clientlib.entities.Review review = getItem(position);
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_review, parent, false);

        //fonts
        Typeface regType = Typeface.createFromAsset(getContext().getAssets(),"Champ.ttf");
        Typeface boldType = Typeface.createFromAsset(getContext().getAssets(),"ChampBold.ttf");

        tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
        tvUsername.setTypeface(regType);
//        tvUserId = (TextView) convertView.findViewById(R.id.tvUserId);
        tvExcerpt = (TextView) convertView.findViewById(R.id.tvExcerpt);
        tvExcerpt.setTypeface(regType);
       // tvRating = (TextView) convertView.findViewById(R.id.tvRating);
        tvTimestamp = (TextView) convertView.findViewById(R.id.tvTimestamp);
        tvTimestamp.setTypeface(regType);
        ivProfile = (ImageView) convertView.findViewById(R.id.ivProfile);

        tvUsername.setText(review.user().name());
        //tvUserId.setText(review.user().id());
        tvExcerpt.setText(review.excerpt());
       // tvRating.setText(String.valueOf(review.rating()));
        tvTimestamp.setText(String.valueOf(review.timeCreated()));

        Glide.with(getContext()).load(review.user().imageUrl()).fitCenter().into(ivProfile);

        return convertView;
    }


}
