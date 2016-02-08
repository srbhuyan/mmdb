package com.lukti.android.mmdb.mobilemoviedatabase.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.lukti.android.mmdb.mobilemoviedatabase.picasso.SquaredImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Tiklu on 2/7/2016.
 */
public class ImageAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<String> mPosterUrls;

    public ImageAdapter(Context c, ArrayList<String> p) {
        mContext = c;
        mPosterUrls = p;
    }

    public String getItem(int position) {
        return mPosterUrls.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public int getCount() {
        return mPosterUrls.size();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(600, 400));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mPosterIds.get(position));
        */
        SquaredImageView view = (SquaredImageView) convertView;

        if (view == null) {
            view = new SquaredImageView(mContext);
            view.setLayoutParams(new GridView.LayoutParams(400, 400));
           // view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setPadding(0, 0, 0, 0);
        }
        String url = getItem(position);

        Picasso.with(mContext).load(url).into(view);

        return view;
    }
}
