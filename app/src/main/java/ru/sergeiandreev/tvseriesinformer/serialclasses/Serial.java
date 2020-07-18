package ru.sergeiandreev.tvseriesinformer.serialclasses;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Serial implements Parcelable {
    private String name, seasonNumber, srcImage, link;
    private ArrayList<Episode> episodes;

    public Serial(String name, String seasonNumber, ArrayList<Episode> episodes, String srcImage, String link) {
        this.name = name;
        this.seasonNumber = seasonNumber;
        this.episodes = episodes;
        this.srcImage = srcImage;
        this.link = link;
    }

    public Serial(String name, String srcImage, String link) {
        this.name = name;
        this.srcImage = srcImage;
        this.link = link;
    }

    public Serial(Parcel in) {
        String[] data = new String[3];
        in.readStringArray(data);
        name= data[0];
        srcImage  = data[1];
        link = data[2];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(ArrayList<Episode> episodes) {
        this.episodes = episodes;
    }

    public String getSrcImage() {
        return srcImage;
    }

    public String getLink() {
        return link;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(String seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public void setSrcImage(String srcImage) {
        this.srcImage = srcImage;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public static final Comparator<Serial> COMPARE_BY_NAME = new Comparator<Serial>() {
        @Override
        public int compare(Serial lhs, Serial rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{name, srcImage, link});
    }

    public static final Parcelable.Creator<Serial> CREATOR = new Parcelable.Creator<Serial>() {

        @Override
        public Serial createFromParcel(Parcel source) {
            return new Serial(source);
        }

        @Override
        public Serial[] newArray(int size) {
            return new Serial[size];
        }
    };
}
