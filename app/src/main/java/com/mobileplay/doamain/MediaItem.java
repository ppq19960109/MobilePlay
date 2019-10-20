package com.mobileplay.doamain;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class MediaItem implements Serializable, Parcelable {
    private String name;
    private long duration;
    private long size;
    private String data;
    private String artist;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "MediaItem{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", data='" + data + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.duration);
        dest.writeLong(this.size);
        dest.writeString(this.data);
        dest.writeString(this.artist);
    }

    public MediaItem() {
    }

    protected MediaItem(Parcel in) {
        this.name = in.readString();
        this.duration = in.readLong();
        this.size = in.readLong();
        this.data = in.readString();
        this.artist = in.readString();
    }

    public static final Parcelable.Creator<MediaItem> CREATOR = new Parcelable.Creator<MediaItem>() {
        @Override
        public MediaItem createFromParcel(Parcel source) {
            return new MediaItem(source);
        }

        @Override
        public MediaItem[] newArray(int size) {
            return new MediaItem[size];
        }
    };
}
