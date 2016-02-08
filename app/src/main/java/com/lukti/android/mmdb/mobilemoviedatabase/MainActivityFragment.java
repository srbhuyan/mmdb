package com.lukti.android.mmdb.mobilemoviedatabase;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.lukti.android.mmdb.mobilemoviedatabase.adapter.ImageAdapter;
import com.lukti.android.mmdb.mobilemoviedatabase.data.Movie;

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
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private ImageAdapter mImageAdapter;
    private ArrayList<Movie> mMovies;
    private ArrayList<String> mPosterUrls;

    public MainActivityFragment() {
        mMovies = new ArrayList<Movie>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_movie_fragment, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
        mPosterUrls = new ArrayList<String>(Arrays.asList(posterUrls));

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView)rootView;
        mImageAdapter = new ImageAdapter(getActivity(), mPosterUrls);

        gridView.setAdapter(mImageAdapter);

        return rootView;
    }

    public void movieDataArrived(){
        mPosterUrls.clear();
        ArrayList<String> posterUrls = buildPosterUrls();
        mPosterUrls.addAll(posterUrls);
        mPosterUrls.addAll(posterUrls);
        mImageAdapter.notifyDataSetChanged();
    }

    private ArrayList<String> buildPosterUrls(){

        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_SIZE = "w185";

        ArrayList<String> posters = new ArrayList<String>();

        for( Movie movie:mMovies ){
            Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                    .appendPath(POSTER_SIZE)
                    .appendEncodedPath(movie.getPosterPath())
                    .build();
            posters.add(builtUri.toString());
        }

        for( String poster:posters ) {
            Log.v(LOG_TAG, "POSTER_URL: " + poster);
        }

        return posters;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            FetchMovieTask fetchMovieTask = new FetchMovieTask();
            fetchMovieTask.execute("popularity.desc");
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * FetchMovieTask AsynchTask to fetch the movie data
     */

    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        final String TMD_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
        final String SORT_PARAM = "sort_by";
        final String APPID_PARAM = "api_key";

        private ArrayList<Movie> getMoviesFromJson(String movieJsonStr) throws JSONException {

            final String TMD_RESULT = "results";
            final String TMD_POSTER = "poster_path";
            final String TMD_PLOT = "overview";
            final String TMD_RELEASE_DATE = "release_date";
            final String TMD_TITLE = "original_title";
            final String TMD_RATING = "vote_average";
            final String TMD_POPULARITY = "popularity";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(TMD_RESULT);

            ArrayList<Movie> movies = new ArrayList<Movie>();

            for(int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                movies.add(new Movie(
                        movie.getString(TMD_TITLE),
                        movie.getString(TMD_POSTER),
                        movie.getString(TMD_PLOT),
                        movie.getString(TMD_RELEASE_DATE),
                        movie.getDouble(TMD_RATING),
                        movie.getDouble(TMD_POPULARITY)
                ));
            }
            return movies;
        }

        protected ArrayList<Movie> doInBackground(String... params){

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = null;

            try {
                Uri builtUri = Uri.parse(TMD_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, params[0])
                        .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                //Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();

                //Log.v(LOG_TAG, "Movie JSON String: " + movieJsonStr);

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
                mMovies.clear();
                mMovies.addAll(movies);
                movieDataArrived();
            }
        }
    }
}
