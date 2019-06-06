package com.denspark.strelets.cinematrix.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.denspark.strelets.cinematrix.R;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.glide.GlideApp;
import com.denspark.strelets.cinematrix.utils.DimensionUtils;
import jp.wasabeef.glide.transformations.SupportRSBlurTransformation;

public class MovieAdapter extends ListAdapter<FilmixMovie, MovieAdapter.MovieHolder> {

    public MovieAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<FilmixMovie> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<FilmixMovie>() {
                @Override
                public boolean areItemsTheSame(@NonNull FilmixMovie oldItem, @NonNull FilmixMovie newItem) {

                    return oldItem.getId() == (newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull FilmixMovie oldItem, @NonNull FilmixMovie newItem) {
                    if (oldItem.getName() != null
                            &&
                            newItem.getName() != null
                            &&
                            oldItem.getDuration() != null
                            &&
                            newItem.getDuration() != null
                    ) {

                        return oldItem.getName().equals(newItem.getName())
                                &&
                                oldItem.getDuration().equals(newItem.getDuration());
                    }
                    return false;
                }
            };


    class MovieHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView genreTextView;
        private TextView viewsTextView;
        private TextView yearTextView;
        private TextView durationTextView;

        private Button addToFavButton;
        private Button ratingButton;

        private ImageView bluredPosterImageView;
        private ImageView posterImageView;


        public MovieHolder(View movieItem) {
            super(movieItem);

            titleTextView = movieItem.findViewById(R.id.movie_title_tv);
            genreTextView = movieItem.findViewById(R.id.movie_genre_tv);
            viewsTextView = movieItem.findViewById(R.id.movie_views_tv);
            yearTextView = movieItem.findViewById(R.id.movie_year_tv);
            durationTextView = movieItem.findViewById(R.id.movie_duration_tv);

            addToFavButton = movieItem.findViewById(R.id.add_to_fav_button);
            ratingButton = movieItem.findViewById(R.id.rating_button);

            bluredPosterImageView = movieItem.findViewById(R.id.blured_poster_image_view);
            posterImageView = movieItem.findViewById(R.id.poster_image_view);

        }
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
        FilmixMovie currentMovie = getItem(position);
        Context context = holder.bluredPosterImageView.getContext();

        holder.titleTextView.setText(currentMovie.getName());
        holder.genreTextView.setText("genre to do");
        holder.viewsTextView.setText("0");
        holder.yearTextView.setText(currentMovie.getYear());
        holder.durationTextView.setText(currentMovie.getDuration());

        MultiTransformation<Bitmap> bitmapMultiTransformation =
                new MultiTransformation<>(
                        new SupportRSBlurTransformation(10, 1),
                        new CenterCrop(),
                        new RoundedCorners(DimensionUtils.dpToPx(context, 3))

                );

        GlideApp.with(context)
                .asBitmap()
                .load(currentMovie.getFilmPosterUrl())
                .placeholder(R.drawable.blur_poster_bg_default)
                .transform(bitmapMultiTransformation)
                .override(155, 232)
                .into(holder.bluredPosterImageView);


        GlideApp.with(context)
                .load(currentMovie.getFilmPosterUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.poster_default)
                .transform(new RoundedCorners(DimensionUtils.dpToPx(context, 7)))
                .override(340, 433)
                .into(holder.posterImageView);

        View.OnClickListener addToFavoriteClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.addToFavButton.setSelected(!holder.addToFavButton.isSelected());
                if (holder.addToFavButton.isSelected()) {
                    Toast.makeText(context, "Movie added to favorite", Toast.LENGTH_SHORT).show();
                } else if (!holder.addToFavButton.isSelected()) {
                    Toast.makeText(context, "Movie removed from favorite", Toast.LENGTH_SHORT).show();
                }
            }
        };

        holder.addToFavButton.setOnClickListener(addToFavoriteClick);
    }
}
