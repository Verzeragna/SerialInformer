package ru.sergeiandreev.tvseriesinformer.searchclasses;

import android.content.Context;

public interface Search {
    Result mainSearch(String url) throws Exception;
    void update(String link, String serialName, Context ctx) throws Exception;
}
