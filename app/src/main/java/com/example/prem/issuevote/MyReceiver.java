package com.example.prem.issuevote;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Toast.makeText(context, "Alarm Receiver invoked", Toast.LENGTH_LONG).show();

        Intent serviceIntent = new Intent(context, IssueVoteService.class);
        context.startService(serviceIntent);
        scheduleNextAlarm(context);
    }

    private void scheduleNextAlarm(Context context) {
        Intent intent = new Intent(context, MyReceiver.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        int alarmType = AlarmManager.ELAPSED_REALTIME;
//        final int FIFTEEN_SEC_MILLIS = 15000;
//        final int ONE_MINUTE_MILLIS = 60 * 1000;
        final int THIRTY_MIN_MILLIS = 30 * 60 * 1000;


        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(alarmType,  SystemClock.elapsedRealtime() + THIRTY_MIN_MILLIS, pendingIntent);
        }
        else {
            alarmManager.setExact(alarmType, SystemClock.elapsedRealtime() + THIRTY_MIN_MILLIS, pendingIntent);
        }
        // END_INCLUDE (configure_alarm_manager);
        Log.i("RepeatingAlarmFragment", "Alarm set.");
    }
}
