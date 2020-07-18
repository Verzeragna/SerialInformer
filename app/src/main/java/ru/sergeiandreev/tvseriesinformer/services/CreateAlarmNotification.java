package ru.sergeiandreev.tvseriesinformer.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;
import java.util.Date;

import ru.sergeiandreev.tvseriesinformer.activities.MainActivity;
import ru.sergeiandreev.tvseriesinformer.R;
import ru.sergeiandreev.tvseriesinformer.recievers.BootReciever;

public class CreateAlarmNotification extends Service {

    public static final String CHANNEL_ID = "SerialInformer";
    private AlarmManager alarmMgr;
    private AlarmManager alarmMgrUp;
    private long nextTimeAlarm = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (startId!=1) {
            // TODO Если это повторный запуск, выполнить какие-то действия.
            setAlarmNotify();
            setAlarmUpdate();
        }
        else {
            // TODO Альтернативные действия в фоновом режиме.
            reCreateNotification();
        }
        return START_STICKY;
    }

    private void reCreateNotification() {
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setOngoing(true)
                .setContentText("Оповещение о выходе серий включено!")
                .setSmallIcon(R.drawable.main_icon_service)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1989, notification);
        setAlarmNotify();
        setAlarmUpdate();
    }

    private void setAlarmNotify(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTimeInMillis(System.currentTimeMillis());
        calendarEnd.set(Calendar.HOUR_OF_DAY, 20);
        calendarEnd.set(Calendar.MINUTE, 0);
        calendarEnd.set(Calendar.SECOND, 0);
        Date date = new Date();
        long getNow = date.getTime();
        if (getNow < calendar.getTimeInMillis()){
            alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplicationContext(), NotificationService.class);
            PendingIntent alarmIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);//43200000
            alarmMgr.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), alarmIntent);
            nextTimeAlarm = calendar.getTimeInMillis();
        }else{
            if (nextTimeAlarm < getNow) {
                if (getNow < calendarEnd.getTimeInMillis()) {
                    if (System.currentTimeMillis() + 14400000 < calendarEnd.getTimeInMillis()) {
                        calendar.setTimeInMillis(System.currentTimeMillis() + 14400000);
                        alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
                        PendingIntent alarmIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);//43200000
                        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
                        nextTimeAlarm = calendar.getTimeInMillis();
                    }else{
                        alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
                        PendingIntent alarmIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);//43200000
                        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendarEnd.getTimeInMillis(), alarmIntent);
                        nextTimeAlarm = calendarEnd.getTimeInMillis();
                    }
                }else{
                    calendarEnd.setTimeInMillis(calendarEnd.getTimeInMillis() + 43200000);
                    alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getApplicationContext(), NotificationService.class);
                    PendingIntent alarmIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);//43200000
                    alarmMgr.set(AlarmManager.RTC_WAKEUP, calendarEnd.getTimeInMillis(), alarmIntent);
                    nextTimeAlarm = calendarEnd.getTimeInMillis();
                }
            }
        }

    }

    private void setAlarmUpdate(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date date = new Date();
        long getNow = date.getTime();
        if (calendar.getTimeInMillis() > getNow) {
            alarmMgrUp = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplicationContext(), UpdateService.class);
            PendingIntent alarmIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
            alarmMgr.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), alarmIntent);
        } else {
                    calendar.setTimeInMillis(calendar.getTimeInMillis() + 86400000);//86400000
                    alarmMgrUp = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getApplicationContext(), UpdateService.class);
                    PendingIntent alarmIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
                    alarmMgrUp.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_NONE
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(new BootReciever());
    }
}
