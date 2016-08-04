package fbu.spooned.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import fbu.spooned.R;

/**
 * Created by jennytlee on 8/2/16.
 */
public class DeleteAllDialogFragment extends DialogFragment {

    TextView tvDeleteTitle;
    TextView tvDeleteDesc;
    Button btnDeleteOkay;
    Button btnDeleteCancel;

    public interface DeleteAllDialogFragmentListener {
        void onClickButton(boolean decision);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delete_all, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // fonts
        Typeface regType = Typeface.createFromAsset(getContext().getAssets(),"Champ.ttf");
        Typeface boldType = Typeface.createFromAsset(getContext().getAssets(),"ChampBold.ttf");

        tvDeleteTitle = (TextView) view.findViewById(R.id.tvDeleteTitle);
        tvDeleteTitle.setTypeface(boldType);
        tvDeleteDesc = (TextView) view.findViewById(R.id.tvDeleteDesc);
        tvDeleteDesc.setTypeface(regType);

        btnDeleteOkay = (Button) view.findViewById(R.id.btnDeleteOkay);
        btnDeleteOkay.setTypeface(regType);
        btnDeleteCancel = (Button) view.findViewById(R.id.btnDeleteCancel);
        btnDeleteCancel.setTypeface(boldType);

        btnDeleteOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteAllDialogFragmentListener listener = (DeleteAllDialogFragmentListener) getActivity();
                listener.onClickButton(true);
                dismiss();
            }
        });

        btnDeleteCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteAllDialogFragmentListener listener = (DeleteAllDialogFragmentListener) getActivity();
                listener.onClickButton(false);
                dismiss();
            }
        });

    }
}
