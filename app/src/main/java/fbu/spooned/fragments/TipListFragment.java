package fbu.spooned.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import fbu.spooned.R;
import fbu.spooned.Utility;
import fbu.spooned.adapters.TipArrayAdapter;
import fbu.spooned.clients.FsqClient;
import fbu.spooned.models.Tip;

/**
 * Created by jennytlee on 7/18/16.
 */
public class TipListFragment extends Fragment {

    private ArrayList<Tip> tips;
    private TipArrayAdapter aTips;
    ListView lvReviews;
    TextView tvReviewLabel;
    String tipId;
    FsqClient fsqClient;


    public static TipListFragment newInstance(String id) {
        TipListFragment tipListFragment = new TipListFragment();
        Bundle args = new Bundle();
        args.putString("tipId", id);
        tipListFragment.setArguments(args);
        return tipListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tipId = getArguments().getString("tipId", "");

        tips = new ArrayList<>();
        aTips = new TipArrayAdapter(getActivity(), tips);
        fsqClient = new FsqClient();


        grabTips();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);
        lvReviews = (ListView) view.findViewById(R.id.lvReviews);

        // fonts
        Typeface regType = Typeface.createFromAsset(getContext().getAssets(),"Champ.ttf");
        Typeface boldType = Typeface.createFromAsset(getContext().getAssets(),"ChampBold.ttf");

        tvReviewLabel = (TextView) view.findViewById(R.id.tvReviewLabel);
        tvReviewLabel.setTypeface(boldType);

        lvReviews.setAdapter(aTips);
        return view;
    }

    private void grabTips() {
        fsqClient.getTipsById(tipId, 3, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    tips = Tip.fromJson(response.getJSONObject("response").getJSONObject("tips").getJSONArray("items"));
                    aTips.clear();

                    if (tips.size() < 3) {
                        for (int i = 0; i < tips.size(); i++) {
                            aTips.add(tips.get(i));
                            aTips.notifyDataSetChanged();
                        }
                    } else {

                        for (int i = 0; i < 3; i++) {
                            aTips.add(tips.get(i));
                            aTips.notifyDataSetChanged();
                        }
                    }

                    Utility.setListViewHeightBasedOnChildren(lvReviews);

                    lvReviews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String tipId = tips.get(i).getTipId();
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            FullTipDialogFragment fullTipDialogFragment = FullTipDialogFragment.newInstance(tipId);
                            fullTipDialogFragment.show(fm, "fragment_full_tip");
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
