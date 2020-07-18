package ru.sergeiandreev.tvseriesinformer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "tvSeries";
    public static final String TABLE_SERIES = "series";
    public static final String TABLE_EPISODES = "episodes";

    public static final String KEY_ID = "_id";
    public static final String KEY_SERIAL = "serial";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_LINK = "link";
    public static final String KEY_CURRENT_SEASON = "current_season";
    public static final String KEY_EPISODE_INFO = "episode_info";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_SERIES + "(" + KEY_ID
                + " integer primary key," + KEY_SERIAL + " text," + KEY_CURRENT_SEASON + " text," + KEY_IMAGE + " text," + KEY_LINK + " text" + ")");

        db.execSQL("create table " + TABLE_EPISODES + "(" + KEY_ID
                + " integer primary key," + KEY_SERIAL + " text," + KEY_EPISODE_INFO + " text" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists " + TABLE_SERIES);

        db.execSQL("drop table if exists " + TABLE_EPISODES);

        onCreate(db);
    }
}
