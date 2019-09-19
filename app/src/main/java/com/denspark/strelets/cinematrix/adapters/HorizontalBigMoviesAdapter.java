package com.denspark.strelets.cinematrix.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.denspark.strelets.cinematrix.R;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.glide.GlideApp;
import com.denspark.strelets.cinematrix.utils.DimensionUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.SupportRSBlurTransformation;

public class HorizontalBigMoviesAdapter extends PagedListAdapter<FilmixMovie, HorizontalBigMoviesAdapter.MovieHolder> {

    private HorizontalAdapterListener listener;

    public class MovieHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.movie_title_upd_big_tv)
        TextView titleTextView;
        @BindView(R.id.movie_duration_upd_big_tv)
        TextView durationTextView;
        @BindView(R.id.movie_year_upd_big_tv)
        TextView yearTextView;
        @BindView(R.id.pos_rating_value_upd_big)
        TextView posTextView;
        @BindView(R.id.neg_rating_value_upd_big)
        TextView negTextView;
        @BindView(R.id.blured_poster_upd_big_image_view)
        ImageView bluredPosterImageView;
        @BindView(R.id.poster_upd_big_image_view)
        ImageView posterImageView;

        public MovieHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onMovieClick(getMovie(position));
                    }
                }
            });
        }
    }

    public HorizontalBigMoviesAdapter(HorizontalAdapterListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
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
//                    return oldItem.getId() == (newItem.getId());
                }
            };

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_upd_big, parent, false);
        return new HorizontalBigMoviesAdapter.MovieHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
        FilmixMovie currentMovie = getItem(position);
        if (currentMovie != null) {


            Context context = holder.bluredPosterImageView.getContext();

            holder.titleTextView.setText(currentMovie.getName());
            holder.yearTextView.setText(currentMovie.getYear());
            holder.durationTextView.setText(currentMovie.getDuration());

            holder.posTextView.setText(" +"+ currentMovie.getPosRating());
            holder.negTextView.setText(" -"+ currentMovie.getNegRating());

            MultiTransformation<Bitmap> bitmapMultiTransformation =
                    new MultiTransformation<>(
                            new CenterCrop(),
                            new SupportRSBlurTransformation(10, 1),
                            new RoundedCorners(DimensionUtils.dpToPx(context, 2))

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
                    .transform(new RoundedCorners(DimensionUtils.dpToPx(context, 4)))
                    .override(340, 433)
                    .into(holder.posterImageView);

        }
    }

    private FilmixMovie getMovie(int position) {
        return getItem(position);
    }

}
