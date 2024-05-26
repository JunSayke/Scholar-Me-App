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

public class CourseListRecyclerViewAdapter extends RecyclerView.Adapter<CourseListRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private final List<CourseGson> courseGsonList;
    private final CourseListRecyclerViewAdapter.OnItemClickListener onItemClickListener;

    public CourseListRecyclerViewAdapter(Context context, List<CourseGson> courseGsonList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.courseGsonList = courseGsonList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CourseListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_course_list, parent, false);
        return new CourseListRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseListRecyclerViewAdapter.ViewHolder holder, int position) {
        CourseGson model = courseGsonList.get(position);
        String fullname = model.getAuthor().getFirstName() + " " + model.getAuthor().getLastName();
        holder.txtTitle.setText(model.getTitle());
        holder.txtAuthor.setText(fullname);
        holder.txtDetail.setText(model.getDateUpdated());
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
        private final TextView txtAuthor;
        private final TextView txtTitle;
        private final TextView txtDetail;
        private final ImageButton btnOpen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAuthor = itemView.findViewById(R.id.course_list_txt_creator);
            txtTitle = itemView.findViewById(R.id.course_list_txt_title);
            txtDetail = itemView.findViewById(R.id.course_list_txt_course_detail);
            btnOpen = itemView.findViewById(R.id.course_list_btn_course);
            btnOpen.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    onItemClickListener.onItemClick(courseGsonList.get(position).getId());
                }
            });
        }
    }
}
