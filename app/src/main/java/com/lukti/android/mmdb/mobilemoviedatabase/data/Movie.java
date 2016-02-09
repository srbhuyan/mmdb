package com.lukti.android.mmdb.mobilemoviedatabase.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tiklu on 2/8/2016.
 */
public class Movie implements Parcelable{

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

    protected Movie(Parcel in) {
        originalTitle = in.readString();
        posterPath = in.readString();
        plot = in.readString();
        releaseDate = in.readString();
        rating = in.readDouble();
        popularity = in.readDouble();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalTitle);
        dest.writeString(posterPath);
        dest.writeString(plot);
        dest.writeString(releaseDate);
        dest.writeDouble(rating);
        dest.writeDouble(popularity);
    }
}
