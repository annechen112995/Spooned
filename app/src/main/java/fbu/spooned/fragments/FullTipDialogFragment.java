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
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import fbu.spooned.R;
import fbu.spooned.clients.FsqClient;
import fbu.spooned.models.Tip;

/**
 * Created by jennytlee on 7/20/16.
 */
public class FullTipDialogFragment extends DialogFragment {

    FsqClient fsqClient;
    String tipId;

    private ImageView ivProfileFrag;
    private TextView tvUsernameFrag;
    private TextView tvExcerptFrag;
    private TextView tvTimestampFrag;
    private TextView tvLikes;
    private TextView tvDislikes;

    public FullTipDialogFragment() {
    }

    public static FullTipDialogFragment newInstance(String tipId) {
        FullTipDialogFragment frag = new FullTipDialogFragment();
        Bundle args = new Bundle();
        args.putString("tipId", tipId);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fsqClient = new FsqClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_full_tip, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //fonts
        Typeface regType = Typeface.createFromAsset(getContext().getAssets(),"Champ.ttf");
        Typeface boldType = Typeface.createFromAsset(getContext().getAssets(),"ChampBold.ttf");

        ivProfileFrag = (ImageView) view.findViewById(R.id.ivProfileFrag);
        tvUsernameFrag = (TextView) view.findViewById(R.id.tvUsernameFrag);
        tvUsernameFrag.setTypeface(boldType);
        tvExcerptFrag = (TextView) view.findViewById(R.id.tvExcerptFrag);
        tvExcerptFrag.setTypeface(regType);
        tvTimestampFrag = (TextView) view.findViewById(R.id.tvTimestampFrag);
        tvTimestampFrag.setTypeface(regType);
        tvLikes = (TextView) view.findViewById(R.id.tvLikes);
        tvLikes.setTypeface(regType);
        tvTimestampFrag.setTypeface(regType);
        tvDislikes = (TextView) view.findViewById(R.id.tvDislike);
        tvDislikes.setTypeface(regType);

        tipId = getArguments().getString("tipId", "");
        fsqClient.getTip(tipId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Tip tip = Tip.fromJson(response.getJSONObject("response").getJSONObject("tip"));

                    tvUsernameFrag.setText(tip.getFirstName() + " " + tip.getLastInitial());
                    tvExcerptFrag.setText(tip.getText());
                    tvTimestampFrag.setText(tip.getTimestamp());
                    tvLikes.setText(String.valueOf(tip.getAgreeCount()));
                    tvDislikes.setText(String.valueOf(tip.getDisagreeCount()));

                    Glide.with(getContext()).load(tip.getUserPhotoUrl()).fitCenter().into(ivProfileFrag);

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
