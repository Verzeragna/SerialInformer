package ru.sergeiandreev.tvseriesinformer.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.sergeiandreev.tvseriesinformer.database.DBHelper;
import ru.sergeiandreev.tvseriesinformer.searchclasses.EpscapeSearch;
import ru.sergeiandreev.tvseriesinformer.searchclasses.Result;
import ru.sergeiandreev.tvseriesinformer.searchclasses.SerialDataSearch;
import ru.sergeiandreev.tvseriesinformer.serialclasses.Serial;

public class UpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        doUpdate();

        return Service.START_REDELIVER_INTENT;
    }


    private void doUpdate() {
        doUpdate doUpdate = new doUpdate();
        doUpdate.execute("");
    }

    private void startSetAlarm(){
        startService(new Intent(this,CreateAlarmNotification.class));
        stopSelf();
        onDestroy();
    }

    private class doUpdate extends AsyncTask<String,String,String> {
        private EpscapeSearch epscapeSearch;
        private SerialDataSearch serialDataSearch;
        private DBHelper mDbHelper;

        @Override
        protected String doInBackground(String... str) {
            int linkIndex, resourceId, nameIndex;
            String link, serialName;
            mDbHelper = new DBHelper(getApplicationContext());
            SQLiteDatabase database = mDbHelper.getWritableDatabase();
            Cursor cursor = database.query(mDbHelper.TABLE_SERIES, null, null, null, null, null, null);
            if (cursor.moveToFirst()) { //проверка на выполнение запроса
                linkIndex = cursor.getColumnIndex(mDbHelper.KEY_LINK);
                nameIndex = cursor.getColumnIndex(mDbHelper.KEY_SERIAL);
                do {
                    link = cursor.getString(linkIndex);
                    serialName = cursor.getString(nameIndex);
                    resourceId = getResourceId(link);
                    switch (resourceId){
                        case 1:
                            epscapeSearch = new EpscapeSearch();
                            try {
                                epscapeSearch.update(link, serialName, getApplicationContext());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case 2:
                            serialDataSearch = new SerialDataSearch();
                            try {
                                serialDataSearch.update(link, serialName, getApplicationContext());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }

                } while (cursor.moveToNext());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            startSetAlarm();
        }

        private int getResourceId(String link){
            if(link.contains("epscape")){
                return 1;
            }else return 2;
        }
    }
}
