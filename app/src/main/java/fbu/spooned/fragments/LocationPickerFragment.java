package fbu.spooned.fragments;

import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import fbu.spooned.R;

/**
 * Created by eshen on 7/21/16.
 */
public class LocationPickerFragment extends DialogFragment{
    private static final String TAG = "LocationPickerFragment";

    TextView tvLocationLabel;
    TextView tvRadiusLabel;
    TextView tvMiles;

    private ImageButton btnApply;
    private ImageButton ibCurrentLocation;
    private Location currentLoc;
    private Location chosenLoc;
    private static View view;
    private NumberPicker npDistance;
    private int radius;
    private String locName;

    public interface LocationPickerDialogListener {
        void onFinishPickDialog(Location currentLocation, Location chosenLocation, int radius, String locName);
    }

    public LocationPickerFragment() {

    }

    public static LocationPickerFragment newInstance(Location chosenLocation, Location currentLocation, int radius, String locName) {
        LocationPickerFragment frag = new LocationPickerFragment();
        Bundle args = new Bundle();
        args.putParcelable("chosen_location", chosenLocation);
        args.putParcelable("current_location", currentLocation);
        args.putInt("radius", radius);
        args.putString("location_name", locName);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("chosen_location", chosenLoc);
        outState.putParcelable("current_location", currentLoc);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
            try {
                view = inflater.inflate(R.layout.fragment_location_picker, container);
            }
            catch (InflateException e){

            }
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Fonts
        Typeface regType = Typeface.createFromAsset(getContext().getAssets(),"Champ.ttf");
        Typeface boldType = Typeface.createFromAsset(getContext().getAssets(),"ChampBold.ttf");

        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        currentLoc = getArguments().getParcelable("current_location");
        chosenLoc = getArguments().getParcelable("chosen_location");
        radius = getArguments().getInt("radius");
        locName = getArguments().getString("location_name");
        autocompleteFragment.setText(locName);

        tvLocationLabel = (TextView) view.findViewById(R.id.tvLocationLabel);
        tvLocationLabel.setTypeface(boldType);

        tvRadiusLabel = (TextView) view.findViewById(R.id.tvRadiusLabel);
        tvRadiusLabel.setTypeface(boldType);

        tvMiles = (TextView) view.findViewById(R.id.tvMiles);
        tvMiles.setTypeface(regType);

        btnApply = (ImageButton) view.findViewById(R.id.btnApply);
        ibCurrentLocation = (ImageButton) view.findViewById(R.id.ibCurrentLocation);

        npDistance = (NumberPicker) view.findViewById(R.id.npDistance);
        npDistance.setValue(radius);

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationPickerDialogListener listener = (LocationPickerDialogListener) getActivity();
                radius = npDistance.getValue();
                listener.onFinishPickDialog(currentLoc, chosenLoc, radius, locName);
                dismiss();
            }
        });

        ibCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chosenLoc = currentLoc;
                autocompleteFragment.setText("Current Location");
                locName = "Current Location";
            }
        });


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                chosenLoc = new Location ("");
                chosenLoc.setLatitude(place.getLatLng().latitude);
                chosenLoc.setLongitude(place.getLatLng().longitude);
                locName = place.getName().toString();
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }



}
