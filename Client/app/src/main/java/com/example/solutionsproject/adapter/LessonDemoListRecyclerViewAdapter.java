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
import com.example.solutionsproject.model.gson.data.LessonGson;

import java.util.List;

import lombok.Getter;

public class LessonDemoListRecyclerViewAdapter extends RecyclerView.Adapter<LessonDemoListRecyclerViewAdapter.ViewHolder>{
    private final Context context;
    private final List<LessonGson> lessonGsonList;

    public LessonDemoListRecyclerViewAdapter(Context context, List<LessonGson> lessonGsonList) {
        this.context = context;
        this.lessonGsonList = lessonGsonList;
    }

    @NonNull
    @Override
    public LessonDemoListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_lesson_list, parent, false);
        return new LessonDemoListRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonDemoListRecyclerViewAdapter.ViewHolder holder, int position) {
        LessonGson model = lessonGsonList.get(position);
        String lessonNum = Integer.parseInt(model.getLessonNumber()) < 10 ? "0" + model.getLessonNumber() : model.getLessonNumber();
        holder.txtNumber.setText(lessonNum);
        holder.txtTitle.setText(model.getTitle());
    }

    @Override
    public int getItemCount() {
        return lessonGsonList.size();
    }

    @Getter
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtNumber;
        private final TextView txtTitle;
        private final ImageButton btnOpen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNumber = itemView.findViewById(R.id.lesson_list_number);
            txtTitle = itemView.findViewById(R.id.lesson_list_txt_title);
            btnOpen = itemView.findViewById(R.id.lesson_list_btn_open);
            btnOpen.setVisibility(View.GONE);
        }
    }
}
