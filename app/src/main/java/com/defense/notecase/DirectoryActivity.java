package com.defense.notecase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static com.defense.notecase.LoginActivity.BA_NUMBER;
import static com.defense.notecase.LoginActivity.SHARED_PREFS;

public class DirectoryActivity extends AppCompatActivity {
    private ImageView avatar,scan,helpline,notification,coroFile,uploadedFiles,ipftFile;
    private TextView coro,ipft,upfiles;
    private SharedPreferences sharedPref;
    private String baNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);

        avatar = findViewById(R.id.avatar);
        scan = findViewById(R.id.scan);
        helpline = findViewById(R.id.helpline);
        notification = findViewById(R.id.notification);
        coroFile = findViewById(R.id.coroFile);
        uploadedFiles = findViewById(R.id.uploadedFile);
        ipftFile = findViewById(R.id.ipftFile);
        coro = findViewById(R.id.coro);
        ipft = findViewById(R.id.ipft);
        upfiles = findViewById(R.id.upfile);

        sharedPref = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        baNumber = sharedPref.getString(BA_NUMBER, "123");

        ipftFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DirectoryActivity.this,FileShowActivity.class);
                intent.putExtra("fileType","IPFT Records");
                intent.putExtra("filePath","ipft records/"+baNumber);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
            }
        });
        ipft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DirectoryActivity.this,FileShowActivity.class);
                intent.putExtra("fileType","IPFT Records");
                intent.putExtra("filePath","ipft records/"+baNumber);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
            }
        });

        coroFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DirectoryActivity.this,FileShowActivity.class);
                intent.putExtra("fileType","CORO Records");
                intent.putExtra("filePath","coro records/"+baNumber);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
            }
        });

        coro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DirectoryActivity.this,FileShowActivity.class);
                intent.putExtra("fileType","CORO Records");
                intent.putExtra("filePath","coro records/"+baNumber);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
            }
        });

        upfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DirectoryActivity.this,FileShowActivity.class);
                intent.putExtra("fileType","Uploaded Files");
                intent.putExtra("filePath","uploaded files/"+baNumber);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
            }
        });

        uploadedFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DirectoryActivity.this,FileShowActivity.class);
                intent.putExtra("fileType","Uploaded Files");
                intent.putExtra("filePath","uploaded files/"+baNumber);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
            }
        });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DirectoryActivity.this,UserProfileActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DirectoryActivity.this,UploadActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
            }
        });
        helpline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DirectoryActivity.this,HelpLineActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DirectoryActivity.this,NotificationActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
            }
        });

        if (checkConnectivity()){
        } else {
            nointernetp();
        }

    }

    @Override
    public void onBackPressed()
    {
        exitapp();
    }
    private void exitapp() {
        AlertDialog.Builder builder=new AlertDialog.Builder(DirectoryActivity.this);
        builder.setCancelable(true);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Exit App");
        builder.setMessage("Are you sure you want to leave the application?");
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Yes",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which){
                finish();

            }
        });

        builder.setNegativeButton("No",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }

    private void nointernetp() {
        AlertDialog.Builder builder=new AlertDialog.Builder(DirectoryActivity.this);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_baseline_network_check_24);
        builder.setTitle("Bad Connection");
        builder.setMessage("No internet access, please activate the internet to use the app!");
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Close",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Reload",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which){

                Intent intent = new Intent(getBaseContext(), DirectoryActivity.class);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }

    private boolean checkConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if ((info == null || !info.isConnected() || !info.isAvailable())) {
            // Toast.makeText(getApplicationContext(), "Sin conexión a Internet...", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}