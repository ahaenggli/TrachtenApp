package ch.trachtengruppe_merenschwand.mytrachtenapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


/**
 * Created by ahaen on 31.03.2015.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        boolean Benachrichtigung = false;
        boolean Ton = false;
        boolean Vibration = false;
        boolean Licht = false;


        SharedPreferences settings;
        settings = context.getSharedPreferences(context.getString(R.string.app_settings), context.MODE_PRIVATE);

        if(settings != null)
        {

            Benachrichtigung = settings.getBoolean(context.getString(R.string.app_settings_benachrichtigung), true);
            Ton = settings.getBoolean(context.getString(R.string.app_settings_ton), true);
            Vibration = settings.getBoolean(context.getString(R.string.app_settings_vibration), false);
            Licht = settings.getBoolean(context.getString(R.string.app_settings_licht), true);

            if(Benachrichtigung){
                context.stopService(new Intent(context, RssService.class));
                context.startService(new Intent(context, RssService.class)
                                .putExtra("Ton", Ton)
                                .putExtra("Vibration", Vibration)
                                .putExtra("Licht", Licht)
                );
            }
            else    context.stopService(new Intent(context, RssService.class));

        }

    }
}