package ru.sergeiandreev.tvseriesinformer.searchclasses;

import java.util.ArrayList;

import ru.sergeiandreev.tvseriesinformer.serialclasses.Serial;

public class Result {
    private String url;
    private ArrayList<Serial> serialList = new ArrayList<>();
    private boolean workResult = false;
    private String status="";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList<Serial> getSerialList() {
        return serialList;
    }

    public void setSerialList(ArrayList<Serial> serialList) {
        this.serialList = serialList;
    }

    public boolean isWorkResult() {
        return workResult;
    }

    public void setWorkResult(boolean workResult) {
        this.workResult = workResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
