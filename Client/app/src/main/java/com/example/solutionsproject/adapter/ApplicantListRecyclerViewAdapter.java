package com.example.solutionsproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solutionsproject.R;
import com.example.solutionsproject.model.gson.data.ApplicantsGson;

import java.util.List;

import lombok.Getter;

public class ApplicantListRecyclerViewAdapter extends RecyclerView.Adapter<ApplicantListRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private final List<ApplicantsGson> ApplicantsGsonList;
    private final ApplicantListRecyclerViewAdapter.OnItemClickListener onAcceptItemClickListener;
    private final ApplicantListRecyclerViewAdapter.OnItemClickListener onRejectItemClickListener;

    public ApplicantListRecyclerViewAdapter(Context context, List<ApplicantsGson> ApplicantsGsonList, OnItemClickListener onAcceptItemClickListener, OnItemClickListener onRejectItemClickListener) {
        this.context = context;
        this.ApplicantsGsonList = ApplicantsGsonList;
        this.onAcceptItemClickListener = onAcceptItemClickListener;
        this.onRejectItemClickListener = onRejectItemClickListener;
    }

    @NonNull
    @Override
    public ApplicantListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_messages_requests, parent, false);
        return new ApplicantListRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicantListRecyclerViewAdapter.ViewHolder holder, int position) {
        ApplicantsGson model = ApplicantsGsonList.get(position);
        String message = "User ID: " + model.getUserId() + " requests to be a creator.";
        holder.txtContent.setText(message);
        holder.txtDate.setText(model.getDateAdded());
    }

    @Override
    public int getItemCount() {
        return ApplicantsGsonList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String itemId);
    }

    @Getter
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtContent;
        private final TextView txtDate;
        private final Button btnAccept;
        private final Button btnReject;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtContent = itemView.findViewById(R.id.messages_requests_txt_title);
            txtDate = itemView.findViewById(R.id.messages_requests_txt_date);
            btnAccept = itemView.findViewById(R.id.messages_requests_btn_close);
            btnReject = itemView.findViewById(R.id.messages_requests_btn_save);
            btnAccept.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onAcceptItemClickListener != null) {
                    onAcceptItemClickListener.onItemClick(String.valueOf(ApplicantsGsonList.get(position).getCreatorApplicantId()));
                }
            });
            btnReject.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onRejectItemClickListener != null) {
                    onRejectItemClickListener.onItemClick(String.valueOf(ApplicantsGsonList.get(position).getCreatorApplicantId()));
                }
            });
        }
    }
}