package fbu.spooned.models;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jennytlee on 7/13/16.
 */
public class FsqVenue {

    private String venueId;
    private String name;
    private String phone;
    private String address;
    private String category;
    private String webUrl;
    private double rating;
    private boolean openNow;
    private String photoUrl;

    private String openHours;
    private int price;

    private int photoCount;
    private int tipCount;

    private LatLng latlng;

    private ArrayList<Tip> tips;

    public String getVenueId() {
        return venueId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public String getCategory() {
        return category;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public int getPrice() {
        return price;
    }

    public double getRating() {
        return rating;
    }

    public String getOpenHours() {
        return openHours;
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public int getPhotoCount() {
        return photoCount;
    }

    public int getTipCount() {
        return tipCount;
    }

    public ArrayList<Tip> getTips() {
        return tips;
    }

    public static FsqVenue fromJson(JSONObject venueObject) {

        FsqVenue venue = new FsqVenue();

        try {
            //JSONObject venueObject = jsonObject.getJSONObject("venue");
            //JSONArray tipArray = jsonObject.getJSONArray("tips");
            //Log.d("DEBUG", tipArray.toString());

            //venue.tips = Tip.fromJson(tipArray);

            venue.venueId = venueObject.getString("id");
            venue.name = venueObject.getString("name");

            if (venueObject.has("photos")) {
                JSONObject photoObject = venueObject.getJSONObject("photos");
                venue.photoCount = photoObject.getInt("count");
            } else {
                venue.photoCount = 0;
            }

            if (venueObject.has("tips")) {
                JSONObject tipObject = venueObject.getJSONObject("tips");
                venue.tipCount = tipObject.getInt("count");
            } else {
                venue.tipCount = 0;
            }

/*          venue.phone = venueObject.getJSONObject("contact").getString("phone");

            JSONArray addressTemp = venueObject.getJSONObject("location").getJSONArray("formattedAddress");
            String addressT = "";
            for (int i = 0; i < addressTemp.length(); i++) {
                addressT = addressT + addressTemp.getString(i).replaceAll("\"", "") + " ";
            }
            venue.address = addressT.substring(0, addressT.length() - 1);

            double lat = venueObject.getJSONObject("location").getDouble("lat");
            double lng = venueObject.getJSONObject("location").getDouble("lng");
            venue.latlng = new LatLng(lat, lng);

            venue.category = venueObject.getJSONArray("categories").getJSONObject(0).getString("name");
            venue.webUrl = venueObject.getString("url");
            venue.price = venueObject.getJSONObject("price").getInt("tier");
            venue.rating = venueObject.getDouble("rating");

            JSONObject hours = venueObject.getJSONObject("hours");
            venue.openHours = hours.getString("status");
            venue.openNow = hours.getBoolean("isOpen");

            JSONObject photoObject = venueObject.getJSONObject("photo");
            String prefix = photoObject.getString("prefix");
            String suffix = photoObject.getString("suffix");
            String width = photoObject.getString("width");
            String height = photoObject.getString("height");
            venue.photoUrl = prefix + "original" + suffix;
*/

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return venue;

    }

    public static ArrayList<FsqVenue> fromJson(JSONArray jsonArray) {

        ArrayList<FsqVenue> venues = new ArrayList<>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject venueJson;

            try {
                venueJson = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

            FsqVenue venue = FsqVenue.fromJson(venueJson);
            if (venue != null) {
                venues.add(venue);
            }
        }

        return venues;
    }


}
