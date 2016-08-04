package fbu.spooned.clients;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by jennytlee on 7/13/16.
 */
public class FsqClient {

    private static final String API_BASE_URL = "https://api.foursquare.com/v2/";
    final String CLIENT_ID = "M1VSIYC0XW2GFENDNBPXSF3RAUHMY34IY2L14MZL1P1UNPEC";
    final String CLIENT_SECRET = "CEFILKY41KTAKUDPB55R4Y5BDI0UTOELYAYCEDYCADRQLOEY";
    private static final String VERSION = "20160713";


    private AsyncHttpClient client;

    public FsqClient() {
        this.client = new AsyncHttpClient();
    }

    private String getApiUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }


    // Method for accessing the search API
//    public void exploreVenues(JsonHttpResponseHandler handler) {
//        String url = getApiUrl("venues/explore");
//
//        RequestParams params = new RequestParams();
//        params.put("client_id", CLIENT_ID);
//        params.put("client_secret", CLIENT_SECRET);
//        params.put("v", VERSION);
//        params.put("ll", latitude + "," + longitude);
//        params.put("limit", 10 );
//
//        client.get(url, params, handler);
//    }

    public void searchVenues(double latitude, double longitude, String query, JsonHttpResponseHandler handler) {
        String url = getApiUrl("venues/search");
        RequestParams params = new RequestParams();
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);
        params.put("v", VERSION);
        params.put("ll", latitude + "," + longitude);
        params.put("query", query);

        client.get(url, params, handler);
    }

    public void getVenueById(String id, JsonHttpResponseHandler handler) {
        String url = getApiUrl("venues/" + id);
        RequestParams params = new RequestParams();
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);
        params.put("v", VERSION);
        client.get(url, params, handler);
    }

    public void getTipsById(String id, int offset, JsonHttpResponseHandler handler) {
        String url = getApiUrl("venues/" + id + "/tips");

        Log.d("url list", url);

        RequestParams params = new RequestParams();
        params.put("sort", "recent");
        params.put("limit", 1000);
        params.put("offset", offset);
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);
        params.put("v", VERSION);

        client.get(url, params, handler);
    }

    public void getVenuePhotos(String venueId, JsonHttpResponseHandler handler) {
        String url = getApiUrl("venues/" + venueId + "/photos");

        RequestParams params = new RequestParams();
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);
        params.put("v", VERSION);

        client.get(url, params, handler);
    }

    public void getTip(String tipId, JsonHttpResponseHandler handler) {
        String url = getApiUrl("tips/" + tipId);

        RequestParams params = new RequestParams();
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);
        params.put("v", VERSION);

        client.get(url, params, handler);
    }

    /*
    public void getUserPhotos(String userId, JsonHttpResponseHandler handler) {
        String url = getApiUrl("users/" + userId + "/photos");

        RequestParams params = new RequestParams();
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);
        params.put("v", VERSION);

        client.get(url, params, handler);
    }*/

}
