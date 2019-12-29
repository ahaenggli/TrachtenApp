package ch.trachtengruppe_merenschwand.mytrachtenapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Objects;


/**
 * Created by ahaen on 31.03.2015.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

    if(Objects.requireNonNull(intent.getAction()).equals(Intent.ACTION_BOOT_COMPLETED)) {


            boolean Benachrichtigung;

            SharedPreferences settings;
            settings = context.getSharedPreferences(context.getString(R.string.app_settings), Context.MODE_PRIVATE);

            if(settings != null)
            {

                Benachrichtigung = settings.getBoolean(context.getString(R.string.app_settings_benachrichtigung), true);

                if(Benachrichtigung){
                    context.stopService(new Intent(context, RssService.class));
                    context.startService(new Intent(context, RssService.class));
                }
                else    context.stopService(new Intent(context, RssService.class));

            }

    }




    }
}