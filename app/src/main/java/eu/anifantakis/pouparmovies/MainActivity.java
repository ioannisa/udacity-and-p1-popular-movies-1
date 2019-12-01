package eu.anifantakis.pouparmovies;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import eu.anifantakis.pouparmovies.Utils.MoviesCollection;
import eu.anifantakis.pouparmovies.Utils.NetworkUtils;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ListItemClickListener{

    private MoviesAdapter moviesAdapter;
    private MainActivityViewModel mainActivityViewModel;

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

        // Initialize the ViewModel and observe Recycler View's MoviesCollection LiveData
        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        // OBSERVE THE RECYCLE VIEW
        mainActivityViewModel.getMoviesCollection().observe(this, new Observer<MoviesCollection>() {
            @Override
            public void onChanged(@Nullable MoviesCollection moviesCollection) {
                moviesAdapter.setCollectionData(moviesCollection);
            }
        });





        // OBSERVE THE MENU CHECKBOXES
        mainActivityViewModel.getPopular().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(final Boolean isPopular) {
                if (isPopular){
                    setTitle(getString(R.string.main_most_popular_actionbar));
                }
                else{
                    setTitle(getString(R.string.main_most_voted_actionbar));
                }

                if (menu != null) {
                    if (isPopular) {
                        menu.findItem(R.id.action_popular).setChecked(true);
                        menu.findItem(R.id.action_voted).setChecked(false);
                    } else {
                        menu.findItem(R.id.action_popular).setChecked(false);
                        menu.findItem(R.id.action_voted).setChecked(true);
                    }
                }
            }
        });


        if (getString(R.string.network_api_key).equals("API KEY HERE")){
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setMessage(R.string.main_no_api_key)
                    .setPositiveButton(R.string.main_no_network_close, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            // Create the AlertDialog object and return it
            dialogBuilder.create().show();
        } else

        if (mainActivityViewModel.isFirst()) {
            if (NetworkUtils.isNetworkAvailable(this)) {
                mainActivityViewModel.makeMoviesQuery(true);
            } else {
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
    }

    private Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        this.menu=menu;

        if (mainActivityViewModel.getPopular().getValue()) {
            menu.findItem(R.id.action_popular).setChecked(true);
            menu.findItem(R.id.action_voted).setChecked(false);
        } else {
            menu.findItem(R.id.action_popular).setChecked(false);
            menu.findItem(R.id.action_voted).setChecked(true);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_popular) {
            //item.setChecked(true);
            //menu.findItem(R.id.action_voted).setChecked(false);
            mainActivityViewModel.makeMoviesQuery(true);
            return true;
        }
        else if (id == R.id.action_voted){
            //item.setChecked(true);
            //menu.findItem(R.id.action_popular).setChecked(false);
            mainActivityViewModel.makeMoviesQuery(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        //MoviesCollection.Movie movie = moviesAdapter.getMovieAtIndex(clickedItemIndex);
        //Toast.makeText(this, movie.getTitle(), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("movie",  moviesAdapter.getMovieAtIndex(clickedItemIndex));
        startActivity(intent);
    }
}
