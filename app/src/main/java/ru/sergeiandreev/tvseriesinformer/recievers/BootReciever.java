package ru.sergeiandreev.tvseriesinformer.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.sergeiandreev.tvseriesinformer.services.CreateAlarmNotification;

public class BootReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentService = new Intent(context, CreateAlarmNotification.class);
        context.startService(intentService);
    }
}
