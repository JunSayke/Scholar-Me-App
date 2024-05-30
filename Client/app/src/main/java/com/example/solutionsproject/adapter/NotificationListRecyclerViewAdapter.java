//package com.example.solutionsproject.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
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
//public class NotificationListRecyclerViewAdapter extends RecyclerView.Adapter<NotificationListRecyclerViewAdapter.ViewHolder>{
//
//    private final Context context;
//    private final List<NotificationGson> notificationGsonList;
//    private final NotificationListRecyclerViewAdapter.OnItemClickListener onItemClickListener;
//
//    public NotificationListRecyclerViewAdapter(Context context, List<NotificationGson> notificationGsonList, NotificationListRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
//        this.context = context;
//        this.notificationGsonList = notificationGsonList;
//        this.onItemClickListener = onItemClickListener;
//    }
//
//    @NonNull
//    @Override
//    public NotificationListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_messages_notifications, parent, false);
//        return new NotificationListRecyclerViewAdapter.ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull NotificationListRecyclerViewAdapter.ViewHolder holder, int position) {
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
//    public interface OnItemClickListener {
//        void onItemClick(String itemId);
//    }
//
//    @Getter
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        private final TextView txtTitle;
//        private final TextView txtDate;
//        private final ImageButton btnDelete;
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            txtTitle = itemView.findViewById(R.id.messages_notifications_txt_title);
//            txtDate = itemView.findViewById(R.id.messages_notifications_txt_date);
//            btnDelete = itemView.findViewById(R.id.messages_notifications_btn_delete);
//            btnDelete.setOnClickListener(v -> {
//                int position = getBindingAdapterPosition();
//                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
//                    onItemClickListener.onItemClick(String.valueOf(notificationGsonList.get(position).getNotificationId()));
//                }
//            });
//        }
//    }
//}
