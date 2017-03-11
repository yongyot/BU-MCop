package th.ac.bu.mcop.widgets;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import th.ac.bu.mcop.R;
import th.ac.bu.mcop.activities.HomeActivity;

/**
 * Created by jeeraphan on 11/14/16.
 */

public class NotificationView {

    private static int counter = 0;

    public static void show(Context context, String message){

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setContentTitle(context.getString(R.string.app_name));

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setContentText(message).build();
        notificationBuilder.setPriority(Notification.PRIORITY_LOW);
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        Intent intent = new Intent(context, HomeActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        // notificationID allows you to update the notification later on.
        notificationManager.notify(counter, notificationBuilder.build());

        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(650);

        counter++;
    }
}
