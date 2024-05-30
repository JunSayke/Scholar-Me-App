package com.example.solutionsproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solutionsproject.R;
import com.example.solutionsproject.model.gson.data.CommentGson;

import java.util.List;

import lombok.Getter;

public class DiscussionCommentListRecyclerViewAdapter extends RecyclerView.Adapter<DiscussionCommentListRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private final List<CommentGson> commentGsonList;

    public DiscussionCommentListRecyclerViewAdapter(Context context, List<CommentGson> commentGsonList) {
        this.context = context;
        this.commentGsonList = commentGsonList;
    }

    @NonNull
    @Override
    public DiscussionCommentListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_messages_messages, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscussionCommentListRecyclerViewAdapter.ViewHolder holder, int position) {
        CommentGson model = commentGsonList.get(position);
        holder.txtContent.setText(model.getComment());
        holder.txtDate.setText(model.getDateAdded());
    }

    @Override
    public int getItemCount() {
        return commentGsonList.size();
    }

    @Getter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtContent;
        private final TextView txtDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtContent = itemView.findViewById(R.id.messages_notifications_txt_content);
            txtDate = itemView.findViewById(R.id.messages_notifications_txt_date);
        }
    }
}