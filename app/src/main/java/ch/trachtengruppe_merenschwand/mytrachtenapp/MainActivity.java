package ch.trachtengruppe_merenschwand.mytrachtenapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {

    private WebView mWebView;

    private boolean Debug = false;

    private boolean Benachrichtigung;
    private boolean Ton;
    private boolean Vibration;
    private boolean Licht;

    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView  = (WebView) findViewById(R.id.webView);
        mWebView.clearCache(true);

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        String ua = mWebView.getSettings().getUserAgentString() + getString(R.string.app_UserAgent);
        mWebView.getSettings().setUserAgentString(ua);

        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.toLowerCase().contains(getString(R.string.app_OpenLinkInApp)))
                    view.loadUrl(url);
                else startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
        });

        ActionBar myBar = getSupportActionBar();
        myBar.setTitle(getString(R.string.app_name));
        myBar.setSubtitle(getString((R.string.app_titel)));

    }
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Log.w("opc", "hier");

        SharedPreferences settings = getSharedPreferences(getString(R.string.app_settings), MODE_PRIVATE);

        if(settings != null)
        {
            Benachrichtigung = settings.getBoolean(getString(R.string.app_settings_benachrichtigung), true);
            Ton = settings.getBoolean(getString(R.string.app_settings_ton), true);
            Vibration = settings.getBoolean(getString(R.string.app_settings_vibration), false);
            Licht = settings.getBoolean(getString(R.string.app_settings_vibration), true);

            RssServiceHandler(true);
        }

        String OpenMe = getString(R.string.app_Startseite);

        if(getIntent().getExtras() != null && getIntent().getExtras().getString("OpenLink") != null) {
            OpenMe = getIntent().getExtras().getString("OpenLink");
            if(Debug) Log.e("OpenLink:", getIntent().getExtras().getString("OpenLink"));
        }
        if (OpenMe.toLowerCase().contains(getString(R.string.app_OpenLinkInApp)))
            mWebView.loadUrl(OpenMe);
        else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(OpenMe)));
            OpenMe = getString(R.string.app_Startseite);
            mWebView.loadUrl(OpenMe);
        }
    }

    private boolean RssServiceHandler(Boolean DoStop)
    {
        if(Benachrichtigung){
            if(DoStop) this.stopService(new Intent(this, RssService.class));
            this.startService(new Intent(this, RssService.class));
        }
        else    this.stopService(new Intent(this, RssService.class));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SharedPreferences settings = getSharedPreferences(getString(R.string.app_settings), MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();


        switch (item.getItemId()) {
            case R.id.benachrichtigung:
                item.setChecked(!item.isChecked());
                Benachrichtigung = item.isChecked();
                editor.putBoolean(getString(R.string.app_settings_benachrichtigung), Benachrichtigung);
                editor.commit();
                return RssServiceHandler(true);

            case R.id.ton:
                item.setChecked(!item.isChecked());
                Ton = item.isChecked();
                editor.putBoolean(getString(R.string.app_settings_ton), Ton);
                editor.commit();
                return RssServiceHandler(true);

            case R.id.vibration:
                item.setChecked(!item.isChecked());
                Vibration = item.isChecked();
                editor.putBoolean(getString(R.string.app_settings_vibration), Vibration);
                editor.commit();
                return RssServiceHandler(true);

            case R.id.licht:
                item.setChecked(!item.isChecked());
                Licht = item.isChecked();
                editor.putBoolean(getString(R.string.app_settings_licht), Licht);
                editor.commit();
                return RssServiceHandler(true);

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.benachrichtigung).setChecked(Benachrichtigung);
        menu.findItem(R.id.ton).setChecked(Ton);
        menu.findItem(R.id.vibration).setChecked(Vibration);
        menu.findItem(R.id.licht).setChecked(Licht);

        menu.add("Guguus");

        return true;
    }

    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
    }
}