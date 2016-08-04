package fbu.spooned.application;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

import fbu.spooned.models.Location;
import fbu.spooned.models.Restaurant;

/**
 * Created by eshen on 7/8/16.
 */
public class SpoonedApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Restaurant.class);

        //ParseObject.registerSubclass(FsqVenue.class);
        ParseObject.registerSubclass(Location.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("APP_ID")
                .clientKey(null)
                .server("https://spooned.herokuapp.com/parse/").build());
        ParseFacebookUtils.initialize(this);
//        uncomment this to check if Parse is working
//        ParseObject testObject = new ParseObject("TestObject");
//        testObject.put("foo", "bar");
//        testObject.saveInBackground(new SaveCallback() {
//            public void done(ParseException e) {
//                if (e != null) {
//                    e.printStackTrace();
//                }
//                else {
//                    Log.d("DEBUG", "success");
//                }
//            }
//        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }
}