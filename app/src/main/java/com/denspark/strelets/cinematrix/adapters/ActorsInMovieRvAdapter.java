package com.denspark.strelets.cinematrix.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.denspark.strelets.cinematrix.R;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.database.entity.Person;
import com.denspark.strelets.cinematrix.glide.GlideApp;

public class ActorsInMovieRvAdapter extends ListAdapter<Person, ActorsInMovieRvAdapter.PersonHolder> {
    private  ActorClickListener actorClickListener;
    private static final DiffUtil.ItemCallback<Person> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Person>() {
                @Override
                public boolean areItemsTheSame(@NonNull Person oldItem, @NonNull Person newItem) {
                    return oldItem.getId() == (newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Person oldItem, @NonNull Person newItem) {
                    if (oldItem.getName() != null
                            &&
                            newItem.getName() != null
                    ) {

                        return oldItem.getName().equals(newItem.getName());
//                                &&
//                                oldItem.getPhotoUrl().equals(newItem.getPhotoUrl());
                    }
                    return false;
                }
            };

    public ActorsInMovieRvAdapter(ActorClickListener actorClickListener) {
        super(DIFF_CALLBACK);
        this.actorClickListener = actorClickListener;
    }

    class PersonHolder extends RecyclerView.ViewHolder {
        private TextView actorName;
        private ImageView actorPhoto;

        public PersonHolder(View personItem) {
            super(personItem);
            actorName = personItem.findViewById(R.id.actor_name_tv);
            actorPhoto = personItem.findViewById(R.id.actor_photo_iv);

            personItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (actorClickListener != null && position != RecyclerView.NO_POSITION) {
                        actorClickListener.onActorClick(getActor(position));
                    }
                }
            });
        }

    }

    public Person getActor(int position) {
        return getItem(position);
    }

    public interface ActorClickListener {
        void onActorClick(Person actor);
    }

    @NonNull
    @Override
    public PersonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actor, parent, false);
        return new PersonHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonHolder holder, int position) {
        Person currentPerson = getItem(position);
        Context context = holder.actorPhoto.getContext();

        holder.actorName.setText(currentPerson.getName());

        MultiTransformation<Bitmap> bitmapMultiTransformation =
                new MultiTransformation<>(
                        new CircleCrop()
                );

        GlideApp.with(context)
                .asBitmap()
                .fallback(R.drawable.empty_actor_photo)
                .load(currentPerson.getPhotoUrl())
                .transform(bitmapMultiTransformation)
                .into(holder.actorPhoto);

    }
}
