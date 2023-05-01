package com.example.myapplication.firebase;

import static android.content.Context.NOTIFICATION_SERVICE;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.MainActivity;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.myapplication.R;

public class MyNotificationManager {

    private final Context mCtx;
    private static MyNotificationManager mInstance;
    NotificationManager mNotifyMgr;
    NotificationCompat.Builder mBuilder;

    private MyNotificationManager(Context context) {
        mCtx = context;
    }

    String temp = "";

    public static synchronized MyNotificationManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyNotificationManager(context);
        }
        return mInstance;
    }

    public int createID() {
        Date now = new Date();
        return Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(now));
    }
    public void displayNotification(String title, String body, Bitmap image, String blogId, String deeplink) {
        Intent intent;
        intent = new Intent(mCtx, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("Notification", true);
        intent.putExtra("postId", blogId);
        intent.putExtra("mNotideeplink", deeplink);
        Log.e("TAGn", " displayNotification blog id "+ blogId);

        int id = createID();
        int uniqueId = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, id, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE );
//        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            temp = Base64.encodeToString(b, Base64.DEFAULT);

//            Log.e("TAGn", temp + "jfgkgjk");
            Log.e("TAGn", image + "nulll"   );
        } catch (Exception e) {
        }

        mNotifyMgr = (NotificationManager) mCtx.getSystemService(NOTIFICATION_SERVICE);
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel= new NotificationChannel( String.valueOf(R.string.default_notification_channel_id), Constant.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            mNotifyMgr.createNotificationChannel(channel);
        }*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel( String.valueOf(R.string.default_notification_channel_id),
                    Constant.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            mNotifyMgr.createNotificationChannel(channel);
        }

        if (image == null) {

            mBuilder =
                    new NotificationCompat.Builder(mCtx, String.valueOf(R.string.default_notification_channel_id))
                            .setSmallIcon(R.drawable.ic_refresh)
                            .setColor(ContextCompat.getColor(mCtx, R.color.black))
                            .setContentTitle(title)
                            .setStyle(new NotificationCompat.BigPictureStyle()
                                    .bigPicture(image))
                            .setContentText(body)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                            .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(body))
                            .setStyle(new NotificationCompat.BigTextStyle().setSummaryText(body))
                            .setContentIntent(pendingIntent);


            Log.e("TAGn",     "image is null in if" + image);
        } else {

            mBuilder =
                    new NotificationCompat.Builder(mCtx, String.valueOf(R.string.default_notification_channel_id))
                            .setSmallIcon(R.drawable.ic_refresh)
                            .setLargeIcon(image)
                            .setContentTitle(title)
                            .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image))
//                            .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                            .setContentText(body)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent);


            Log.e("TAGn",  "image is not null in else ");
        }

        if (mNotifyMgr != null) {
            mNotifyMgr.notify(id, mBuilder.build());
        }
    }

}