package fbu.spooned.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by eshen on 7/8/16.
 */
@ParseClassName("Restaurant")
public class Restaurant extends ParseObject{
    private String reaction;

    public double getRating() {
        return getDouble("rating");
    }
    public String getPhone() {
        return getString("phone");
    }
    public Location getLocation(){
        return (Location) getParseObject("location");
    }
    public boolean isClosed() {
        return getBoolean("closed");
    }
    public String getCategories() {
        return getString("categories");
    }
    public String getName() {
        return getString("name");
    }
    public String getImageUrl() {
        return getString("image_url").replace("ms.jpg", "o.jpg");
    }
    public String getId() {
        return getString("id");
    }
    public String getReaction() { return reaction; }

    public Restaurant() {
    }


    public Restaurant(String id, String name, String imageUrl,
                      String categories, double rating, boolean closed, String phone, Location loc, String reaction) {
        super();
        setId(id);
        setName(name);
        setImageUrl(imageUrl);
        setCategories(categories);
        setRating(rating);
        setClosed(closed);
        setPhone(phone);
        setLocation(loc);
        setReaction(reaction);
    }
    public void setId(String id) {
        put("id", id);
    }
    public void setClosed(boolean closed) {
        put("closed", closed);
    }
    public void setName(String name) {
        put("name", name);
    }
    public void setImageUrl(String imageUrl) {
        String modUrl = imageUrl.replace("ms.jpg", "o.jpg");
        put("image_url", modUrl);
    }
    public void setCategories(String categories) {
        put("categories", categories);
    }
    public void setRating(double rating) {
        put("rating", rating);
    }
    public void setPhone(String phone) {
        if (phone == null) {
            put("phone", "");
        }
        else {
            put("phone", phone);
        }
    }
    public void setLocation(Location loc) {
        put("location", loc);
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

}
