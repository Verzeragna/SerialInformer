package ru.sergeiandreev.tvseriesinformer.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;

import java.util.ArrayList;

import ru.sergeiandreev.tvseriesinformer.adapters.CustomAdapter;
import ru.sergeiandreev.tvseriesinformer.database.DBHelper;
import ru.sergeiandreev.tvseriesinformer.R;

public class EpisodesList extends AppCompatActivity {
   // private RecyclerView lv;
    private ListView lv;
    private String serial;
    private DBHelper mDbHelper;
    private ArrayList<String> episodes = new ArrayList<>();
    private CustomAdapter mCustomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes);
        lv = findViewById(R.id.episodes_list);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(lv.getContext(),
                DividerItemDecoration.VERTICAL);
        serial = getIntent().getExtras().getString("serial");
        mDbHelper = new DBHelper(this);
        showEpisodes(serial);
    }

    void showEpisodes(String serial) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        Cursor cursor = database.query(mDbHelper.TABLE_EPISODES, null, "serial = ?", new String[]{serial}, null, null, null);
        String seasonNumber = "";
        if (cursor.moveToFirst()) { //проверка на выполнение запроса
            //получаем порядковые номера столбцов по их именам
            int lineIndex = cursor.getColumnIndex(mDbHelper.KEY_EPISODE_INFO);
            do {
                episodes.add(cursor.getString(lineIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();
        Cursor cursorSerial = database.query(mDbHelper.TABLE_SERIES, null, "serial = ?", new String[]{serial}, null, null, null);
        if (cursorSerial.moveToFirst()) { //проверка на выполнение запроса
            //получаем порядковые номера столбцов по их именам
            int seasonIndex = cursorSerial.getColumnIndex(mDbHelper.KEY_CURRENT_SEASON);
            do {
                seasonNumber = cursorSerial.getString(seasonIndex);
            } while (cursorSerial.moveToNext());
        }
        cursorSerial.close();
        mDbHelper.close();
        mCustomAdapter = new CustomAdapter(this, episodes, seasonNumber);
        lv.setAdapter(mCustomAdapter);//передаем адаптер в ListView
    }
}
