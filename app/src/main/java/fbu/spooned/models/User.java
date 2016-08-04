package fbu.spooned.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jennytlee on 7/13/16.
 */
public class User extends com.yelp.clientlib.entities.User {

    private String id;
    private String profileImage;
    private String name;


    @Override
    public String id() {
        return id;
    }

    @Override
    public String imageUrl() {
        return profileImage;
    }

    @Override
    public String name() {
        return name();
    }


    public static User fromJSON(JSONObject jsonObject) {
        User u = new User();

        try {
            u.name = jsonObject.getString("name");
            u.id = jsonObject.getString("id");
            String profileimg = jsonObject.getString("image_url");
            u.profileImage = profileimg.replace("ms.jpg", "o.jpg");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return u;
    }

}
