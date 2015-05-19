package com.android.markschmidt.nonzeroday;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String EXTRA_TITLE = "title";
    public static final String ACTION_NOTIFY_NIGHT = "NOTIFY_NIGHT";
    public static final String ACTION_NOTIFY_MORNING = "NOTIFY_MORNING";
    public static final String TAG = "AlarmReceiver";

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "AlarmReceiver received intent");
        if(action == ACTION_NOTIFY_MORNING)
        {
            handleActionMorning(context);
        }
        else if(action == ACTION_NOTIFY_NIGHT);
        {
            String title = intent.getStringExtra(EXTRA_TITLE);
            handleActionNight(title, context);
        }


    }

    private void handleActionNight(String title, Context c) {
        Log.i(TAG, "HandleActionNight");

        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) c
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(c, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(c, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                c).setSmallIcon(R.drawable.notificationicon)
                .setContentTitle("Nonzero Day")
                .setContentText("Your " + title + " objective is about to expire.  Add some data to continue your streak!")
                .setSound(alarmSound)
                .setAutoCancel(true).setWhen(when)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        notificationManager.notify(0, mNotifyBuilder.build());
    }

    private void handleActionMorning(Context c) {
        Log.i(TAG, "HandleActionMorning");

        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) c
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(c, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(c, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                c).setSmallIcon(R.drawable.notificationicon)
                .setContentTitle("Nonzero Day")
                .setContentText("Make today a Nonzero Day! Add new streak data now")
                .setSound(alarmSound)
                .setAutoCancel(true).setWhen(when)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        notificationManager.notify(0, mNotifyBuilder.build());
    }
}
