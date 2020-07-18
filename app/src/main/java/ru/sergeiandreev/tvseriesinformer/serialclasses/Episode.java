package ru.sergeiandreev.tvseriesinformer.serialclasses;

public class Episode {
    private String number, name, date;

    public Episode (String number, String name, String date){
        this.number = number;
        this.name = name;
        this.date = date;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return number + "," + name + "," + date;
    }
}
