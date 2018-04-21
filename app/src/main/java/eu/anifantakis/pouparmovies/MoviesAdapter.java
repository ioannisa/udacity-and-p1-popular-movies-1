package eu.anifantakis.pouparmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import eu.anifantakis.pouparmovies.Utils.MoviesCollection;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>{
    private MoviesCollection collection;

    final private ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public MoviesAdapter(ListItemClickListener clickListener) {
        this.mOnClickListener = clickListener;
    }

    public MoviesCollection.Movie getMovieAtIndex(int index){
        return collection.getMovie(index);
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_row, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.setTitle(collection.getMovie(position).getTitle());
        holder.setImage(collection.getMovie(position).getBackdropPath());
        holder.setRating(collection.getMovie(position).getRating(1));
    }

    @Override
    public int getItemCount() {
        if (null == collection) return 0;
        return collection.getCollectionSize();
    }

    protected class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mMovieTitle;
        private ImageView mMovieThumb;
        private Context context;
        private RatingBar ratingBar;

        private MovieViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            mMovieTitle = (TextView) itemView.findViewById(R.id.row_tv_title);
            mMovieThumb = (ImageView) itemView.findViewById(R.id.row_iv_movie_thumb);
            ratingBar = (RatingBar) itemView.findViewById(R.id.row_rating_bar);
            itemView.setOnClickListener(this);
        }

        /**
         * Set the holder movie's thumbnail
         * @param image
         */
        void setImage(String image){

            String posterPath = context.getString(R.string.network_url_images) + context.getString(R.string.network_width_342);
            Picasso.with(context)
                    .load(posterPath+image)
                    .into(mMovieThumb);
        }

        /**
         * Set the holder movie's rating
         * @param rating
         */
        void setRating(float rating) {
            ratingBar.setRating(rating);
        }

        /**
         * Set the holer movie's title
         * @param title
         */
        void setTitle(String title){
            mMovieTitle.setText(title);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    void setCollectionData(MoviesCollection collection){
        this.collection = collection;
        notifyDataSetChanged();
    }
}
