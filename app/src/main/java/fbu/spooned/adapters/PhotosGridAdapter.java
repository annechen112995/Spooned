package fbu.spooned.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import fbu.spooned.R;
import fbu.spooned.models.Photo;

/**
 * Created by jennytlee on 7/20/16.
 */
public class PhotosGridAdapter extends ArrayAdapter<Photo> {

    ImageView ivPhoto;
    public final int DISPLAY_WIDTH = getContext().getResources().getDisplayMetrics().widthPixels;
    public final int DISPLAY_HEIGHT = getContext().getResources().getDisplayMetrics().heightPixels;

    public PhotosGridAdapter(Context context, ArrayList<Photo> photos) {
        super(context, android.R.layout.simple_list_item_1, photos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Photo photo = getItem(position);
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);

        ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);

        Glide.with(getContext()).load(photo.getPhotoUrl()).placeholder(R.drawable.placeholder).override(DISPLAY_WIDTH/2, DISPLAY_HEIGHT/3).centerCrop().into(ivPhoto);

        return convertView;
    }
}

