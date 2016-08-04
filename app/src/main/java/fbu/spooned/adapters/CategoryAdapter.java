package fbu.spooned.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import fbu.spooned.R;

/**
 * Created by eshen on 7/27/16.
 */
public class CategoryAdapter extends ArrayAdapter<String> {
    private TextView tvCategory;
    private ImageButton ibDelete;
    private ArrayList<String> categoryNames;

    public CategoryAdapter(Context context, ArrayList<String> categoryNames) {
        super(context, android.R.layout.simple_list_item_1, categoryNames);
        this.categoryNames = categoryNames;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final String category = getItem(position);
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_category, parent, false);

        tvCategory = (TextView) convertView.findViewById(R.id.tvCategory);
        ibDelete = (ImageButton) convertView.findViewById(R.id.ibDelete);

        tvCategory.setText(category);

        Typeface regType = Typeface.createFromAsset(convertView.getContext().getAssets(),"Champ.ttf");
        tvCategory.setTypeface(regType);

        ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryNames.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
