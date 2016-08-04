package fbu.spooned.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import fbu.spooned.R;

public class WebsiteActivity extends AppCompatActivity {

    WebView wvSite;
    String webUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website);

        TextView tvNoWeb = (TextView) findViewById(R.id.tvNoWeb);

        wvSite = (WebView) findViewById(R.id.wvSite);
        webUrl = getIntent().getStringExtra("webUrl");

        if (webUrl != null) {
            tvNoWeb.setVisibility(View.INVISIBLE);
        } else {
            tvNoWeb.setVisibility(View.VISIBLE);
        }

        wvSite.loadUrl(webUrl);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
