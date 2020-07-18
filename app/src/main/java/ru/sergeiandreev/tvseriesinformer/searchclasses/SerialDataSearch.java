package ru.sergeiandreev.tvseriesinformer.searchclasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ru.sergeiandreev.tvseriesinformer.database.DBHelper;
import ru.sergeiandreev.tvseriesinformer.serialclasses.Episode;
import ru.sergeiandreev.tvseriesinformer.serialclasses.Serial;

public class SerialDataSearch implements Search {
    private Result serialDataResult = new Result();

    private void getAllList(Document mDoc) {
        Elements findSerials = mDoc.getElementsByTag("table");
        int count;
        if (findSerials.size() - 1 > 20) {
            count = 20;
        } else {
            count = findSerials.size() - 1;
        }
        for (int i = 0; i < count; i++) {
            String serialTitle;
            Elements links = findSerials.get(i).getElementsByTag("a");
            String serailLink = "https://serialdata.ru/" + links.get(0).attr("href");
            Elements serialName = links.get(1).getElementsByClass("fir_name");
            serialTitle = serialName.get(0).text();
            Elements img = findSerials.get(i).getElementsByTag("img");
            String LinkSrcImage = "https://serialdata.ru" + img.get(0).attr("src");
            serialDataResult.getSerialList().add(new Serial(serialTitle, LinkSrcImage, serailLink));
        }
    }

    @Override
    public Result mainSearch(String url) throws Exception {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Document doc, docPage;
        serialDataResult.setUrl(url);
        doc = Jsoup.connect(url).get();// получаем страницу по url
        getAllList(doc);
        if (serialDataResult.getSerialList().size() > 0) {
            for (Serial sData : serialDataResult.getSerialList()) {
                docPage = Jsoup.connect(sData.getLink()).get();
                Element head = docPage.getElementsByClass("episodes_header").first();
                String seasonNumber = head.text().split(" ")[0];
                sData.setSeasonNumber(seasonNumber);
                Element table = docPage.getElementsByClass("episodes_first_season").first();
                Elements episodes = table.getElementsByTag("span");
                sData.setEpisodes(getEpisodes(episodes));
            }
        }
        return serialDataResult;
    }

    @Override
    public void update(String link, String serialName, Context ctx) throws Exception {
        Document docPage;
        String seasonNumber;
        docPage = Jsoup.connect(link).get();
        Element head = docPage.getElementsByClass("episodes_header").first();
        seasonNumber = head.text().split(" ")[0];
        Element table = docPage.getElementsByClass("episodes_first_season").first();
        Elements episodes = table.getElementsByTag("span");
        updateData(seasonNumber, getEpisodes(episodes), serialName, ctx);
    }

    private void updateData(String seasonNumber, ArrayList<Episode> epList, String serial, Context ctx) {
        if(epList.size()!=0) {
            Map<Integer, String> map = new HashMap<>();
            DBHelper mDbHelper = new DBHelper(ctx);
            SQLiteDatabase database = mDbHelper.getWritableDatabase();
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
            cursorEpisode.close();
            Cursor cursorSerial = database.query(mDbHelper.TABLE_EPISODES, null, "serial = ?", new String[]{serial}, null, null, null);
            if (cursorSerial.moveToFirst()) {
                int idIndex = cursorSerial.getColumnIndex(mDbHelper.KEY_ID);
                do {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(mDbHelper.KEY_CURRENT_SEASON, seasonNumber);
                    database.update(mDbHelper.TABLE_SERIES, contentValues, mDbHelper.KEY_ID + "=" + cursorSerial.getString(idIndex), null);
                } while (cursorSerial.moveToNext());
                cursorSerial.close();
            }
            for (Episode episode : epList) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(mDbHelper.KEY_SERIAL, serial);
                contentValues.put(mDbHelper.KEY_EPISODE_INFO, episode.toString());
                database.insert(mDbHelper.TABLE_EPISODES, null, contentValues);
            }
        }
    }

    private ArrayList<Episode> getEpisodes(Elements episodes) throws Exception {
        ArrayList<Episode> episodeList = new ArrayList<>();
        //берем информацию из первого элемента
        Elements info = episodes.get(0).getElementsByTag("meta");
        String episodeNumber = info.get(0).attr("content");
        String episodeName = info.get(2).attr("content");
        Elements dateInfo = episodes.get(1).getElementsByTag("meta");
        String episodeDate = dateInfo.get(0).attr("content");
        episodeList.add(new Episode(episodeNumber, episodeName, correctDate(episodeDate)));
        for (int i = 2; i < episodes.size() - 1; i = i + 2) {
            info = episodes.get(i).getElementsByTag("meta");
            episodeNumber = info.get(0).attr("content");
            episodeName = info.get(2).attr("content");
            dateInfo = episodes.get(i + 1).getElementsByTag("meta");
            episodeDate = dateInfo.get(0).attr("content");
            episodeList.add(new Episode(episodeNumber, episodeName, correctDate(episodeDate)));
        }
        return episodeList;
    }

    private String correctDate(String sDate) throws Exception {
        if(sDate.equals("Неизвестно")){ return "Неизвестно";}
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(sDate);
        return new SimpleDateFormat("dd.MMMM.yyyy").format(date);
    }
}
