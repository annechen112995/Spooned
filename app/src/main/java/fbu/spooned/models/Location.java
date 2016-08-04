package fbu.spooned.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

/**
 * Created by eshen on 7/13/16.
 */
@ParseClassName("Location")
public class Location extends ParseObject{
    public String getDisplayAddress() {
        try {
            return fetchIfNeeded().getString("display_address");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    public double getLongitude() {
        try {
            return fetchIfNeeded().getDouble("longitude");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    public double getLatitude() {
        try {
            return fetchIfNeeded().getDouble("latitude");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0.0;
    }


    public Location() {
    }

    public Location(double lat, double lng, String displayAddress) {

        setLongitude(lng);
        setLatitude(lat);
        setDisplayAddress(displayAddress);
    }
    public void setLongitude(double lng) {
        put("longitude", lng);
    }
    public void setLatitude(double lat) {
        put("latitude", lat);
    }
    public void setDisplayAddress(String displayAddress) {
        put("display_address", displayAddress);
    }
}
