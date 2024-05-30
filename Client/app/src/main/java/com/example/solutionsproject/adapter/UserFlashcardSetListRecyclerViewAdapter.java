package com.example.solutionsproject.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solutionsproject.R;
import com.example.solutionsproject.model.gson.data.CommentGson;
import com.example.solutionsproject.model.gson.data.FlashcardSetGson;
import com.example.solutionsproject.model.gson.data.FlashcardSetGson;

import java.util.List;

import lombok.Getter;

public class UserFlashcardSetListRecyclerViewAdapter extends RecyclerView.Adapter<UserFlashcardSetListRecyclerViewAdapter.ViewHolder>{

    private final Context context;
    private final List<FlashcardSetGson> flashcardSetGsonList;
    private final UserFlashcardSetListRecyclerViewAdapter.OnItemClickListener onItemClickListener;

    public UserFlashcardSetListRecyclerViewAdapter(Context context, List<FlashcardSetGson> flashcardSetGsonList, UserFlashcardSetListRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        this.context = context;
        this.flashcardSetGsonList = flashcardSetGsonList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public UserFlashcardSetListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_flashcardset, parent, false);
        return new UserFlashcardSetListRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserFlashcardSetListRecyclerViewAdapter.ViewHolder holder, int position) {
        FlashcardSetGson model = flashcardSetGsonList.get(position);
        holder.txtTitle.setText(model.getTitle());
        holder.txtDescription.setText(model.getDescription());
    }

    @Override
    public int getItemCount() {
        return flashcardSetGsonList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(FlashcardSetGson item);
    }

    @Getter
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtTitle;
        private final TextView txtDescription;
        private final ImageButton btnOpen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.flashcardset_list_txt_title);
            txtDescription = itemView.findViewById(R.id.flashcardset_list_txt_desc);
            btnOpen = itemView.findViewById(R.id.flashcardset_list_btn_open);
            btnOpen.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    onItemClickListener.onItemClick(flashcardSetGsonList.get(position));
                }
            });
        }
    }
}
