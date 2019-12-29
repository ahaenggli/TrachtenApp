package ch.trachtengruppe_merenschwand.mytrachtenapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Objects;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;

    private boolean Benachrichtigung;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = findViewById(R.id.webView);
        mWebView.clearCache(true);

        // Enable Javascript
        mWebView.getSettings().setJavaScriptEnabled(true);
        //mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        // set User Agent
        String ua = mWebView.getSettings().getUserAgentString() + getString(R.string.app_UserAgent);
        mWebView.getSettings().setUserAgentString(ua);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.toLowerCase().contains(getString(R.string.app_OpenLinkInApp))) {
                    view.loadUrl(url);
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
                return true;
            }


        });

        ActionBar myBar = getSupportActionBar();
        Objects.requireNonNull(myBar).setTitle(getString((R.string.app_titel)));
        //myBar.setSubtitle(getString(R.string.app_name));

    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(getString(R.string.app_settings), MODE_PRIVATE);

        if (settings != null) {
            Benachrichtigung = settings.getBoolean(getString(R.string.app_settings_benachrichtigung), true);
            RssServiceHandler();
        }

        String OpenMe = getString(R.string.app_Startseite);

        // Optional: Lese Daten bei öffnen durch Push-Nachricht
        if (getIntent().getExtras() != null && getIntent().getExtras().getString("OpenLink") != null) {
            OpenMe = getIntent().getExtras().getString("OpenLink");
        }

        if (Objects.requireNonNull(OpenMe).toLowerCase().contains(getString(R.string.app_OpenLinkInApp)))
            mWebView.loadUrl(OpenMe);
        else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(OpenMe)));
            OpenMe = getString(R.string.app_Startseite);
            mWebView.loadUrl(OpenMe);
        }
    }

    private void RssServiceHandler() {
        if (Benachrichtigung) {
            this.stopService(new Intent(this, RssService.class));
            this.startService(new Intent(this, RssService.class));
        } else this.stopService(new Intent(this, RssService.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SharedPreferences settings = getSharedPreferences(getString(R.string.app_settings), MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();


        if (item.getItemId() == R.id.benachrichtigung) {
            item.setChecked(!item.isChecked());
            Benachrichtigung = item.isChecked();
            editor.putBoolean(getString(R.string.app_settings_benachrichtigung), Benachrichtigung);
            editor.apply();
            RssServiceHandler();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.benachrichtigung).setChecked(Benachrichtigung);

        //menu.add("Guguus");

        return true;
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}