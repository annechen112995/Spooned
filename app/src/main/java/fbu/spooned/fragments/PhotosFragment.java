package fbu.spooned.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import fbu.spooned.R;
import fbu.spooned.adapters.PhotosAdapter;
import fbu.spooned.clients.FsqClient;
import fbu.spooned.models.Photo;

/**
 * Created by jennytlee on 7/19/16.
 */
public class PhotosFragment extends Fragment {

    private ArrayList<Photo> photos;
    private PhotosAdapter aPhotos;

    RecyclerView rvPhotos;
    String photoId;
    FsqClient fsqClient;

    public static PhotosFragment newInstance(String id) {
        PhotosFragment photosFragment = new PhotosFragment();
        Bundle args = new Bundle();
        args.putString("tipId", id);
        photosFragment.setArguments(args);
        return photosFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        photoId = getArguments().getString("tipId", "");

        photos = new ArrayList<>();
        aPhotos = new PhotosAdapter(getActivity(), photos);
        fsqClient = new FsqClient();


        fetchPhotos();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        rvPhotos = (RecyclerView) view.findViewById(R.id.rvPhotos);
        rvPhotos.setAdapter(aPhotos);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvPhotos.setLayoutManager(layoutManager);

        return view;
    }

    private void fetchPhotos() {
        fsqClient.getVenuePhotos(photoId, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    ArrayList<Photo> photoList = Photo.fromJson(response.getJSONObject("response").getJSONObject("photos").getJSONArray("items"));
                    photos.addAll(photoList);
                    aPhotos.notifyDataSetChanged();

                    aPhotos.setOnItemClickListener(new PhotosAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View itemView, int position) {
                            Photo photo = photos.get(position);
                            String photoId = photo.getPhotoUrl();
                            String name = photo.getFullName();
                            String timeStamp = photo.getTimeStamp();
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            FullPhotoDialogFragment fullPhotoDialogFragment = FullPhotoDialogFragment.newInstance(photoId, name, timeStamp);
                            fullPhotoDialogFragment.show(fm, "fragment_full_photo");
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });

    }

}
