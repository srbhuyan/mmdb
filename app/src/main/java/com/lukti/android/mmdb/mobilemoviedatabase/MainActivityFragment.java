package com.lukti.android.mmdb.mobilemoviedatabase;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.lukti.android.mmdb.mobilemoviedatabase.adapter.ImageAdapter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<String> mMovieAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // references to our images
        Integer[] posterIds = {
                R.drawable.sample_2, R.drawable.sample_3,
                R.drawable.sample_4, R.drawable.sample_5,
                R.drawable.sample_6, R.drawable.sample_7,
                R.drawable.sample_0, R.drawable.sample_1,
                R.drawable.sample_2, R.drawable.sample_3,
                R.drawable.sample_4, R.drawable.sample_5,
                R.drawable.sample_6, R.drawable.sample_7,
                R.drawable.sample_0, R.drawable.sample_1,
                R.drawable.sample_2, R.drawable.sample_3,
                R.drawable.sample_4, R.drawable.sample_5,
                R.drawable.sample_6, R.drawable.sample_7
        };

        String[] posterUrls = {
                "http://i.imgur.com/DvpvklR.png",
                "http://i.imgur.com/DvpvklR.png",
                "http://i.imgur.com/DvpvklR.png",
                "http://i.imgur.com/DvpvklR.png",
                "http://i.imgur.com/DvpvklR.png",
                "http://i.imgur.com/DvpvklR.png",
                "http://i.imgur.com/DvpvklR.png",
                "http://i.imgur.com/DvpvklR.png",
                "http://i.imgur.com/DvpvklR.png",
                "http://i.imgur.com/DvpvklR.png",
                "http://i.imgur.com/DvpvklR.png",
                "http://i.imgur.com/DvpvklR.png"
        };

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView)rootView;
        //gridView.setAdapter(new ImageAdapter(getActivity(), new ArrayList<Integer>(Arrays.asList(posterIds))));
        gridView.setAdapter(new ImageAdapter(getActivity(), new ArrayList<String>(Arrays.asList(posterUrls))));

        return rootView;
    }
}
