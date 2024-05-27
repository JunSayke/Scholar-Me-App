//package com.example.solutionsproject.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.solutionsproject.R;
//import com.example.solutionsproject.model.gson.data.NotificationGson;
//
//import java.util.List;
//
//import lombok.Getter;
//
//public class MessageListRecyclerViewAdapter extends RecyclerView.Adapter<MessageListRecyclerViewAdapter.ViewHolder>{
//
//    private final Context context;
//    private final List<NotificationGson> notificationGsonList;
//
//    public MessageListRecyclerViewAdapter(Context context, List<NotificationGson> notificationGsonList) {
//        this.context = context;
//        this.notificationGsonList = notificationGsonList;
//    }
//
//    @NonNull
//    @Override
//    public MessageListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_messages_messages, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MessageListRecyclerViewAdapter.ViewHolder holder, int position) {
//        NotificationGson model = notificationGsonList.get(position);
//        holder.txtTitle.setText(model.getTitle());
//        holder.txtDate.setText(model.getDateUpdated());
//    }
//
//    @Override
//    public int getItemCount() {
//        return notificationGsonList.size();
//    }
//
//    @Getter
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        private final TextView txtTitle;
//        private final TextView txtDate;
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            txtTitle = itemView.findViewById(R.id.messages_notifications_txt_title);
//            txtDate = itemView.findViewById(R.id.messages_notifications_txt_date);
//        }
//    }
//}
