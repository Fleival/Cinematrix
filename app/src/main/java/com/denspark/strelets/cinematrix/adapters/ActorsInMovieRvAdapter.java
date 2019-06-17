package com.denspark.strelets.cinematrix.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.denspark.strelets.cinematrix.R;
import com.denspark.strelets.cinematrix.database.entity.Person;
import com.denspark.strelets.cinematrix.glide.GlideApp;

public class ActorsInMovieRvAdapter extends ListAdapter<Person, ActorsInMovieRvAdapter.PersonHolder> {
    private static final DiffUtil.ItemCallback<Person> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Person>() {
                @Override public boolean areItemsTheSame(@NonNull Person oldItem, @NonNull Person newItem) {
                    return oldItem.getId() == (newItem.getId());
                }

                @Override public boolean areContentsTheSame(@NonNull Person oldItem, @NonNull Person newItem) {
                    if (oldItem.getName() != null
                            &&
                            newItem.getName() != null
                    ) {

                        return oldItem.getName().equals(newItem.getName())
                                &&
                                oldItem.getPhotoUrl().equals(newItem.getPhotoUrl());
                    }
                    return false;
                }
            };

    public ActorsInMovieRvAdapter() {
        super(DIFF_CALLBACK);
    }

    class PersonHolder extends RecyclerView.ViewHolder {
        private TextView actorName;
        private ImageView actorPhoto;

        public PersonHolder(View personItem) {
            super(personItem);
            actorName = personItem.findViewById(R.id.actor_name_tv);
            actorPhoto = personItem.findViewById(R.id.actor_photo_iv);
        }
    }

    @NonNull @Override public PersonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actor, parent, false);
        return new PersonHolder(itemView);
    }

    @Override public void onBindViewHolder(@NonNull PersonHolder holder, int position) {
        Person currentPerson = getItem(position);
        Context context = holder.actorPhoto.getContext();

        holder.actorName.setText(currentPerson.getName());

        GlideApp.with(context)
                .asBitmap()
                .load(currentPerson.getPhotoUrl())
                .into(holder.actorPhoto);

    }
}
