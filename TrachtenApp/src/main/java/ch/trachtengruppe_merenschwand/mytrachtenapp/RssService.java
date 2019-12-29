package ch.trachtengruppe_merenschwand.mytrachtenapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import androidx.core.app.NotificationCompat;

/**
 * Created by ahaen on 31.03.2015.
 */
public class RssService extends Service {
    private String RSS_LINK;
    private Long LastDate;

    private boolean Benachrichtigung = false;
    private boolean Ton = false;
    private boolean Vibration = false;
    private boolean Licht = false;

    private Handler mHandler;

    private String CHANNEL_ID = "TrachtenChannel";
/*
    public static int randInt(int min, int max) {
    Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
*/

    private final Runnable mRunnable = new Runnable() {

        @Override
        public void run() {

                    //RSS Daten lesen
                    List<RssItem> rssItems;
                    try {
                        Log.d("RssService", "Start");
                        RssParser parser = new RssParser();
                        //InputStream sss = getInputStream(RSS_LINK);
                        rssItems = parser.parse(getInputStream(RSS_LINK));


                        SharedPreferences settings;
                        settings =  getBaseContext().getSharedPreferences( getBaseContext().getString(R.string.app_settings),  getBaseContext().MODE_PRIVATE);

                        LastDate = System.currentTimeMillis();
                        if(settings != null)
                        {
                            Benachrichtigung = settings.getBoolean(getBaseContext().getString(R.string.app_settings_benachrichtigung), true);
                            Ton = settings.getBoolean(getBaseContext().getString(R.string.app_settings_ton), true);
                            Vibration = settings.getBoolean(getBaseContext().getString(R.string.app_settings_vibration), false);
                            Licht = settings.getBoolean(getBaseContext().getString(R.string.app_settings_licht), true);
                            LastDate = settings.getLong(getBaseContext().getString(R.string.app_settings_lastdate), LastDate);
                        }

                        if(!Benachrichtigung) getBaseContext().stopService(new Intent( getBaseContext(), RssService.class));


                        Log.i("RssService", "Benachrichtigung: " + Benachrichtigung);
                        String dateStr = null;

                        if(rssItems.get(1).getLastBuildDate()!=null) dateStr = rssItems.get(1).getLastBuildDate();
                        if(rssItems.get(1).getPubDate()!=null) dateStr = rssItems.get(1).getPubDate();


                        Date fromDate = null;

                        if(dateStr!=null) {
                            SimpleDateFormat fromFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z", Locale.US);
                            try {
                                fromDate = fromFormat.parse(dateStr); //pubDate[i] is your date (node value)
                            } catch (Exception e) {
                                Log.e("RssService", "Error: Datumskonvertierung nicht möglich");
                                e.printStackTrace();
                            }
                        }

                        Long LastPub;
                        if(fromDate!=null) LastPub = fromDate.getTime();
                        else LastPub = LastDate;

                        //Debug: führt dazu, dass bei jedem Timer onRun eine Notifcation kommt
                        //if(Debug)
                        LastDate = LastPub  -1;

                        if (LastDate < LastPub) {

                            Intent intenty = new Intent(getBaseContext(), MainActivity.class);
                            intenty.putExtra("OpenLink", rssItems.get(1).getLink());
                            PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),  new Random().nextInt(), intenty, 0);

                            Log.i("RssService", "OpenLink: " + rssItems.get(1).getLink());

                            //Notification machen
                            NotificationManager mNotificationManager;
                            Context mContext = getBaseContext();
                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(mContext.getApplicationContext(), CHANNEL_ID);

                            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
                            bigText.bigText(android.text.Html.fromHtml(rssItems.get(1).getDescription()));
                            bigText.setBigContentTitle(rssItems.get(1).getTitle());
                            //bigText.setSummaryText("Text im Detail");

                            mBuilder.setSmallIcon(R.drawable.tg_logo_notification);
                            mBuilder.setContentTitle(rssItems.get(1).getTitle());
                            mBuilder.setContentText(android.text.Html.fromHtml(rssItems.get(1).getDescription()));
                            mBuilder.setContentIntent(pendingIntent);


                            mBuilder.setPriority(Notification.PRIORITY_DEFAULT);

                            mBuilder.setStyle(bigText);


                            mBuilder.setWhen(LastPub);
                            mBuilder.setShowWhen(true);
                            mBuilder.setColor(Color.WHITE);
                            mBuilder.setAutoCancel(true);

                            if (Ton) mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);//noti.setDefaults(NotificationCompat.DEFAULT_SOUND);
                            else mBuilder.setSound(null);

                            if (Vibration) mBuilder.setVibrate(new long[] { 500, 500});//noti.setDefaults(NotificationCompat.DEFAULT_VIBRATE);
                            if (Licht) mBuilder.setLights(Color.WHITE, 2500, 2500);


                            mNotificationManager =
                                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                            {
                                // Anzahl in App-Badge anzeigen
                                // mBuilder.setNumber(1);

                                // Channel aufbauen
                                NotificationChannel channel = new NotificationChannel(
                                        CHANNEL_ID,
                                        CHANNEL_ID,
                                        NotificationManager.IMPORTANCE_LOW);
                                mNotificationManager.createNotificationChannel(channel);
                                mBuilder.setChannelId(CHANNEL_ID);
                            }

                            mNotificationManager.notify(0, mBuilder.build());
                            Log.i("RssService", "Notify gemacht");


                            LastDate = LastPub;
                            SharedPreferences.Editor editor = Objects.requireNonNull(settings).edit();
                            editor.putLong(getString(R.string.app_settings_lastdate), LastDate);
                            editor.apply();
                        }

                    } catch(Exception e) {
                        Log.e("RssService:",  e.getStackTrace().toString());

                    } finally {
                        mHandler.postDelayed(() -> new Thread(mRunnable).start(), 60000);
                    }


            Log.d("RssService", "Ende");
        }
    };

  @Override
  public IBinder onBind(Intent intent) {
    return null;
}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RSS_LINK = getString(R.string.app_rss);

        SharedPreferences settings;
        settings =  getBaseContext().getSharedPreferences( getBaseContext().getString(R.string.app_settings),  getBaseContext().MODE_PRIVATE);

        LastDate = System.currentTimeMillis();
           if(settings != null)
           {
               Benachrichtigung = settings.getBoolean(getBaseContext().getString(R.string.app_settings_benachrichtigung), true);
               Ton = settings.getBoolean(getBaseContext().getString(R.string.app_settings_ton), true);
               Vibration = settings.getBoolean(getBaseContext().getString(R.string.app_settings_vibration), false);
               Licht = settings.getBoolean(getBaseContext().getString(R.string.app_settings_licht), true);
               LastDate = settings.getLong(getBaseContext().getString(R.string.app_settings_lastdate), LastDate);
           }

        if(!Benachrichtigung) getBaseContext().stopService(new Intent( getBaseContext(), RssService.class));

        mHandler = new Handler();
        // delay for 15 sec.
        int delay = 15 * 1000;
        // repeat every 3600 sec.
        int period = 15 * 1000;
        mHandler.postDelayed(() -> new Thread(mRunnable).start(), period + delay);


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        SharedPreferences settings = getSharedPreferences(getString(R.string.app_settings), MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(getString(R.string.app_settings_lastdate), LastDate);
        editor.apply();
        mHandler.removeCallbacks(mRunnable);
        stopSelf();
    }

    @Override
    public boolean stopService(Intent name) {
        SharedPreferences settings = getSharedPreferences(getString(R.string.app_settings), MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(getString(R.string.app_settings_lastdate), LastDate);
        editor.apply();
        mHandler.removeCallbacks(mRunnable);
        stopSelf();
        return super.stopService(name);
    }

    private InputStream getInputStream(String link) {
            try {
                URL url = new URL(link);
                return url.openConnection().getInputStream();
            } catch (IOException e) {
                return null;
            }
    }

}