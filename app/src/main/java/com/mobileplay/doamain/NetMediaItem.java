package com.mobileplay.doamain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


public class NetMediaItem extends MediaItem implements Parcelable {
    private int id;
    private String movieName;
    private String coverImg;
    private int movieId;
    private String url;
    private String hightUrl;
    private String videoTitle;
    private int videoLength;
    private float rating;
    private List<String> type;
    private String summary;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHightUrl() {
        return hightUrl;
    }

    public void setHightUrl(String hightUrl) {
        this.hightUrl = hightUrl;
    }


    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public int getVideoLength() {
        return videoLength;
    }

    public void setVideoLength(int videoLength) {
        this.videoLength = videoLength;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", movieName='" + movieName + '\'' +
                ", coverImg='" + coverImg + '\'' +
                ", movieId=" + movieId +
                ", hightUrl='" + hightUrl + '\'' +
                ", videoTitle='" + videoTitle + '\'' +
                ", videoLength=" + videoLength +
                ", rating=" + rating +
                ", type=" + type +
                ", summary='" + summary + '\'' +
                '}';
    }

    @Override
    public String getName() {
        return getMovieName();
    }
    @Override
    public void setName(String name) {
        super.setName(name);
    }
    @Override
    public String getData() {
        return getHightUrl();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.movieName);
        dest.writeString(this.coverImg);
        dest.writeInt(this.movieId);
        dest.writeString(this.url);
        dest.writeString(this.hightUrl);
        dest.writeString(this.videoTitle);
        dest.writeInt(this.videoLength);
        dest.writeFloat(this.rating);
        dest.writeStringList(this.type);
        dest.writeString(this.summary);
    }

    public NetMediaItem() {
    }

    protected NetMediaItem(Parcel in) {
        this.id = in.readInt();
        this.movieName = in.readString();
        this.coverImg = in.readString();
        this.movieId = in.readInt();
        this.url = in.readString();
        this.hightUrl = in.readString();
        this.videoTitle = in.readString();
        this.videoLength = in.readInt();
        this.rating = in.readFloat();
        this.type=in.createStringArrayList();
        this.summary = in.readString();
    }

    public static final Parcelable.Creator<NetMediaItem> CREATOR = new Parcelable.Creator<NetMediaItem>() {
        @Override
        public NetMediaItem createFromParcel(Parcel source) {
            return new NetMediaItem(source);
        }

        @Override
        public NetMediaItem[] newArray(int size) {
            return new NetMediaItem[size];
        }
    };
}
