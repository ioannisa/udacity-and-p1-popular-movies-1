package eu.anifantakis.pouparmovies.Utils;

import android.annotation.SuppressLint;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by ioannisa on 25/2/2018.
 */

public final class PopularMoviesJSonUtils {
    @SuppressLint("SimpleDateFormat")
    public static MoviesCollection parseMoviesJson(String json) {
        MoviesCollection collection = null;
        try{
            // retrieve the root
            JSONObject moviesJson = new JSONObject(json);

            collection = new MoviesCollection();

            JSONArray moviesJsonJSONArray = moviesJson.getJSONArray("results");
            for (int i = 0; i < moviesJsonJSONArray.length(); i++) {
                JSONObject movie = (JSONObject) moviesJsonJSONArray.get(i);

                collection.addMovie(
                        new MoviesCollection.Movie(
                                movie.optInt("vote_count"),
                                movie.optInt("id"),
                                movie.optBoolean("video"),
                                movie.optDouble("vote_average"),
                                movie.optString("title"),
                                movie.optDouble("popularity"),
                                movie.optString("poster_path"),
                                movie.optString("original_language"),
                                movie.optString("original_title"),
                                null,
                                movie.optString("backdrop_path"),
                                movie.optBoolean("adult"),
                                movie.optString("overview"),
                                new SimpleDateFormat("yyyy-mm-dd").parse(movie.optString("release_date"))
                        )
                );
            }

            return collection;
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
            // if some error occurred, return null
            return null;
        }
    }
}
