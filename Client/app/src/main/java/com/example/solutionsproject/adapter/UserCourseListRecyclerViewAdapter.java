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
import com.example.solutionsproject.model.gson.data.CourseGson;

import java.util.List;

import lombok.Getter;

public class UserCourseListRecyclerViewAdapter extends RecyclerView.Adapter<UserCourseListRecyclerViewAdapter.ViewHolder>{

    private final Context context;
    private final List<CourseGson> courseGsonList;
    private final UserCourseListRecyclerViewAdapter.OnItemClickListener onItemClickListener;

    public UserCourseListRecyclerViewAdapter(Context context, List<CourseGson> courseGsonList, UserCourseListRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        this.context = context;
        this.courseGsonList = courseGsonList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public UserCourseListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_home_learning_plan, parent, false);
        return new UserCourseListRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserCourseListRecyclerViewAdapter.ViewHolder holder, int position) {
        CourseGson model = courseGsonList.get(position);
        holder.txtTitle.setText(model.getTitle());
    }

    @Override
    public int getItemCount() {
        return courseGsonList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String itemId);
    }

    @Getter
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtTitle;
        private final ImageButton btnOpen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.home_learning_plan_txt_title);
            btnOpen = itemView.findViewById(R.id.home_learning_plan_btn_course);
            btnOpen.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    onItemClickListener.onItemClick(courseGsonList.get(position).getId());
                }
            });
        }
    }
}
