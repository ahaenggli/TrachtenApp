package ch.trachtengruppe_merenschwand.mytrachtenapp;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by ahaen on 31.03.2015.
 */
public class RssService extends Service {
    private String RSS_LINK;
    private Long LastDate;

    boolean Debug = false;

    boolean Benachrichtigung = false;
    boolean Ton = false;
    boolean Vibration = false;
    boolean Licht = false;

    int delay = 15*1000; // delay for 15 sec.
    int period = 3600*1000; // repeat every 3600 sec.

    private Handler mHandler;

/*
    public static int randInt(int min, int max) {
    Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
*/
    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            if(Debug) Log.w("RSS-Read", "Start");
                try {
                //RSS Daten lesen
                List<RssItem> rssItems;
                try {
                    RssParser parser = new RssParser();
                    //InputStream sss = getInputStream(RSS_LINK);
                    rssItems = parser.parse(getInputStream(RSS_LINK));
                } catch(Exception e) {
                    if(Debug) Log.w("Error:", "Feed nicht lesbar (kein Inet?)");
                    if(Debug) e.printStackTrace();
                    mHandler.postDelayed(new Runnable(){public void run(){new Thread(mRunnable).start();}}, 60000);
                    return;
                }

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

                String dateStr = null;

                if(rssItems.get(1).getLastBuildDate()!=null) dateStr = rssItems.get(1).getLastBuildDate();
                if(rssItems.get(1).getPubDate()!=null) dateStr = rssItems.get(1).getPubDate();


                Date fromDate = null;

                if(dateStr!=null) {
                    SimpleDateFormat fromFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z", Locale.US);
                    try {
                        fromDate = fromFormat.parse(dateStr); //pubDate[i] is your date (node value)
                    } catch (Exception e) {
                        if(Debug) Log.w("Error:", "Datumskonvertierung nicht möglich");
                        if(Debug) e.printStackTrace();
                    }
                }

                Long LastPub;
                if(fromDate!=null) LastPub = fromDate.getTime();
                else LastPub = LastDate;
/*
        Log.w("fromDate", fromDate.toString());
        Log.w("LastPub", LastPub.toString());
        Log.w("LastDate", LastDate.toString());
*/
                //Debug: führt dazu, dass bei jedem Timer onRun eine Notifcation kommt
                //if(Debug) LastDate = LastPub  -1;

                if (LastDate < LastPub) {

                    Intent intenty = new Intent(getBaseContext(), MainActivity.class);
                    intenty.putExtra("OpenLink", rssItems.get(1).getLink());
                    PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),  new Random().nextInt(), intenty, 0);

                    Log.i("OpenLink:", rssItems.get(1).getLink());

                    //Notification machen
                    NotificationCompat.Builder noti = new NotificationCompat.Builder(
                            getBaseContext())
                            .setContentTitle(rssItems.get(1).getTitle())
                            .setContentText(android.text.Html.fromHtml(rssItems.get(1).getDescription()))
                                    //.setContentInfo("Info")
                                    //.setNumber(99)

                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setSmallIcon(R.mipmap.tg_logo)
                            .setContentIntent(pendingIntent)
                            .setWhen(LastPub)
                            .setShowWhen(true)
                            .setColor(Color.WHITE)
                            .setAutoCancel(true);

                    if (Ton) noti.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);//noti.setDefaults(NotificationCompat.DEFAULT_SOUND);
                    if (Vibration) noti.setVibrate(new long[] { 500, 500});//noti.setDefaults(NotificationCompat.DEFAULT_VIBRATE);
                    if (Licht) noti.setLights(Color.WHITE, 2500, 2500);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    //Zeige Notification endlich an
                    notificationManager.notify( new Random().nextInt(), noti.build());
                    LastDate = LastPub;
                    if(Debug) Log.i("Notify", "gemacht");

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putLong(getString(R.string.app_settings_lastdate), LastDate);
                    editor.commit();
                }
                mHandler.postDelayed(new Runnable(){public void run(){new Thread(mRunnable).start();}}, period+delay);
            } catch(Exception e) {
                    if(Debug) Log.w("Error:", "Position unerwartet");
                    if(Debug) e.printStackTrace();
                    mHandler.postDelayed(new Runnable(){public void run(){new Thread(mRunnable).start();}}, period+delay);
                return;
            }
            if(Debug) Log.w("RSS-Read", "Fertig");
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
        mHandler.postDelayed(new Runnable(){public void run(){new Thread(mRunnable).start();}}, period+delay);

        if(Debug) Log.i("OnCreate", "finito");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        SharedPreferences settings = getSharedPreferences(getString(R.string.app_settings), MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(getString(R.string.app_settings_lastdate), LastDate);
        editor.commit();
        mHandler.removeCallbacks(mRunnable);
        stopSelf();
    }

    @Override
    public boolean stopService(Intent name) {
        SharedPreferences settings = getSharedPreferences(getString(R.string.app_settings), MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(getString(R.string.app_settings_lastdate), LastDate);
        editor.commit();
        mHandler.removeCallbacks(mRunnable);
        stopSelf();
        return super.stopService(name);
    }

    public InputStream getInputStream(String link) {
            try {
                URL url = new URL(link);
                return url.openConnection().getInputStream();
            } catch (IOException e) {
                return null;
            }
    }

}