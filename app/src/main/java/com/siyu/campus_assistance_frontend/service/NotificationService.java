package com.siyu.campus_assistance_frontend.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.activity.IndexActivity;
import com.siyu.campus_assistance_frontend.enums.NotificationType;
import com.siyu.campus_assistance_frontend.utils.HttpUtils;

public class NotificationService extends Service {

    private static final String CHANNEL_ID = "notificationChannel";
    private NotificationManager notificationManager;

    private HttpUtils httpUtils;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        httpUtils = HttpUtils.getInstance(this);
        httpUtils.disconnect();
        httpUtils.connect(message -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "通知", NotificationManager.IMPORTANCE_LOW);
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(notificationChannel);

                Intent to = new Intent(this, IndexActivity.class);
                to.putExtra("nav", "notification");
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, to, PendingIntent.FLAG_IMMUTABLE);


                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
                builder.setContentTitle("通知");
                if(message.equals(NotificationType.SYSTEM.name())) {
                    builder.setContentText("您收到一条系统消息");
                } else if(message.equals(NotificationType.ADMIN.name())) {
                    builder.setContentText("您收到一条管理员消息");
                } else if(message.equals(NotificationType.COUNSELOR.name())) {
                    builder.setContentText("您收到一条辅导员消息");
                }
                builder.setSmallIcon(R.drawable.logo);
                builder.setAutoCancel(false);
                builder.setContentIntent(pendingIntent);
                builder.setAutoCancel(true);
                builder.setDefaults(Notification.DEFAULT_ALL);
                notificationManager.notify(1, builder.build());
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
}
