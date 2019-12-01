package eu.anifantakis.pouparmovies;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.URL;

import eu.anifantakis.pouparmovies.Utils.MoviesCollection;
import eu.anifantakis.pouparmovies.Utils.NetworkUtils;
import eu.anifantakis.pouparmovies.Utils.PopularMoviesJSonUtils;

public class MainActivityViewModel extends AndroidViewModel {

    private boolean first;
    public boolean isFirst(){
        return first;
    }

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        first = true;
        isPopularLiveData.setValue(true);
    }



    /* MoviesCollection
     */
    private MutableLiveData<MoviesCollection> moviesCollectionLiveData = new MutableLiveData<>();
    public LiveData<MoviesCollection> getMoviesCollection(){
        return  moviesCollectionLiveData;
    }

    // MENU CHECKBOXES
    private MutableLiveData<Boolean> isPopularLiveData = new MutableLiveData<>();
    public LiveData<Boolean> getPopular(){
        return isPopularLiveData;
    }


    private Context getContext(){
        return getApplication().getApplicationContext();
    }

    public void makeMoviesQuery(boolean fetchPopular) {
        first = false;

        // fetch the url with the API KEY
        isPopularLiveData.setValue(fetchPopular);

        String movieSearchUrlStr;
        if (fetchPopular)
            movieSearchUrlStr = getContext().getString(R.string.network_url_popular);
        else
            movieSearchUrlStr = getContext().getString(R.string.network_url_top_rated);

        URL movieSearchUrl = NetworkUtils.buildUrl(movieSearchUrlStr, getContext().getString(R.string.network_api_param), getContext().getString(R.string.network_api_key));
        new MainActivityViewModel.MovieQuery().execute(movieSearchUrl);
    }


    private class MovieQuery extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String githubSearchResults = null;
            try {
                githubSearchResults = NetworkUtils.getResponseFromHttpUrl(urls[0]);
                return githubSearchResults;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            MoviesCollection mv = PopularMoviesJSonUtils.parseMoviesJson(s);
            moviesCollectionLiveData.postValue(mv);
        }
    }
}
