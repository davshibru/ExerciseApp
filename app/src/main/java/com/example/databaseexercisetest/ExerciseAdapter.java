package com.example.databaseexercisetest;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private List<ExerciseModel> list;
    private Listener listener;
    private LongListener longListener;

    public interface Listener{
        void onClick(int id, int position);
    }

    public interface LongListener{
        void onClick(int id, int position);
    }

    public ExerciseAdapter(List<ExerciseModel> list) {
        this.list = list;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
    public void setLongListener(LongListener longListener) {this.longListener = longListener; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_for_exercise_recycler_view, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;

        TextView name, description;
        name = (TextView) cardView.findViewById(R.id.nameOfExercise);
        description = (TextView) cardView.findViewById(R.id.descriptionOfExercise);
        name.setText(list.get(position).getName());
        description.setText(list.get(position).getDescription());

        ImageView imageView = (ImageView) cardView.findViewById(R.id.imageOfExercise);
        Bitmap bitmap = Utils.getImage(list.get(position).getImage());
        imageView.setImageBitmap(bitmap);
        imageView.setContentDescription(list.get(position).getName());

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onClick(list.get(position).getId(), position);
                }
            }
        });

        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longListener != null){
                    longListener.onClick(list.get(position).getId(), position);
                }
                return true;
            }
        });




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;

        public ViewHolder(@NonNull CardView itemView) {
            super(itemView);

            cardView = itemView;
        }
    }
}
