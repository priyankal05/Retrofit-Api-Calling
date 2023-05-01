package com.example.myapplication.firebase;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.example.myapplication.TinyDB;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.List;
import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String key,value;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        //    private static final int NOTIFICATION_ID = (int) System.currentTimeMillis() + new Random().nextInt();
        //    String temp = "";
        TinyDB mTinydb = new TinyDB(getApplicationContext());
        try {
            Log.e("TAGtoken", s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTinydb.putString("FCMTOKEN_KEY", s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (ForegroundChecker.isForeground(this)) {
            // The app is in the foreground, so you can show a custom notification.
//            Log.e("TAGn", "  onMessageReceived in if getData" +remoteMessage.getData());
//            Log.e("TAGn", "  onMessageReceived in if getNotification " +remoteMessage.getNotification());
//            Log.e("TAGn", "onMessageReceived: showCustomNotification  in if " +remoteMessage.getNotification().getImageUrl() +" \n body "+ remoteMessage.getNotification().getBody()+ "\n title "+remoteMessage.getNotification().getTitle() );
//            showCustomNotification(remoteMessage.getNotification(), remoteMessage.getData());

            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            //then here we can use the title and body to build a notification

            String imageUri = String.valueOf(remoteMessage.getNotification().getImageUrl());
            String blodid = remoteMessage.getData().get("blogId");
            String deeplink = remoteMessage.getData().get("deepLink");
            Bitmap bitmap = getBitmapfromUrl2(imageUri);

//            Log.e("TAGn", "  onMessageReceived in if getData" +remoteMessage.getData()+"  "+blodid );
            MyNotificationManager.getInstance(getApplicationContext()).displayNotification(title, body, bitmap, blodid, deeplink);

        } else {
            // The app is in the background, so the system will handle the notification automatically.
//            Log.e("TAGn", "onMessageReceived: showCustomNotification in else ");

            if (remoteMessage.getNotification() != null) {
                String title = remoteMessage.getNotification().getTitle();
                String body = remoteMessage.getNotification().getBody();
                //then here we can use the title and body to build a notification
                for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                    key = entry.getKey();
                    value = entry.getValue();
//                    Log.e("TAGn", "key, " + key + " value " + value);
                    //   String values = remoteMessage.getData().get("value");
                    //   Log.e("vakye",values);
                }
                String imageUri = String.valueOf(remoteMessage.getNotification().getImageUrl());
                String blodid = remoteMessage.getData().get("blogId");
                String deeplink = remoteMessage.getData().get("deepLink");
                Bitmap bitmap = getBitmapfromUrl2(imageUri);
                MyNotificationManager.getInstance(getApplicationContext()).displayNotification(title, body, bitmap, blodid, deeplink);
//                Log.e("TAGn", "remoteMessage  not null "  + imageUri + "\n title "+ title +" \n body "+ body);

            }
        }
    }

    public void handleIntent(Intent intent) {
        try {
            if (intent.getExtras() != null) {
                RemoteMessage.Builder builder = new RemoteMessage.Builder("MyFirebaseMessagingService");
                for (String key : intent.getExtras().keySet()) {
                    builder.addData(key, intent.getExtras().get(key).toString());
                }
                onMessageReceived(builder.build());

            } else {
                super.handleIntent(intent);
            }
        } catch (Exception e) {
            super.handleIntent(intent);
        }
    }

    private void showCustomNotification(RemoteMessage.Notification notification, Map<String, String> data) {
//        String imageUri = String.valueOf(notification.getImageUrl());
//        Bitmap bitmap = getBitmapfromUrl2(imageUri);

        new sendNotification(notification.getTitle(), notification.getBody(), notification.getImageUrl().toString(), data.get("blogId"), data.get("deepLink")).execute();

       /* Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("Notification", true);
        intent.putExtra("mNotiblogId", notification.);
        intent.putExtra("mNotideeplink", deeplink);
        int id = createID();
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, id, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE );
*/

       /* try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            temp = Base64.encodeToString(b, Base64.DEFAULT);

            Log.e("TAGn", String.valueOf(bitmap) + "nulll"   );
        } catch (Exception e) {
        }

        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel( Constant.CHANNEL_ID, Constant.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,  Constant.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notif_firebase)
                .setColor(ContextCompat.getColor(this, R.color.red))
                .setContentTitle(notification.getTitle())
                .setLargeIcon(bitmap)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap))
                .setContentText(notification.getBody())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                *//*.setContentIntent(pendingIntent)*//*;

        notificationManager.notify(NOTIFICATION_ID, builder.build());*/
    }

    public static class ForegroundChecker {
        public static boolean isForeground(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

            if (appProcesses == null) {
                return false;
            }

            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                        appProcess.processName.equals(context.getPackageName())) {
                    return true;
                }
            }
            return false;
        }
    }

    public Bitmap getBitmapfromUrl(String imageUrl ) {
        Bitmap chefBitmap = null;
        try {
            chefBitmap = Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(imageUrl)
                    .submit()
                    .get();
            return chefBitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap getBitmapfromUrl2(String imageUrl) {
        Bitmap chefBitmap = null;
        try {
            chefBitmap = Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(imageUrl)
                    .submit()
                    .get();
            return chefBitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private class sendNotification extends AsyncTask<Void, Void, Void> {
        String title, body, image, type, link;
        Bitmap bitmap;


        public sendNotification(String title, String body, String image, String type,  String link) {
            this.title = title;
            this.body = body;
            this.image = image;
            this.type = type;
            this.link = link;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            bitmap = getBitmapfromUrl(image );
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MyNotificationManager.getInstance(getApplicationContext()).displayNotification(title, body, bitmap, type,  link/*,key,value*/);
        }
    }
}
