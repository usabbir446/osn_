package com.defense.notecase.viewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.defense.notecase.R;

public class NotificationViewHolder extends RecyclerView.ViewHolder {
    public TextView notificationBody;
    public NotificationViewHolder(@NonNull View itemView) {
        super(itemView);
        notificationBody = itemView.findViewById(R.id.notificationBody);
    }
}
