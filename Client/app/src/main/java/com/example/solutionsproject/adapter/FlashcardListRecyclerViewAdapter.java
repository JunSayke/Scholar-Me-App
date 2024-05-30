package com.example.solutionsproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solutionsproject.R;
import com.example.solutionsproject.model.gson.data.FlashcardGson;

import java.util.List;

import lombok.Getter;

public class FlashcardListRecyclerViewAdapter extends RecyclerView.Adapter<FlashcardListRecyclerViewAdapter.ViewHolder>{

    private final Context context;
    private final List<FlashcardGson> flashcardGsonList;
    private final FlashcardListRecyclerViewAdapter.OnItemClickListener onItemClickListener;

    public FlashcardListRecyclerViewAdapter(Context context, List<FlashcardGson> flashcardGsonList, FlashcardListRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        this.context = context;
        this.flashcardGsonList = flashcardGsonList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public FlashcardListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_flashcard_questions, parent, false);
        return new FlashcardListRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardListRecyclerViewAdapter.ViewHolder holder, int position) {
        FlashcardGson model = flashcardGsonList.get(position);
        holder.txtFlashcardNumber.setText(String.valueOf(position + 1));
        holder.txtFlashcardQuestion.setText(model.getQuestion());
    }

    @Override
    public int getItemCount() {
        return flashcardGsonList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(FlashcardGson item);
    }

    @Getter
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtFlashcardNumber;
        private final TextView txtFlashcardQuestion;
        private final ImageButton btnOpen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFlashcardNumber = itemView.findViewById(R.id.flashcard_question_number);
            txtFlashcardQuestion = itemView.findViewById(R.id.flashcard_question_txt_question);
            btnOpen = itemView.findViewById(R.id.flashcard_question_btn_open);
            btnOpen.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    onItemClickListener.onItemClick(flashcardGsonList.get(position));
                }
            });
        }
    }
}
