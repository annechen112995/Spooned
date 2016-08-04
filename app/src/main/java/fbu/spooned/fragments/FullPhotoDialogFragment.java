package fbu.spooned.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import fbu.spooned.R;

/**
 * Created by jennytlee on 7/21/16.
 */
public class FullPhotoDialogFragment extends DialogFragment {

    private ImageView ivPhotoFrag;
    private TextView tvNameFrag;
    private TextView tvPhotoTimeFrag;

    public FullPhotoDialogFragment() {
    }

    public static FullPhotoDialogFragment newInstance(String photoUrl, String name, String timeStamp) {
        FullPhotoDialogFragment frag = new FullPhotoDialogFragment();
        Bundle args = new Bundle();
        args.putString("photoUrl", photoUrl);
        args.putString("name", name);
        args.putString("timestamp", timeStamp);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_full_photo, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //fonts
        Typeface regType = Typeface.createFromAsset(getContext().getAssets(),"Champ.ttf");
        Typeface boldType = Typeface.createFromAsset(getContext().getAssets(),"ChampBold.ttf");

        ivPhotoFrag = (ImageView) view.findViewById(R.id.ivPhotoFrag);
        tvNameFrag = (TextView) view.findViewById(R.id.tvNameFrag);
        tvNameFrag.setTypeface(boldType);
        tvPhotoTimeFrag = (TextView) view.findViewById(R.id.tvPhotoTimeFrag);
        tvPhotoTimeFrag.setTypeface(regType);

        String photoUrl = getArguments().getString("photoUrl", "");
        String name = getArguments().getString("name", "");
        String timeStamp = getArguments().getString("timestamp", "");

        tvNameFrag.setText(name);
        tvPhotoTimeFrag.setText(timeStamp);

        Glide.with(getContext()).load(photoUrl).placeholder(R.drawable.placeholder).centerCrop().into(ivPhotoFrag);

    }
}
