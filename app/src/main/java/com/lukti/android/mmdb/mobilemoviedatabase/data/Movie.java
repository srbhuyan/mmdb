package com.lukti.android.mmdb.mobilemoviedatabase.data;

/**
 * Created by Tiklu on 2/8/2016.
 */
public class Movie {

    private String originalTitle;
    private String posterPath;
    private String plot;
    private String releaseDate;
    private double rating;
    private double popularity;

    public Movie(String title, String poster, String plot, String rDate, double rating, double popularity){
        this.originalTitle = title;
        this.posterPath = poster;
        this.plot = plot;
        this.releaseDate = rDate;
        this.rating = rating;
        this.popularity = popularity;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getPlot() {
        return plot;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public double getRating() {
        return rating;
    }

    public double getPopularity() {
        return popularity;
    }
}
