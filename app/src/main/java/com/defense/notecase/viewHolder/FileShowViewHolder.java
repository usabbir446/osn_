package com.defense.notecase.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.defense.notecase.R;

public class FileShowViewHolder extends RecyclerView.ViewHolder {
    public ImageView fileType;
    public TextView fileName;
    public LinearLayout file;
    public FileShowViewHolder(@NonNull View itemView) {
        super(itemView);
        fileType = itemView.findViewById(R.id.fileType);
        fileName = itemView.findViewById(R.id.fileName);
        file = itemView.findViewById(R.id.file);
    }
}
