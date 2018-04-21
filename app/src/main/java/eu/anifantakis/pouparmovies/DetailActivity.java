package eu.anifantakis.pouparmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import eu.anifantakis.pouparmovies.Utils.MoviesCollection;

public class DetailActivity extends AppCompatActivity {

    private MoviesCollection.Movie movie = null;
    private ImageView ivMovieHoriz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ivMovieHoriz = (ImageView) findViewById(R.id.detail_iv_img_horiz);
        TextView title = (TextView) findViewById(R.id.detail_tv_title);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.detail_rb_movie_rating);
        TextView tvRating = (TextView) findViewById(R.id.detail_tv_movie_rating);
        TextView tvReleaseDate = (TextView) findViewById(R.id.detail_tv_release_date);
        TextView tvSynopsis = (TextView) findViewById(R.id.detail_tv_synopsis);

        // Receive the Parcelable Movie object from the extras of the intent.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("movie")) {
                movie = getIntent().getParcelableExtra("movie");
            }
        }

        // Set ActionBar title to that of the movie title.
        setTitle(movie.getTitle());
        title.setText(movie.getTitle());

        // setup the rating
        ratingBar.setRating(movie.getRating(1));
        tvRating.setText(getString(R.string.detail_rating_text, movie.getRating(1)));

        // Get the date
        tvReleaseDate.setText(getString(R.string.detail_released, getLocalizedDateStr(movie.getReleaseDate())));

        // Get the synopsis (overview)
        tvSynopsis.setText(movie.getOverview());

        // then load the large image
        setImage(movie.getBackdropPath());
    }

    /**
     * Gets a date, and returns a localized date format string with 4 digits on the year.
     * @param theDate
     * @return The String with the localized date with 4 year digits
     */
    private String getLocalizedDateStr(Date theDate){
        // get local date format
        DateFormat df = android.text.format.DateFormat.getDateFormat(this);
        String pattern = ((SimpleDateFormat) df).toPattern();

        // local date year format is always using 2 digits - we transform year to 4 digits
        pattern = pattern.replaceAll("yy", "yyyy");
        ((SimpleDateFormat) df).applyLocalizedPattern(pattern);

        return df.format(theDate);
    }

    /**
     * Takes the image id and applies it to the tmdb.org URL to draw it using
     * the Picasso library on the ImageView
     * @param image The image id
     */
    void setImage(String image){
        // http://image.tmdb.org/t/p/w780/lkOZcsXcOLZYeJ2YxJd3vSldvU4.jpg
        String posterPath = getString(R.string.network_url_images) + getString(R.string.network_width_780);
        Picasso.with(this)
                .load(posterPath+image)
                .into(ivMovieHoriz);
    }
}