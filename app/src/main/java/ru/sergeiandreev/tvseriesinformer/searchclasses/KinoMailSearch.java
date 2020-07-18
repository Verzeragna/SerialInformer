package ru.sergeiandreev.tvseriesinformer.searchclasses;

import android.content.Context;
import android.os.StrictMode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.sergeiandreev.tvseriesinformer.serialclasses.Episode;
import ru.sergeiandreev.tvseriesinformer.serialclasses.Serial;

public class KinoMailSearch implements Search {

    private Result kinoMailResult = new Result();

    private void getAllList(Document mDoc) {
        Elements findSerials = mDoc.getElementsByClass("searchitem__item");
        int count;
        if (findSerials.size() - 1 > 20) {
            count = 20;
        } else {
            count = findSerials.size() - 1;
        }
        for (int i = 0; i < count; i++) {
            String serialTitle, LinkSrcImage = "", serailLink = "";
            Element serialInfo = findSerials.get(i).getElementsByClass("margin_top_20").first();
            Element link = serialInfo.getElementsByTag("a").first();
            serailLink = "https://kino.mail.ru/" + link.attr("href");
            serialTitle = serialInfo.text();
            Element photo = findSerials.get(i).getElementsByClass("photo__inner").first();
            Element media = photo.select("[src]").first();
            LinkSrcImage = media.attr("abs:src");
            kinoMailResult.getSerialList().add(new Serial(serialTitle, LinkSrcImage, serailLink));
        }
    }

    @Override
    public Result mainSearch(String url) throws Exception {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Document doc, docPage, seasonPage;
        Element lastSeason;
        kinoMailResult.setUrl(url);
        doc = Jsoup.connect(url).get();// получаем страницу по url
        getAllList(doc);
        if (kinoMailResult.getSerialList().size() > 0) {
            for (Serial sData : kinoMailResult.getSerialList()) {
                docPage = Jsoup.connect(sData.getLink()).get();
                Elements seasonsWrapper = docPage.getElementsByClass("cols__wrapper");
                Elements seasons = seasonsWrapper.get(2).getElementsByClass("cols__inner");
                if(seasons.size()>2) {
                    lastSeason = seasons.get(seasons.size() - 1);
                }else{
                    lastSeason = seasons.get(0);
                }
                Elements links = lastSeason.getElementsByTag("a");
                String serailLink = "https://kino.mail.ru" + links.get(0).attr("href");
                seasonPage = Jsoup.connect(serailLink).get();
                Elements tableWrapper = seasonPage.getElementsByClass("cols__wrapper");
                Elements tableInner = tableWrapper.get(1).getElementsByClass("cols__inner");
                Elements series = tableInner.get(0).getElementsByClass("table");
                int count=1;
                count++;
            }
        }
        return null;
    }

    @Override
    public void update(String link, String serialName, Context ctx) {

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
