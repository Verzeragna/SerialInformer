package ru.sergeiandreev.tvseriesinformer.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import ru.sergeiandreev.tvseriesinformer.database.DBHelper;
import ru.sergeiandreev.tvseriesinformer.activities.MainActivity;
import ru.sergeiandreev.tvseriesinformer.R;

import static android.telephony.AvailableNetworkInfo.PRIORITY_HIGH;

public class NotificationService extends Service {
    private NotificationManager notificationManager;
    private static final int NOTIFY_ID = 1;
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private final Pattern pattern = Pattern.compile("[^\\d]");

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                showNotification();
            }
        };
        thread.run();

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }


    private void showNotification() {
        String TittleMessage = getApplicationContext().getString(R.string.tittle_message);
        String TextMessage = getTextMessage();
        if (TextMessage.length() > 0) {
            notificationManager = (NotificationManager) getApplicationContext().getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
            Intent intentN = new Intent(getApplicationContext(), MainActivity.class);
            intentN.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intentN, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                            .setAutoCancel(false)
                            .setSmallIcon(R.drawable.main_icon_notif)
                            .setWhen(System.currentTimeMillis())
                            .setContentIntent(pendingIntent)
                            .setContentTitle(TittleMessage)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(TextMessage))
                            .setContentText(TextMessage)
                            .setAutoCancel(true)
                            .setWhen(System.currentTimeMillis())
                            //.setContentText("Message")
                            .setPriority(PRIORITY_HIGH);
            createChannelIfNeeded(notificationManager);
            notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
        }
        startService(new Intent(this,CreateAlarmNotification.class));
        stopSelf();
        onDestroy();
    }

    public void createChannelIfNeeded(NotificationManager manager) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);

            manager.createNotificationChannel(notificationChannel);
        }
    }

    private String getTextMessage() {
        String returnText = "";
        DBHelper mDbHelper = new DBHelper(getApplicationContext());
        Date dateNow = new Date();
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd.MMMM.yyyy");
        String today = simpleDate.format(dateNow);
        //String today="15.Октября.2019";
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        Cursor cursor = database.query(mDbHelper.TABLE_EPISODES, null, null, null, null, null, null);// читаем из БД все данные. Класс cursor можно рассматривать, как набор строк с данными
        if (cursor.moveToFirst()) { //проверка на выполнение запроса
            //получаем порядковые номера столбцов по их именам
            int serialIndex = cursor.getColumnIndex(mDbHelper.KEY_SERIAL);
            int episodeIndex = cursor.getColumnIndex(mDbHelper.KEY_EPISODE_INFO);
            do {
                String serial = cursor.getString(serialIndex);
                String[] episodeInfo = cursor.getString(episodeIndex).split(",");
                String dateString = episodeInfo[2];
                String normalDate = dateString;
                StringBuilder number = new StringBuilder();
                number.append("серия: ").append(episodeInfo[0]);
                if (normalDate.equalsIgnoreCase(today)) {
                    returnText += serial + ": " + number + ", " + episodeInfo[1] + "\n";
                }
            } while (cursor.moveToNext());

            cursor.close();
        }
        return returnText;
    }

}
