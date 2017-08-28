package com.example.gildo.quadcoptercontroller.services.communication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.gildo.quadcoptercontroller.R;
import com.example.gildo.quadcoptercontroller.models.entities.IOIOControllerLooper;

import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOService;

public class IOIOControllerService extends IOIOService {
    public static final String ACTION_STOP_KEY = "stop";

    private NotificationManager notificationManager;

    private IOIOControllerLooper ioioControllerLooper;

    @Override
    protected IOIOLooper createIOIOLooper() {
//        this.ioioControllerLooper = new IOIOControllerLooper();
//        this.ioioControllerLooper.setContext(this.getApplicationContext());
        this.ioioControllerLooper = new IOIOControllerLooper(this.getApplicationContext());

        return this.ioioControllerLooper;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result;

        result = super.onStartCommand(intent, flags, startId);
        this.notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (intent != null && intent.getAction() != null && intent.getAction().equals(ACTION_STOP_KEY)) {
            //User clicks on notification
            this.notificationManager.cancel(0);
            //Stop the service
            stopSelf();
        } else {
            createNotification();
        }

        return result;
    }

    public void createNotification() {
        Notification notification;
        NotificationCompat.Builder notificationBuilder;
        PendingIntent resultPendingIntent;
        Intent stopServiceIntent;

        stopServiceIntent = new Intent(ACTION_STOP_KEY, null, this, IOIOControllerService.class);
//        resultPendingIntent = PendingIntent.getService(this, 0, stopServiceIntent, 0);
        resultPendingIntent = PendingIntent.getService(this.getApplicationContext(), 0, stopServiceIntent, 0);

        notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.ic_notification);
        notificationBuilder.setContentTitle("Serviço da IOIO em execução");
        notificationBuilder.setContentText("Toque para encerrar o serviço!");
        notificationBuilder.setContentIntent(resultPendingIntent);

        notification = notificationBuilder.build();
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;

        this.notificationManager.notify(0, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
