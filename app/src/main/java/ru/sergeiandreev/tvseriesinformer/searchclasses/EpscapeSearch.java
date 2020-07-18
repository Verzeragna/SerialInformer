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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ru.sergeiandreev.tvseriesinformer.database.DBHelper;
import ru.sergeiandreev.tvseriesinformer.database.Utils;
import ru.sergeiandreev.tvseriesinformer.serialclasses.Episode;
import ru.sergeiandreev.tvseriesinformer.serialclasses.Serial;

public class EpscapeSearch implements Search {

    private Result epscapeResult = new Result();

    private void getAllList(Document mDoc) throws Exception {
        Elements elements = mDoc.getElementsByClass("project-cards");
        int count;
        Elements findSerials = elements.get(0).getElementsByClass("project-card");
        if (findSerials.size() > 20) {
            count = 20;
        } else {
            count = findSerials.size();
        }
        for (int i = 0; i < count; i++) {
            String serialTitle;
            Elements links = findSerials.get(i).getElementsByTag("a");
            String serailLink = "https://epscape.com" + links.get(0).attr("href");
            serialTitle = links.get(1).text();
            Elements img = findSerials.get(i).getElementsByTag("img");
            String LinkSrcImage = img.get(0).attr("src");
            epscapeResult.getSerialList().add(new Serial(serialTitle, LinkSrcImage, serailLink));
        }
    }

    @Override
    public Result mainSearch(String url) throws Exception {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Document doc, docPage;
        epscapeResult.setUrl(url);
        doc = Jsoup.connect(url).get();// получаем страницу по url
        getAllList(doc);
        if (epscapeResult.getSerialList().size() > 0) {
            for (Serial sData : epscapeResult.getSerialList()) {
                docPage = Jsoup.connect(sData.getLink()).get();
                Elements elements = docPage.getElementsByClass("show-episodes");
                for (int i = 21; i > 0; i--) {
                    String str = "season" + i;
                    Element season = elements.get(0).getElementById(str);
                    if (season != null) {
                        sData.setSeasonNumber(String.valueOf(i));
                        Elements episodes = season.getElementsByClass("episode-item");
                        sData.setEpisodes(getEpisodes(episodes));
                        break;
                    }
                }
            }
        }
        return epscapeResult;
    }

    @Override
    public void update(String link, String serialName, Context ctx) throws Exception {
        Document docPage;
        String seasonNumber;
        docPage = Jsoup.connect(link).get();
        Elements elements = docPage.getElementsByClass("show-episodes");
        for (int i = 21; i > 0; i--) {
            String str = "season" + i;
            Element season = elements.get(0).getElementById(str);
            if (season != null) {
                seasonNumber = String.valueOf(i);
                Elements episodes = season.getElementsByClass("episode-item");
                updateData(seasonNumber, getEpisodes(episodes), serialName, ctx);
                break;
            }
        }
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
            Cursor cursorSerial = database.query(mDbHelper.TABLE_SERIES, null, "serial = ?", new String[]{serial}, null, null, null);
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
        for (Element episode : episodes) {
            Elements episodeItemNumber = episode.getElementsByClass("episode-item__number");
            String episodeNumber = episodeItemNumber.attr("content");
            Elements episodeItemDate = episode.getElementsByClass("episode-item__date");
            String episodeDate = episodeItemDate.attr("content");
            Elements episodeItemName = episode.getElementsByClass("episode-item__name");
            String episodeName = episodeItemName.get(0).text();
            episodeList.add(new Episode(episodeNumber, episodeName, correctDate(episodeDate)));
        }
        return episodeList;
    }

    private String correctDate(String sDate) throws Exception {
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(sDate);
        return new SimpleDateFormat("dd.MMMM.yyyy").format(date);
    }

}
