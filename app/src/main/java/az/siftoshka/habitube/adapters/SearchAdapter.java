package az.siftoshka.habitube.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import az.siftoshka.habitube.R;
import az.siftoshka.habitube.entities.movielite.MovieLite;
import az.siftoshka.habitube.utils.DateChanger;
import az.siftoshka.habitube.utils.ImageLoader;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchHolder> {

    public interface SearchItemClickListener {
        void onPostClicked(int id, int mediaType);
    }

    private List<MovieLite> searchMedia;
    private SearchItemClickListener clickListener;
    private DateChanger dateChanger;
    private int mediaState;

    public SearchAdapter(@NonNull SearchItemClickListener clickListener) {
        this.searchMedia = new ArrayList<>();
        this.dateChanger = new DateChanger();
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_search, parent, false);
        return new SearchHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHolder holder, final int position) {
        final MovieLite post = this.searchMedia.get(position);

        if (post.getMovieImage() == null) {
            ImageLoader.load(holder.itemView, post.getStarImage(), holder.posterImage);
        } else {
            ImageLoader.load(holder.itemView, post.getMovieImage(), holder.posterImage);
        }

        if (post.getMovieTitle() == null) {
            holder.posterTitle.setText(post.getShowTitle());
        } else {
            holder.posterTitle.setText(post.getMovieTitle());
        }

        if (post.getReleaseDate() == null) {
            holder.posterDate.setText(dateChanger.changeDate(post.getFirstAirDate()));
        } else {
            holder.posterDate.setText(dateChanger.changeDate(post.getReleaseDate()));
        }

        holder.posterLayout.setOnClickListener(v -> {
            switch (post.getMediaType()) {
                case "movie":
                    mediaState = 1;
                    break;
                case "tv":
                    mediaState = 2;
                    break;
                case "person":
                    mediaState = 3;
                    break;
            }
            clickListener.onPostClicked(post.getMovieId(), mediaState);
        });

    }

    @Override
    public void onViewRecycled(@NonNull SearchHolder holder) {
        holder.posterTitle.setText(null);
        holder.posterDate.setText(null);
        holder.posterLayout.setOnClickListener(null);
    }

    @Override
    public int getItemCount() {
        return searchMedia == null ? 0 : searchMedia.size();
    }

    public void addAllMedia(List<MovieLite> searchMedia) {
        this.searchMedia.clear();
        this.searchMedia.addAll(searchMedia);
        notifyDataSetChanged();
    }

    public void clean() {
        this.searchMedia.clear();
        notifyDataSetChanged();
    }

    static class SearchHolder extends RecyclerView.ViewHolder {

        LinearLayout posterLayout;
        ImageView posterImage;
        TextView posterTitle, posterDate;

        SearchHolder(@NonNull View itemView) {
            super(itemView);
            this.posterLayout = itemView.findViewById(R.id.item_layout_search);
            this.posterImage = itemView.findViewById(R.id.poster_image_search);
            this.posterTitle = itemView.findViewById(R.id.poster_main_text_search);
            this.posterDate = itemView.findViewById(R.id.poster_main_date_search);
        }
    }
}