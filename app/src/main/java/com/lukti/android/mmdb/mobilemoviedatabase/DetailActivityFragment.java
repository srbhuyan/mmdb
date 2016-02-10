package com.lukti.android.mmdb.mobilemoviedatabase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lukti.android.mmdb.mobilemoviedatabase.data.Movie;
import com.squareup.picasso.Picasso;

/**
 * Movie detail fragment
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(getString(R.string.movie_object_key))) {
            Movie movie = intent.getParcelableExtra(getString(R.string.movie_object_key));

            ImageView poster = (ImageView)rootView.findViewById(R.id.detail_poster);
            Picasso.with(getActivity()).load(movie.getPosterPath()).into(poster);

            ((TextView)rootView.findViewById(R.id.detail_title)).setText(movie.getOriginalTitle());
            ((TextView)rootView.findViewById(R.id.detail_date)).setText(movie.getReleaseDate());
            ((TextView)rootView.findViewById(R.id.detail_rating)).setText(Double.toString(movie.getRating()) + "/10");
            ((TextView)rootView.findViewById(R.id.detail_plot)).setText(movie.getPlot());
        }

        return rootView;
    }
}
