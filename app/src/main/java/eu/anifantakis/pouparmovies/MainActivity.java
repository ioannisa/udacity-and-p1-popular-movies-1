package eu.anifantakis.pouparmovies;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import java.io.IOException;
import java.net.URL;
import eu.anifantakis.pouparmovies.Utils.NetworkUtils;
import eu.anifantakis.pouparmovies.Utils.PopularMoviesJSonUtils;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ListItemClickListener{

    private MoviesAdapter moviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView mMoviesList = (RecyclerView) findViewById(R.id.main_rv_movies_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        mMoviesList.setLayoutManager(layoutManager);
        mMoviesList.setHasFixedSize(true);
        moviesAdapter = new MoviesAdapter(this);
        mMoviesList.setAdapter(moviesAdapter);

        if (NetworkUtils.isNetworkAvailable(this)){
            makeMoviesQuery(true);
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.main_no_network)
                    .setPositiveButton(R.string.main_no_network_close, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            // Create the AlertDialog object and return it
            builder.create().show();
        }
    }

    private void makeMoviesQuery(boolean fetchPopular){
        if (fetchPopular){
            setTitle(getString(R.string.main_most_popular_actionbar));
        }
        else{
            setTitle(getString(R.string.main_most_voted_actionbar));
        }

        // fetch the url with the API KEY
        String movieSearchUrlStr;
        if (fetchPopular)
            movieSearchUrlStr = getString(R.string.network_url_popular);
        else
            movieSearchUrlStr = getString(R.string.network_url_top_rated);

        URL movieSearchUrl = NetworkUtils.buildUrl(movieSearchUrlStr, getString(R.string.network_api_param), getString(R.string.network_api_key));
        new MovieQuery().execute(movieSearchUrl);
    }

    private Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        this.menu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_popular) {
            item.setChecked(true);
            menu.findItem(R.id.action_voted).setChecked(false);
            makeMoviesQuery(true);
            return true;
        }
        else if (id == R.id.action_voted){
            item.setChecked(true);
            menu.findItem(R.id.action_popular).setChecked(false);
            makeMoviesQuery(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.d("MAIN", "CLICK");

        //MoviesCollection.Movie movie = moviesAdapter.getMovieAtIndex(clickedItemIndex);
        //Toast.makeText(this, movie.getTitle(), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("movie",  moviesAdapter.getMovieAtIndex(clickedItemIndex));
        startActivity(intent);
    }

    private class MovieQuery extends AsyncTask<URL, Void, String>{

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
            moviesAdapter.setCollectionData(PopularMoviesJSonUtils.parseMoviesJson(s));
        }
    }
}
