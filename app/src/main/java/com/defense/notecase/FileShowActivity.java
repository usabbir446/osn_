package com.defense.notecase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.defense.notecase.models.FileModel;
import com.defense.notecase.models.NotificationModel;
import com.defense.notecase.viewHolder.FileShowViewHolder;
import com.defense.notecase.viewHolder.NotificationViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FileShowActivity extends AppCompatActivity {
    private String fileType,filePath;
    private TextView directoryType;
    private RecyclerView recyclerView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPref;
    private LinearLayoutManager layoutManager;
    private FirebaseRecyclerAdapter<FileModel, FileShowViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_show);

        directoryType = findViewById(R.id.directoryType);
        recyclerView = findViewById(R.id.fileList);
        fileType = getIntent().getStringExtra("fileType");
        filePath = getIntent().getStringExtra("filePath");
        directoryType.setText(fileType);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(filePath);
        databaseReference.keepSynced(true);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<FileModel>().setQuery(databaseReference ,FileModel.class).build();

        adapter = new FirebaseRecyclerAdapter<FileModel, FileShowViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FileShowViewHolder holder, int position, @NonNull FileModel model) {
                holder.fileName.setText(model.getFileName());
                holder.file.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FileShowActivity.this,FileLoadActivity.class);
                        intent.putExtra("url",model.getFileLink());
                        intent.putExtra("fileType",model.getFileType());
                        startActivity(intent);
                    }
                });
                if(model.getFileType().contains("image"))
                {
                    holder.fileType.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_image_24));

                }
                else {
                    holder.fileType.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_picture_as_pdf_24));
                }
            }

            @NonNull
            @Override
            public FileShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.file_row,parent,false);
                FileShowViewHolder fileShowViewHolder = new FileShowViewHolder(view);
                return fileShowViewHolder;
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
    }
}