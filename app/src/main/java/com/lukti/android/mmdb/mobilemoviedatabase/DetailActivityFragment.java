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
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // The detail Activity called via intent.  Inspect the intent for forecast data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("MOVIE")) {
            Movie movie = intent.getParcelableExtra("MOVIE");
            //((TextView) rootView.findViewById(R.id.detail_text)).setText(movie.getOriginalTitle());
            ImageView poster = (ImageView)rootView.findViewById(R.id.detail_poster);
            Picasso.with(getActivity()).load(movie.getPosterPath()).into(poster);

            ((TextView)rootView.findViewById(R.id.detail_title)).setText(movie.getOriginalTitle());
            ((TextView)rootView.findViewById(R.id.detail_date)).setText(movie.getReleaseDate());
            ((TextView)rootView.findViewById(R.id.detail_rating)).setText(Double.toString(movie.getRating()));
            ((TextView)rootView.findViewById(R.id.detail_plot)).setText(movie.getPlot());
        }

        return rootView;
    }
}
