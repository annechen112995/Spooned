package fbu.spooned.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;

import java.util.ArrayList;

import fbu.spooned.R;
import fbu.spooned.activities.SwipeActivity;
import fbu.spooned.models.Restaurant;

/**
 * Created by jennytlee on 7/25/16.
 */
public class SavedRestaurantAdapter extends RecyclerView.Adapter<SavedRestaurantAdapter.ViewHolder> {

    ParseUser parseUser = ParseUser.getCurrentUser();
    ArrayList<Restaurant> restaurants;
    Context mContext;


    // Define listener member variable
    private static OnItemClickListener listener;
    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPreview;
        TextView tvRestNameFrag;
        TextView tvRatingNum;
        TextView tvDistanceFrag;
        TextView tvAddressFrag;

        public ViewHolder(final View itemView) {
            super(itemView);

            Typeface regType = Typeface.createFromAsset(itemView.getContext().getAssets(),"Champ.ttf");
            Typeface boldType = Typeface.createFromAsset(itemView.getContext().getAssets(),"ChampBold.ttf");

            tvRestNameFrag = (TextView) itemView.findViewById(R.id.tvRestNameFrag);
            tvRestNameFrag.setTypeface(boldType);
            tvRatingNum = (TextView) itemView.findViewById(R.id.tvRatingNum);
            tvRatingNum.setTypeface(regType);
            tvAddressFrag = (TextView) itemView.findViewById(R.id.tvAddressFrag);
            tvAddressFrag.setTypeface(regType);
            tvDistanceFrag = (TextView) itemView.findViewById(R.id.tvDistanceFrag);
            tvDistanceFrag.setTypeface(boldType);
            ivPreview = (ImageView) itemView.findViewById(R.id.ivPreview);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onItemClick(itemView, getLayoutPosition());
                    }
                }
            });
        }
    }

    public SavedRestaurantAdapter(Context context, ArrayList<Restaurant> restaurants) {
        this.mContext = context;
        this.restaurants = restaurants;
    }


    @Override
    public SavedRestaurantAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View convertView = inflater.inflate(R.layout.item_saved, parent, false);
        ViewHolder viewHolder = new ViewHolder(convertView);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(SavedRestaurantAdapter.ViewHolder holder, int position) {

        final Restaurant restaurant = restaurants.get(position);

        SharedPreferences preferences = mContext.getSharedPreferences(SwipeActivity.PREFS_NAME, 0);
        double currentLat = Double.longBitsToDouble(preferences.getLong("current_latitude", 0));
        double currentLng = Double.longBitsToDouble(preferences.getLong("current_longitude", 0));

        Location currentLocation = new Location("chosenLocation");
        currentLocation.setLatitude(currentLat);
        currentLocation.setLongitude(currentLng);

        Location restLocation = new Location("restLocation");
        restLocation.setLatitude(restaurant.getLocation().getLatitude());
        restLocation.setLongitude(restaurant.getLocation().getLongitude());

        double distance = (long) currentLocation.distanceTo(restLocation);
        Log.d("DEBUG LOCATION", String.valueOf(distance) + currentLocation.toString());

        double distanceInMiles = distance * 0.000621371;
        holder.tvDistanceFrag.setText(String.format("%.1f", distanceInMiles) + " miles away");

        holder.tvRestNameFrag.setText(restaurant.getName());
        holder.tvRatingNum.setText(String.valueOf(restaurant.getRating()));
        holder.tvAddressFrag.setText(restaurant.getLocation().getDisplayAddress());

        String imageUrl = restaurant.getImageUrl();
        Glide.with(mContext).load(imageUrl).placeholder(R.drawable.logo).centerCrop().into(holder.ivPreview);



    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }
}
