package com.lukti.android.mmdb.mobilemoviedatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lukti.android.mmdb.mobilemoviedatabase.data.Movie;
import com.lukti.android.mmdb.mobilemoviedatabase.data.MovieRecyclerAdapter;
import com.lukti.android.mmdb.mobilemoviedatabase.data.RecyclerItemClickListener;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

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

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * MainActivity fragment.
 */
public class MainActivityFragment extends Fragment implements Paginate.Callbacks{

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private ArrayList<Movie> mMovieBuffer;
    private RecyclerView mRecyclerView;
    private MovieRecyclerAdapter mMovieAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private SharedPreferences mSharedPref;
    private String mSortPref;
    private boolean mPrefChanged;

    private static final int VERTICAL_SPAN_COUNT = 2;
    private static final int HORIZONTAL_SPAN_COUNT = 4;

    private final String SORT_PREF = "SORT_PREF";
    private final String TOTAL_PAGES = "TOTAL_PAGES";

    // pagination
    private int mTmdTotalPages;
    private int THRESHOLD = 4;

    private int mPage = 0;
    private Paginate mPaginate;
    private boolean mLoading = false;
    private boolean mAddLoadingRow = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if( savedInstanceState != null ){
            mMovieBuffer = savedInstanceState.getParcelableArrayList(getString(R.string.movie_object_key));
            mSortPref = savedInstanceState.getString(SORT_PREF);
            mTmdTotalPages = savedInstanceState.getInt(TOTAL_PAGES);
        }else{
            mMovieBuffer = new ArrayList<Movie>();
            mSortPref = "";
            mTmdTotalPages = Integer.MAX_VALUE;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(getString(R.string.movie_object_key), mMovieBuffer);
        outState.putString(SORT_PREF, mSortPref);
        outState.putInt(TOTAL_PAGES, mTmdTotalPages);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRecyclerView = (RecyclerView)inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(getActivity(), getSpanCount(), GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mMovieAdapter = new MovieRecyclerAdapter(getActivity(), mMovieBuffer);
        mRecyclerView.setItemAnimator(new SlideInUpAnimator());
        mRecyclerView.setAdapter(mMovieAdapter);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Movie movie = mMovieAdapter.getItem(position);
                        Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(getString(R.string.movie_object_key), movie);
                        startActivity(intent);
                    }
                }
                ));

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setupPagination();
        return mRecyclerView;
    }

    @Override
    public void onStart() {
        super.onStart();
        String sortPref = mSharedPref.getString(getString(R.string.pref_movie_sort_order_key),
                getString(R.string.sort_order_value_most_popular));

        mPrefChanged = sortPref.equals(mSortPref) ? false : true;

        if( mPrefChanged ){
            mPage = 0;
            mLoading = false;
            mMovieBuffer.clear();
            mMovieAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mSortPref = mSharedPref.getString(getString(R.string.pref_movie_sort_order_key),
                getString(R.string.sort_order_value_most_popular));
    }

    private void fetchMovieData(){
        mPage++;
        new FetchMovieTask(mPage).execute(mSharedPref.getString(getString(R.string.pref_movie_sort_order_key),
                getString(R.string.sort_order_value_most_popular)));
    }

    private int getSpanCount(){
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ?
                VERTICAL_SPAN_COUNT : HORIZONTAL_SPAN_COUNT;
    }

    // pagination implementation
    protected void setupPagination() {
        // if RecyclerView was recently bound, unbind
        if (mPaginate != null) {
            mPaginate.unbind();
        }

        mLoading = false;
        mPage = 0;

        mPaginate = Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(THRESHOLD)
                .addLoadingListItem(mAddLoadingRow)
                .setLoadingListItemCreator(null)
                .setLoadingListItemSpanSizeLookup(new LoadingListItemSpanLookup() {
                    @Override
                    public int getSpanSize() {
                        return getSpanCount();
                    }
                })
                .build();
    }

    @Override
    public void onLoadMore() {
        mLoading = true;
        fetchMovieData();
    }

    @Override
    public boolean isLoading() {
        return mLoading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return mPage == mTmdTotalPages;
    }

    /**
     * FetchMovieTask AsyncTask to fetch the movie data
     */

    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        private int pageToFetch;
        private int totalPages;

        public FetchMovieTask(int page){
            this.pageToFetch = page;
        }

        private String buildFullPosterPath(String partialPath){
            Uri builtUri = Uri.parse(getString(R.string.TMD_POSTER_BASE_URL)).buildUpon()
                    .appendPath(getString(R.string.TMD_POSTER_SIZE))
                    .appendEncodedPath(partialPath)
                    .build();
            return builtUri.toString();
        }

        private ArrayList<Movie> getMoviesFromJson(String movieJsonStr) throws JSONException {

            JSONObject movieJson = new JSONObject(movieJsonStr);
            totalPages = movieJson.getInt(getString(R.string.TMD_TOTAL_PAGES));
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
                        .appendQueryParameter(getString(R.string.TMD_PAGE), Integer.toString(pageToFetch))
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
            if( movies != null) {
                mMovieBuffer.addAll(movies);
                mMovieAdapter.notifyDataSetChanged();
                mLoading = false;
                mTmdTotalPages = totalPages;
            }
        }
    }
}
