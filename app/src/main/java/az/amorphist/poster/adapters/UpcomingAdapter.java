package az.amorphist.poster.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import az.amorphist.poster.R;
import az.amorphist.poster.entities.Movie;
import az.amorphist.poster.entities.MovieLite;

public class UpcomingAdapter extends RecyclerView.Adapter<UpcomingAdapter.UpcomingHolder> {

    public interface UpcomingItemClickListener {
        void onPostClicked(int position);
    }

    private List<MovieLite> movies;
    private UpcomingItemClickListener clickListener;

    public UpcomingAdapter(@NonNull UpcomingItemClickListener clickListener) {
        this.movies = new ArrayList<>();
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public UpcomingHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_post, viewGroup, false);
        return new UpcomingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingHolder holder, final int position) {
        final MovieLite movie = this.movies.get(position);
        Picasso.get().load("https://image.tmdb.org/t/p/original" + movie.getMovieImage())
                .resize(200,300)
                .placeholder(R.drawable.progress_animation)
                .into(holder.posterImage);
        holder.posterTitle.setText(movie.getMovieTitle());
        holder.posterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onPostClicked(position);
            }
        });

    }

    @Override
    public void onViewRecycled(@NonNull UpcomingHolder holder) {
        holder.posterTitle.setText(null);
        holder.posterImage.setOnClickListener(null);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void addAllMovies(List<MovieLite> movies) {
        this.movies.clear();
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    static class UpcomingHolder extends RecyclerView.ViewHolder {

        ImageView posterImage;
        TextView posterTitle;

        UpcomingHolder(@NonNull View itemView) {
            super(itemView);
            this.posterImage = itemView.findViewById(R.id.poster_image);
            this.posterTitle = itemView.findViewById(R.id.poster_main_text);
        }
    }
}
