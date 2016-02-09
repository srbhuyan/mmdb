package com.lukti.android.mmdb.mobilemoviedatabase.data;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Tiklu on 2/7/2016.
 */
public class MovieAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<Movie> mMovies;
    private int mScreenWidth;
    private int mScreenHeight;

    public MovieAdapter(Context c, ArrayList<Movie> p) {
        mContext = c;
        mMovies = p;

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
        mScreenHeight = size.y;
    }

    public Movie getItem(int position) {
        return mMovies.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public int getCount() {
        return mMovies.size();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(mScreenWidth/2, mScreenHeight/2));
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }

        Movie movie = getItem(position);
        Picasso.with(mContext).load(movie.getPosterPath()).into(imageView);
        //Picasso.with(mContext).setIndicatorsEnabled(true);

        return imageView;
    }
}
