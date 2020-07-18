package ru.sergeiandreev.tvseriesinformer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ru.sergeiandreev.tvseriesinformer.serialclasses.Episode;
import ru.sergeiandreev.tvseriesinformer.serialclasses.Serial;

public class Utils {
    private DBHelper mDbHelper;

    public Utils(Context ctx) {
        this.mDbHelper = new DBHelper(ctx);
    }

    public ArrayList<Serial> displayDataBase(Context context) {
        mDbHelper = new DBHelper(context);
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        Cursor cursor = database.query(mDbHelper.TABLE_SERIES, null, null, null, null, null, null);// читаем из БД все данные. Класс cursor можно рассматривать, как набор строк с данными
        ArrayList<Serial> series = new ArrayList<>();
        if (cursor.moveToFirst()) { //проверка на выполнение запроса
            //получаем порядковые номера столбцов по их именам
            int serialIndex = cursor.getColumnIndex(mDbHelper.KEY_SERIAL);
            int imageIndex = cursor.getColumnIndex(mDbHelper.KEY_IMAGE);
            do {
                //создаем объект класса SubjectData и заполняем данными из БД
                series.add(new Serial(cursor.getString(serialIndex), "", null, cursor.getString(imageIndex), ""));
            } while (cursor.moveToNext());

            cursor.close();
            mDbHelper.close();
            Collections.sort(series, Serial.COMPARE_BY_NAME);
        }
        return series;
    }

    public Boolean deleteSerial(Context context, String serial) {
        mDbHelper = new DBHelper(context);
        boolean deleted = true;
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        Cursor cursor = database.query(mDbHelper.TABLE_SERIES, null, "serial = ?", new String[]{serial}, null, null, null);
        int idIndex, id, imageIndex;
        String imagePath;
        if (cursor.moveToFirst()) { //проверка на выполнение запроса
            idIndex = cursor.getColumnIndex(mDbHelper.KEY_ID);
            imageIndex = cursor.getColumnIndex(mDbHelper.KEY_IMAGE);
            do {
                id = cursor.getInt(idIndex);
                imagePath = cursor.getString(imageIndex);
            } while (cursor.moveToNext());
            database.delete(mDbHelper.TABLE_SERIES, "_id = " + id, null);
            File file = new File(imagePath);
            deleted = file.delete();
            cursor.close();
        }
        Map<Integer, String> map = new HashMap<>();
        Cursor cursorEpisode = database.query(mDbHelper.TABLE_EPISODES, null, "serial = ?", new String[]{serial}, null, null, null);
        if (cursorEpisode.moveToFirst()) {
            int episodeIndex = cursorEpisode.getColumnIndex(mDbHelper.KEY_EPISODE_INFO);
            int indIndex = cursorEpisode.getColumnIndex(mDbHelper.KEY_ID);
            do {
                map.put(cursorEpisode.getInt(indIndex), cursorEpisode.getString(episodeIndex));
            } while (cursorEpisode.moveToNext());
            cursorEpisode.close();
        }
        for (Map.Entry<Integer, String> el : map.entrySet()) {
            database.delete(mDbHelper.TABLE_EPISODES, "_id = " + el.getKey(), null);
        }
        mDbHelper.close();
        return deleted;
    }

    public writeResult writeToDatabase(Serial serial, File dir) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        Cursor serialCursor = database.query(mDbHelper.TABLE_SERIES, null, "serial = ?", new String[]{serial.getName()}, null, null, null);
        boolean isSerialInDataBase = false;
        if (serialCursor.moveToFirst()) {
            do {
                isSerialInDataBase = true;
            } while (serialCursor.moveToNext());
            if (isSerialInDataBase) {
                return writeResult.SERIAL_ALLREADY_EXIST;
            }
        }
        serialCursor.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put(mDbHelper.KEY_SERIAL, serial.getName());
        contentValues.put(mDbHelper.KEY_IMAGE, downloadImage(serial.getSrcImage(), dir, serial));
        contentValues.put(mDbHelper.KEY_LINK, serial.getLink());
        contentValues.put(mDbHelper.KEY_CURRENT_SEASON, serial.getSeasonNumber());
        database.insert(mDbHelper.TABLE_SERIES, null, contentValues);
        //пишем в базу эпизоды
        for (Episode element : serial.getEpisodes()) {
            contentValues = new ContentValues();
            contentValues.put(mDbHelper.KEY_SERIAL, serial.getName());
            contentValues.put(mDbHelper.KEY_EPISODE_INFO, element.toString());
            database.insert(mDbHelper.TABLE_EPISODES, null, contentValues);
        }
        return writeResult.SERIAL_ADDED;
    }

    private String downloadImage(String link, File dir, Serial serial) {
        String pathToFile = "";
        try {
            URL url = new URL(link);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            File new_folder = new File(dir + "/image");
            if (!new_folder.exists()) {
                new_folder.mkdir();
            }
            pathToFile = new_folder.getAbsolutePath() + "/"
                    + serial.getName()
                    + serial.getSrcImage().substring(serial.getSrcImage().length() - 4);
            File new_file = new File(pathToFile);
            if (!new_file.exists()) {
                new_file.createNewFile();
            }
            InputStream in = new BufferedInputStream(url.openStream(), 8192);
            byte[] byteArray = new byte[1024];
            OutputStream out = new BufferedOutputStream(new FileOutputStream(new_file));
            int total = 0;
            for (int b; (b = in.read(byteArray)) != -1; ) {
                total += b / 1000;
                out.write(byteArray, 0, b);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            return "";
        }
        return pathToFile;
    }
}
