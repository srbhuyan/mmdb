package com.lukti.android.mmdb.mobilemoviedatabase.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lukti.android.mmdb.mobilemoviedatabase.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Tiklu on 2/7/2016.
 */
public class MovieAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<Movie> mMovies;
    private View mParentView;

    public MovieAdapter(Context c, View p, ArrayList<Movie> m ) {
        mContext = c;
        mParentView = p;
        mMovies = m;
    }

    public void clear(){
        if(mMovies != null) mMovies.clear();
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Movie> movies){
        if( mMovies != null) mMovies.addAll(movies);
        notifyDataSetChanged();
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

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.poster_view, parent, false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mParentView.getWidth() / ((GridView) mParentView).getNumColumns(),
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            convertView.setLayoutParams(layoutParams);
        }

        Movie movie = getItem(position);
        Picasso.with(mContext).load(movie.getPosterPath()).into((ImageView) convertView);

        return convertView;
    }
}
