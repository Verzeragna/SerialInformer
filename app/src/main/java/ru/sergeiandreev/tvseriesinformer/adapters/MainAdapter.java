package ru.sergeiandreev.tvseriesinformer.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import ru.sergeiandreev.tvseriesinformer.database.DBHelper;
import ru.sergeiandreev.tvseriesinformer.R;
import ru.sergeiandreev.tvseriesinformer.serialclasses.Serial;

public class MainAdapter extends ArrayAdapter<Serial> {
    private ArrayList<Serial> mSubject;
    private LayoutInflater mInflater;
    private Context ctx;
    private DBHelper mDbHelper;
    private boolean isScrolling;
    public MainAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Serial> objects) {
        super(context, resource, objects);
        this.mSubject = objects;
        this.mInflater = LayoutInflater.from(context);
        this.ctx = context;
    }

    public static final class ViewHolder {
        ImageView mImage;
        TextView mName;
        TextView mDate;
    }

    public void setmSubject(ArrayList<Serial> mSubject) {
        this.mSubject = mSubject;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;
        Serial serial = mSubject.get(i);

        if (view == null) {
            // Создаем View из файла разметки
            view = mInflater.inflate(R.layout.activity_main_item, viewGroup, false);

            // Создаем ViewHolder класс и ассоциируем элементы управления
            holder = new ViewHolder();
            holder.mImage = view.findViewById(R.id.image_view);
            holder.mName=  view.findViewById(R.id.text_name);
            holder.mDate =  view.findViewById(R.id.text_date);

            // Сохраняем holder в элементе
            view.setTag(holder);
        } else {
            // Объект convertView уже создан и
            // метод findViewById не вызывается.
            // Получаем holder из элемента
            holder = (ViewHolder) view.getTag();
        }

        // заполняем атрибуты соответствующими значениями
        holder.mImage.setImageURI(Uri.parse(serial.getSrcImage()));
        holder.mName.setText(serial.getName());
        String nextDateEpisode = getNextDateEpisode(serial.getName());

        if (nextDateEpisode.contains("Сериал")){
            holder.mDate.setText(nextDateEpisode);
        }else{
            holder.mDate.setText(ctx.getString(R.string.next_episode_date_info) + " " + nextDateEpisode.replaceAll("\\W"," "));
        }

        return view;
    }

    private String getNextDateEpisode(String serial){
        ArrayList<Date> dateArray = new ArrayList<>();
        Date dateNow = new Date();
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd.MMMM.yyyy");
        String today = simpleDate.format(dateNow),normalDate="";
        mDbHelper = new DBHelper(ctx);
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        Cursor cursor = database.query(mDbHelper.TABLE_EPISODES,null,"serial = ?",new String[] {serial},null,null,null);
        if (cursor.moveToFirst()){ //проверка на выполнение запроса
            int episodeIndex = cursor.getColumnIndex(mDbHelper.KEY_EPISODE_INFO);
            do {
                String[] episodeInfo = cursor.getString(episodeIndex).split(",");
                if (episodeInfo.length<4) {
                    String dateString = episodeInfo[2];
                    normalDate = getDate(dateString);
                    String[] dateSplit = normalDate.split("[\\.]");
                    if (dateSplit.length < 3) {
                        normalDate = checkDate(normalDate);
                    }
                }else{
                    String dateString = episodeInfo[3];
                    normalDate = getDate(dateString);
                    String[] dateSplit = normalDate.split("[\\.]");
                    if (dateSplit.length < 3) {
                        normalDate = checkDate(normalDate);
                    }
                }
                try {
                    dateArray.add(simpleDate.parse(normalDate.trim()));
                } catch (ParseException e) {
                    //Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
                }
            }while (cursor.moveToNext());
            cursor.close();
            mDbHelper.close();
            Collections.sort(dateArray);
            for(Date date : dateArray){
                try {
                    Date todayDate = simpleDate.parse(today);
                    if (date.getTime()>=todayDate.getTime()){
                        return simpleDate.format(date);
                    }
                } catch (ParseException e) {
                }
            }
            return "Неизвестно";
        } return ctx.getString(R.string.dont_have_new_episodes);
    }

    private String checkDate (String normalDate){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(normalDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        stringBuilder.append(".");
        stringBuilder.append(calendar.get(Calendar.YEAR));
        return stringBuilder.toString();
    }

    private String getDate(String dateString){
        String[] dateArray = dateString.trim().split(" ");
        if (dateArray.length!=1) {
            String language = Locale.getDefault().getLanguage();
            if (language.equals("en")) {
                String[] arrayPatternRus = {"jan", "feb", "mer", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};
                String[] monthPatternRus = {"january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december"};
                for (int i = 0; i < arrayPatternRus.length; i++) {
                    if (dateArray[1].indexOf(arrayPatternRus[i]) != -1) {
                        dateArray[1] = monthPatternRus[i];
                    }
                }
            }else{
                String[] arrayPatternRus = {"янв", "фев", "мар", "апр", "мая", "июн", "июл", "авг", "сен", "окт", "ноя", "дек"};
                String[] monthPatternRus = {"января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"};
                for (int i = 0; i < arrayPatternRus.length; i++) {
                    if (dateArray[1].indexOf(arrayPatternRus[i]) != -1) {
                        dateArray[1] = monthPatternRus[i];
                    }
                }
            }

            String str = "";
            for (int i = 0; i < dateArray.length; i++) {
                str += dateArray[i] + ".";
            }
            return str.substring(0, str.length() - 1);
        }else return dateString;
    }

    @Nullable
    @Override
    public Serial getItem(int position) {
        return mSubject.get(position);
    }
}
