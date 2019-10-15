package com.mobileplay.doamain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Trailers {
    private ArrayList<Movie> trailers;

    public ArrayList<Movie> getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<Movie> trailers) {
        this.trailers = trailers;
    }

    @Override
    public String toString() {
        return "Trailers{" +
                "trailers=" + trailers +
                '}';
    }
}
