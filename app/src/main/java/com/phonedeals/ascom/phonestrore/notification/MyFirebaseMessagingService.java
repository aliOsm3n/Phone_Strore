package com.phonedeals.ascom.phonestrore.notification;



import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;


import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.data.model.ReceiveMessage;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());


        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            JSONObject object=null;
            try {
                object=new JSONObject( remoteMessage.getData());
                Log.e("sssssssss",remoteMessage.getData().toString());
            if ((object.getString("notification_type")).equals("4")){
                sendNotification(remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle(),object.getString("chat_id"));
            }else {
                sendOrderOfferNotification(remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle());
            }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Context... params) {
            final Context context = params[0].getApplicationContext();
            return isAppOnForeground(context);
        }

        private boolean isAppOnForeground(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }
            final String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
            return false;
        }
    }


    private void sendNotification(String messageBody,String title,String chat_id) {
        boolean foregroud = false;
        try {
            foregroud = new ForegroundCheckTask().execute(this).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

       if (foregroud){
           EventBus.getDefault().post(new ReceiveMessage(messageBody));
       }else {
           EventBus.getDefault().post(new ReceiveMessage(messageBody));
           Intent intent = new Intent("com.phonedeals.ascom.phonestrore.Notification.Chat");
           intent.putExtra("chat_id",chat_id);
           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                   PendingIntent.FLAG_ONE_SHOT);
//           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);



           Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
           Notification.Builder notificationBuilder = new Notification.Builder(this)
                   .setSmallIcon(R.drawable.app_notification)
                   .setContentTitle(title)
                   .setContentText(messageBody)
                   .setAutoCancel(true)
                   .setSound(defaultSoundUri)
                   .setContentIntent(pendingIntent);

           NotificationManager notificationManager =
                   (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

           notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
       }
    }

    private void sendOrderOfferNotification(String messageBody,String title) {



            Intent intent = new Intent("com.phonedeals.ascom.phonestrore.Notification.Target");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 10 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Notification.Builder notificationBuilder = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.app_notification)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(10 /* ID of notification */, notificationBuilder.build());

    }

}