package fbu.spooned.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ParseException;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import fbu.spooned.R;
import fbu.spooned.models.Tip;

/**
 * Created by jennytlee on 7/18/16.
 */
public class TipArrayAdapter extends ArrayAdapter<Tip> {

    TextView tvUsername;
    //TextView tvUserId;
    TextView tvExcerpt;
  //  TextView tvRating;
    ImageView ivProfile;
    TextView tvTimestamp;

  //  ImageView ivRatingIcon;

    public TipArrayAdapter(Context context, ArrayList<Tip> tips) {
        super(context, android.R.layout.simple_list_item_1, tips);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tip tip = getItem(position);
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_review, parent, false);

        //fonts
        Typeface regType = Typeface.createFromAsset(getContext().getAssets(),"Champ.ttf");
        Typeface boldType = Typeface.createFromAsset(getContext().getAssets(),"ChampBold.ttf");

        tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
        tvUsername.setTypeface(boldType);
       // tvUserId = (TextView) convertView.findViewById(R.id.tvUserId);
        tvExcerpt = (TextView) convertView.findViewById(R.id.tvExcerpt);
        tvExcerpt.setTypeface(regType);
        //tvRating = (TextView) convertView.findViewById(R.id.tvRating);
        tvTimestamp = (TextView) convertView.findViewById(R.id.tvTimestamp);
        tvTimestamp.setTypeface(regType);
        ivProfile = (ImageView) convertView.findViewById(R.id.ivProfile);

        //ivRatingIcon = (ImageView) convertView.findViewById(R.id.ivRatingIcon);
        //ivRatingIcon.setVisibility(View.INVISIBLE);
       // tvRating.setText("");

        tvUsername.setText(tip.getFirstName() + " " + tip.getLastInitial());
        //tvUserId.setText(tip.getuId());
        tvExcerpt.setText(tip.getText());

        tvTimestamp.setText(tip.getTimestamp());

        String photoUrl = tip.getUserPhotoUrl();

        Glide.with(getContext()).load(photoUrl).placeholder(R.drawable.placeholder_small).fitCenter().into(ivProfile);

        return convertView;
    }


    private String convertUnixTime(int unix) {

        Date date = new Date(unix*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy"); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4")); // give a timezone reference for formating (see comment at the bottom
        String formattedDate = sdf.format(date);
        return formattedDate;
    }


    private String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }


}
