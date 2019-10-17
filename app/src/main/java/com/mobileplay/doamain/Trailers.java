package com.mobileplay.doamain;

import java.util.ArrayList;

public class Trailers {
    private ArrayList<NetMediaItem> trailers;

    public ArrayList<NetMediaItem> getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<NetMediaItem> trailers) {
        this.trailers = trailers;
    }

    @Override
    public String toString() {
        return "Trailers{" +
                "trailers=" + trailers +
                '}';
    }
}
