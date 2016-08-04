package fbu.spooned.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.FacebookSdk;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

import fbu.spooned.R;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // fonts
        Typeface boldType = Typeface.createFromAsset(this.getAssets(),"ChampBold.ttf");

        if (ParseUser.getCurrentUser() != null) {
            loginButton = (Button) findViewById(R.id.login_button);
            loginButton.setText("logged in");
            loginButton.setTypeface(boldType);
//            Log.d("DEBUG", ParseUser.getCurrentUser().getUsername());
            Intent i = new Intent(LoginActivity.this, SwipeActivity.class);
            startActivity(i);
        }

        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG", "clicked!");
                ArrayList<String> permissions = new ArrayList();
                permissions.add("email");
                permissions.add("user_friends");

                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions,
                        new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException err) {
                                if (err != null) {
                                    Log.d("MyApp", "Uh oh. Error occurred" + err.toString());
                                } else if (user == null) {
                                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                                } else if (user.isNew()) {
                                    saveNewUser(user.getUsername());
                                    //getFriendsInBackground();
                                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                                    Intent i = new Intent(LoginActivity.this, SwipeActivity.class);
                                    startActivity(i);
                                } else {
                                    Log.d("MyApp", "User logged in through Facebook!");
                                    Intent i = new Intent(LoginActivity.this, SwipeActivity.class);
                                    startActivity(i);
                                }
                            }
                        });
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void saveNewUser(String username) {
        Log.d("DEBUG", "new user!");
        ParseUser parseUser = ParseUser.getCurrentUser();
        parseUser.setUsername(username);
        parseUser.put("fbId", parseUser.getUsername());
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("DEBUG", "Saved new user!");
            }
        });
    }

    /*private void getFriendsInBackground() {
        final GraphRequest requestFriend = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray objects, GraphResponse response) {

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        requestFriend.setParameters(parameters);
        requestFriend.executeAsync();
    } */
}