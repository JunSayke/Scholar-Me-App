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

public class LessonListRecyclerViewAdapter extends RecyclerView.Adapter<LessonListRecyclerViewAdapter.ViewHolder>{
    private final Context context;
    private final List<LessonGson> lessonGsonList;
    private final LessonListRecyclerViewAdapter.OnItemClickListener courseLessonOnItemClickListener;

    public LessonListRecyclerViewAdapter(Context context, List<LessonGson> lessonGsonList, LessonListRecyclerViewAdapter.OnItemClickListener courseLessonOnItemClickListener) {
        this.context = context;
        this.lessonGsonList = lessonGsonList;
        this.courseLessonOnItemClickListener = courseLessonOnItemClickListener;
    }

    @NonNull
    @Override
    public LessonListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_lesson_list, parent, false);
        return new LessonListRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonListRecyclerViewAdapter.ViewHolder holder, int position) {
        LessonGson model = lessonGsonList.get(position);
        String lessonNum = Integer.parseInt(model.getLessonNumber()) < 10 ? "0" + model.getLessonNumber() : model.getLessonNumber();
        holder.txtNumber.setText(lessonNum);
        holder.txtTitle.setText(model.getTitle());
    }

    @Override
    public int getItemCount() {
        return lessonGsonList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String itemId, String itemId2);
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
            btnOpen.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && courseLessonOnItemClickListener != null) {
                    courseLessonOnItemClickListener.onItemClick(lessonGsonList.get(position).getCourseLessonId(), lessonGsonList.get(position).getCourseId());
                }
            });
        }
    }
}
