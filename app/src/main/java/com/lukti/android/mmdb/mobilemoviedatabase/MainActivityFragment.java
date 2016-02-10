package com.lukti.android.mmdb.mobilemoviedatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.lukti.android.mmdb.mobilemoviedatabase.data.Movie;
import com.lukti.android.mmdb.mobilemoviedatabase.data.MovieAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * MainActivity fragment.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private ArrayList<Movie> mMovieBuffer;
    private MovieAdapter mMovieAdapter;
    private final int mPortraitNumCols  = 2;
    private final int mLandscapeNumCols = 4;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if( savedInstanceState != null ){
            mMovieBuffer = savedInstanceState.getParcelableArrayList(getString(R.string.movie_object_key));
        }else{
            mMovieBuffer = new ArrayList<Movie>();
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(getString(R.string.movie_object_key), mMovieBuffer);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        GridView gridView = (GridView)inflater.inflate(R.layout.fragment_main, container, false);

        int orientation = getResources().getConfiguration().orientation;
        if( orientation == Configuration.ORIENTATION_PORTRAIT ){
            gridView.setNumColumns(mPortraitNumCols);
        }else if( orientation == Configuration.ORIENTATION_LANDSCAPE ){
            gridView.setNumColumns(mLandscapeNumCols);
        }

        mMovieAdapter = new MovieAdapter(getActivity(), gridView, mMovieBuffer);
        gridView.setAdapter(mMovieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie movie = mMovieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(getString(R.string.movie_object_key), movie);
                startActivity(intent);
            }
        });

        return gridView;
    }

    private void fetchMovieData(){

        if( mMovieBuffer.size() == 0 ) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortPref = prefs.getString(getString(R.string.pref_movie_sort_order_key),
                    getString(R.string.sort_order_value_most_popular));

            new FetchMovieTask().execute(sortPref);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchMovieData();
    }

    /**
     * FetchMovieTask AsyncTask to fetch the movie data
     */

    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private String buildFullPosterPath(String partialPath){
            Uri builtUri = Uri.parse(getString(R.string.TMD_POSTER_BASE_URL)).buildUpon()
                    .appendPath(getString(R.string.TMD_POSTER_SIZE))
                    .appendEncodedPath(partialPath)
                    .build();
            return builtUri.toString();
        }

        private ArrayList<Movie> getMoviesFromJson(String movieJsonStr) throws JSONException {

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(getString(R.string.TMD_RESULT));

            ArrayList<Movie> movies = new ArrayList<Movie>();

            for(int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                movies.add(new Movie(
                        movie.getString(getString(R.string.TMD_TITLE)),
                        buildFullPosterPath(movie.getString(getString(R.string.TMD_POSTER))),
                        movie.getString(getString(R.string.TMD_PLOT)),
                        movie.getString(getString(R.string.TMD_RELEASE_DATE)),
                        movie.getDouble(getString(R.string.TMD_RATING)),
                        movie.getDouble(getString(R.string.TMD_POPULARITY))
                ));
            }
            return movies;
        }

        protected ArrayList<Movie> doInBackground(String... params){

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = null;

            try {
                Uri builtUri = Uri.parse(getString(R.string.TMD_BASE_URL)).buildUpon()
                        .appendQueryParameter(getString(R.string.TMD_SORT), params[0])
                        .appendQueryParameter(getString(R.string.TMD_API_KEY), BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                //Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                movieJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesFromJson(movieJsonStr);
            }catch(JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);
            if( movies != null ) {
                mMovieBuffer.addAll(movies);
                mMovieAdapter.notifyDataSetChanged();
            }
        }
    }
}
