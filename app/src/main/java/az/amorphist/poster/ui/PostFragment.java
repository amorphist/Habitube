package az.amorphist.poster.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import az.amorphist.poster.R;
import az.amorphist.poster.adapters.MovieAdapter;
import az.amorphist.poster.adapters.SeasonAdapter;
import az.amorphist.poster.adapters.ShowAdapter;
import az.amorphist.poster.di.modules.MovieModule;
import az.amorphist.poster.di.modules.SearchModule;
import az.amorphist.poster.entities.movie.MovieGenre;
import az.amorphist.poster.entities.movielite.MovieLite;
import az.amorphist.poster.entities.show.Season;
import az.amorphist.poster.entities.show.Show;
import az.amorphist.poster.entities.show.ShowGenre;
import az.amorphist.poster.presentation.post.PostPresenter;
import az.amorphist.poster.presentation.post.PostView;
import moxy.MvpAppCompatFragment;
import moxy.presenter.InjectPresenter;
import moxy.presenter.ProvidePresenter;
import toothpick.Scope;
import toothpick.Toothpick;

import static az.amorphist.poster.App.IMAGE_URL;
import static az.amorphist.poster.di.DI.APP_SCOPE;
import static az.amorphist.poster.di.DI.POST_SCOPE;

public class PostFragment extends MvpAppCompatFragment implements PostView {

    @InjectPresenter PostPresenter postPresenter;

    private Toolbar toolbar;
    private RecyclerView recyclerViewSimilarMovies, recyclerViewSimilarShows, recyclerViewSeasons;
    private RelativeLayout mainScreen, showScreen, personScreen;
    private LinearLayout loadingScreen, errorScreen;
    private ImageView posterBackground, posterMain, posterShow, posterShowBackground, posterPerson;
    private TextView posterTitle, posterDate, posterRate, posterViews, posterDesc;
    private TextView posterShowTitle, posterShowDate, posterShowRate, posterShowViews, posterShowDesc;
    private TextView posterPersonName, posterPersonBirthDate, posterPersonLocation, posterPersonPopularity, posterPersonBio;
    private ChipGroup movieGenresChip, showGenresChip;
    private MovieAdapter similarMoviesAdapter;
    private ShowAdapter similarShowsAdapter;
    private SeasonAdapter seasonAdapter;

    @ProvidePresenter
    PostPresenter postPresenter() {
        final Bundle movieBundle = getArguments();
        final Integer postPosition = movieBundle.getInt("postPosition");
        final Integer showPosition = movieBundle.getInt("showPosition");
        final Integer upcomingPosition = movieBundle.getInt("upcomingPosition");

        final Bundle searchBundle = getArguments();
        final Integer postId = searchBundle.getInt("postId");
        final Integer mediaType = searchBundle.getInt("mediaType");

        final Scope temporaryPostScope = Toothpick.openScopes(APP_SCOPE, POST_SCOPE);
        temporaryPostScope.installModules(new MovieModule(postPosition, showPosition, upcomingPosition));
        temporaryPostScope.installModules(new SearchModule(postId, mediaType));
        final PostPresenter postPresenter = temporaryPostScope.getInstance(PostPresenter.class);
        Toothpick.closeScope(POST_SCOPE);
        return postPresenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        similarMoviesAdapter = new MovieAdapter(postId -> postPresenter.goToDetailedMovieScreen(postId));
        similarShowsAdapter = new ShowAdapter(showId -> postPresenter.goToDetailedShowScreen(showId));
        seasonAdapter = new SeasonAdapter(position -> showBottomSeasonDialog(position));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        toolbar = view.findViewById(R.id.post_toolbar);

        initPersonItems(view);
        initMovieItems(view);
        initShowItems(view);
        initScreens(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        toolbar.setNavigationOnClickListener(v -> postPresenter.goBack());

        LinearLayoutManager layoutManagerSimilarMovies = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSimilarMovies.setLayoutManager(layoutManagerSimilarMovies);
        recyclerViewSimilarMovies.setItemAnimator(new DefaultItemAnimator());
        recyclerViewSimilarMovies.setHasFixedSize(true);
        recyclerViewSimilarMovies.setAdapter(similarMoviesAdapter);

        LinearLayoutManager layoutManagerSimilarShows = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSimilarShows.setLayoutManager(layoutManagerSimilarShows);
        recyclerViewSimilarShows.setItemAnimator(new DefaultItemAnimator());
        recyclerViewSimilarShows.setHasFixedSize(true);
        recyclerViewSimilarShows.setAdapter(similarShowsAdapter);

        LinearLayoutManager layoutManagerSeasons = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSeasons.setLayoutManager(layoutManagerSeasons);
        recyclerViewSeasons.setItemAnimator(new DefaultItemAnimator());
        recyclerViewSeasons.setHasFixedSize(true);
        recyclerViewSeasons.setAdapter(seasonAdapter);
    }

    @Override
    public void getMovie(String image, String background, String title, String date, double rate, int views, List<MovieGenre> movieGenres, String description) {
        Glide.with(getContext()).load(IMAGE_URL + image)
                .transition(new DrawableTransitionOptions().crossFade())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.ic_poster_name)
                .transform(new CenterCrop(), new RoundedCorners(16))
                .into(posterMain);
        Glide.with(getContext()).load(IMAGE_URL + background)
                .transition(new DrawableTransitionOptions().crossFade())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.color.colorPrimary)
                .error(R.drawable.ic_poster_name)
                .into(posterBackground);
        posterTitle.setText(title);
        posterDate.setText(date);
        posterRate.setText(String.valueOf(rate));
        posterViews.setText(String.valueOf(views));
        for(MovieGenre mGenres: movieGenres) {
            Chip chip = new Chip(getContext());
            ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(getContext(), null, 0, R.style.Widget_MaterialComponents_Chip_Action);
            chip.setChipDrawable(chipDrawable);
            chip.setText(mGenres.getName());
            movieGenresChip.addView(chip);
        }
        posterDesc.setText(description);
    }

    @Override
    public void getShow(String image, String background, String title, String date,
                        float rate, float views, List<ShowGenre> showGenres,
                        String description, List<Season> seasons) {
        Glide.with(getContext()).load(IMAGE_URL + image)
                .transition(new DrawableTransitionOptions().crossFade())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.ic_poster_name)
                .transform(new CenterCrop(), new RoundedCorners(16))
                .into(posterShow);
        Glide.with(getContext()).load(IMAGE_URL + background)
                .transition(new DrawableTransitionOptions().crossFade())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.color.colorPrimary)
                .error(R.drawable.ic_poster_name)
                .into(posterShowBackground);
        posterShowTitle.setText(title);
        posterShowDate.setText(date);
        posterShowRate.setText(String.valueOf(rate));
        posterShowViews.setText(String.valueOf(views));
        for(ShowGenre sGenres: showGenres) {
            Chip chip = new Chip(getContext());
            ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(getContext(), null, 0, R.style.Widget_MaterialComponents_Chip_Action);
            chip.setChipDrawable(chipDrawable);
            chip.setText(sGenres.getName());
            showGenresChip.addView(chip);
        }
        posterShowDesc.setText(description);
        seasonAdapter.addAllMovies(seasons);
    }

    @Override
    public void getPerson(String image, String name, String birthdate, String placeOfBirth, double popularity, String bio) {
        Glide.with(getContext())
                .load(IMAGE_URL + image)
                .transition(new DrawableTransitionOptions().crossFade())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.ic_poster_name)
                .transform(new CenterCrop(), new RoundedCorners(16))
                .into(posterPerson);
        posterPersonName.setText(name);
        posterPersonBirthDate.setText(birthdate);
        posterPersonLocation.setText(placeOfBirth);
        posterPersonPopularity.setText(String.valueOf(popularity));
        posterPersonBio.setText(bio);
    }

    @Override
    public void showSimilarMovieList(List<MovieLite> similarMovies) {
        similarMoviesAdapter.addAllMovies(similarMovies);
    }

    @Override
    public void showSimilarTVShowList(List<MovieLite> similarShows) {
        similarShowsAdapter.addAllMovies(similarShows);
    }

    @Override
    public void showProgress(boolean loadingState) {
        if(loadingState){
            loadingScreen.setVisibility(View.VISIBLE);
        } else {
            loadingScreen.setVisibility(View.GONE);
        }
    }

    @Override
    public void showMovieScreen() {
        mainScreen.setVisibility(View.VISIBLE);
        errorScreen.setVisibility(View.GONE);
        showScreen.setVisibility(View.GONE);
        personScreen.setVisibility(View.GONE);
    }

    @Override
    public void showErrorScreen() {
        mainScreen.setVisibility(View.GONE);
        errorScreen.setVisibility(View.VISIBLE);
        showScreen.setVisibility(View.GONE);
        personScreen.setVisibility(View.GONE);
    }

    @Override
    public void showTVShowScreen() {
        mainScreen.setVisibility(View.GONE);
        errorScreen.setVisibility(View.GONE);
        showScreen.setVisibility(View.VISIBLE);
        personScreen.setVisibility(View.GONE);
    }

    @Override
    public void showPersonScreen() {
        mainScreen.setVisibility(View.GONE);
        errorScreen.setVisibility(View.GONE);
        showScreen.setVisibility(View.GONE);
        personScreen.setVisibility(View.VISIBLE);
    }

    @Override
    public void showBottomSeasonDialog(int position) {
        SeasonBottomDialog seasonBottomDialog = new SeasonBottomDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("SEASON", seasonAdapter.getSeason(position));
        seasonBottomDialog.setArguments(bundle);
        seasonBottomDialog.show(getChildFragmentManager(), null);
    }

    private void initPersonItems(View view) {
        posterPerson = view.findViewById(R.id.poster_person_post);
        posterPersonName = view.findViewById(R.id.poster_person_title);
        posterPersonBirthDate = view.findViewById(R.id.poster_person_birthdate);
        posterPersonLocation = view.findViewById(R.id.poster_person_location);
        posterPersonPopularity = view.findViewById(R.id.poster_person_popularity);
        posterPersonBio = view.findViewById(R.id.poster_person_bio);
    }

    private void initMovieItems(View view) {
        posterBackground = view.findViewById(R.id.poster_background);
        posterMain = view.findViewById(R.id.poster_movie_post);
        posterTitle = view.findViewById(R.id.poster_title);
        posterDate = view.findViewById(R.id.poster_date);
        posterRate = view.findViewById(R.id.poster_rate);
        posterViews = view.findViewById(R.id.poster_views);
        movieGenresChip = view.findViewById(R.id.movie_genres);
        posterDesc = view.findViewById(R.id.poster_desc);
        recyclerViewSimilarMovies = view.findViewById(R.id.recycler_view_similar_movies);
    }

    private void initShowItems(View view) {
        posterShowBackground = view.findViewById(R.id.show_poster_background);
        posterShow = view.findViewById(R.id.poster_show_post);
        posterShowTitle = view.findViewById(R.id.poster_show_title);
        posterShowDate = view.findViewById(R.id.poster_show_date);
        posterShowRate = view.findViewById(R.id.poster_show_rate);
        posterShowViews = view.findViewById(R.id.poster_show_views);
        showGenresChip = view.findViewById(R.id.show_genres);
        posterShowDesc = view.findViewById(R.id.poster_show_desc);
        recyclerViewSeasons = view.findViewById(R.id.recycler_view_seasons);
        recyclerViewSimilarShows = view.findViewById(R.id.recycler_view_similar_shows);
    }

    private void initScreens(View view) {
        loadingScreen = view.findViewById(R.id.loading_screen);
        mainScreen = view.findViewById(R.id.main_screen);
        errorScreen = view.findViewById(R.id.error_screen);
        showScreen = view.findViewById(R.id.show_screen);
        personScreen = view.findViewById(R.id.person_screen);
    }
}
