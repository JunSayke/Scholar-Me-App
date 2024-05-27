package com.example.solutionsproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solutionsproject.R;
import com.example.solutionsproject.model.gson.data.FlashcardChoiceGson;

import java.util.List;

import lombok.Getter;

public class FlashcardChoiceListRecyclerViewAdapter extends RecyclerView.Adapter<FlashcardChoiceListRecyclerViewAdapter.ViewHolder>{

    private final Context context;
    private final List<FlashcardChoiceGson> flashcardChoiceGsonList;

    public FlashcardChoiceListRecyclerViewAdapter(Context context, List<FlashcardChoiceGson> flashcardChoiceGsonList) {
        this.context = context;
        this.flashcardChoiceGsonList = flashcardChoiceGsonList;
    }

    @NonNull
    @Override
    public FlashcardChoiceListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_flashcard_choices, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardChoiceListRecyclerViewAdapter.ViewHolder holder, int position) {
        FlashcardChoiceGson model = flashcardChoiceGsonList.get(position);
        holder.txtChoice.setText(model.getChoice());
//        holder.cbIsCorrect
    }

    @Override
    public int getItemCount() {
        return flashcardChoiceGsonList.size();
    }

    @Getter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView cbIsCorrect;
        private final TextView txtChoice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbIsCorrect = itemView.findViewById(R.id.flashcard_choice_iv_iscorrect);
            txtChoice = itemView.findViewById(R.id.flashcard_choice_txt_choice);
        }
    }
}
