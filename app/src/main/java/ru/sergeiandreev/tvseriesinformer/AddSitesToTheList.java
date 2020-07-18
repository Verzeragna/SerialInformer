package ru.sergeiandreev.tvseriesinformer;

import java.util.ArrayList;

public class AddSitesToTheList {
    public ArrayList<String> addSites(){
        //заполняем лист с сайтами поиска
        ArrayList<String> links = new ArrayList<>();
        links.add("https://epscape.com/search/series?q=");
        links.add("https://serialdata.ru/search.php?Search=");
        links.add("https://kino.mail.ru/search/?region_id=134&q=");
        return links;
    }
}
