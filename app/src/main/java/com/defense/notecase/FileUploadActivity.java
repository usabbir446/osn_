package com.defense.notecase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.defense.notecase.models.FileModel;
import com.defense.notecase.models.NotificationModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static com.defense.notecase.LoginActivity.BA_NUMBER;
import static com.defense.notecase.LoginActivity.IS_LOGGED_IN;
import static com.defense.notecase.LoginActivity.SHARED_PREFS;

public class FileUploadActivity extends AppCompatActivity {

    private TextView actionBar,fileName;
    private Button selectFile,uploadFile;
    private String extra,baNumber,extractedFilename,fileType;
    private ImageView back2;
    private Uri uri;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReferenceRead,databaseReference;
    private ArrayList<String> baNumbers,regTokens;
    private boolean iPftFlag,cOroFlag,isLoggedIn;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private SharedPreferences sharedPref;
    private ProgressDialog progressDialog;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);

        actionBar = findViewById(R.id.actionBar);
        fileName = findViewById(R.id.fileName);
        selectFile = findViewById(R.id.selectFile);
        uploadFile = findViewById(R.id.uploadFile);
        back2 = findViewById(R.id.back2);
        uri = null;
        baNumbers = new ArrayList<String>();
        regTokens = new ArrayList<String>();

        iPftFlag =false;
        cOroFlag = false;

        firebaseDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        databaseReferenceRead = firebaseDatabase.getReference("users");
        databaseReference = firebaseDatabase.getReference("");

        extra = getIntent().getStringExtra("type");

        sharedPref = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        isLoggedIn = sharedPref.getBoolean(IS_LOGGED_IN,false);

        if(isLoggedIn) {
            baNumber = sharedPref.getString(BA_NUMBER, "123");
        }

        requestQueue = Volley.newRequestQueue(FileUploadActivity.this);

        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iPftFlag = false;
                cOroFlag = false;
                if(ContextCompat.checkSelfPermission(FileUploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    selectFile();
                }
                else {
                    ActivityCompat.requestPermissions(FileUploadActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},10);
                }
            }
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        // Get new FCM registration token
                        Log.d("hola", "onComplete: "+task.getResult());

                    }
                });

        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkConnectivity())
                {
                    nointernetp();
                    return;
                }
                if(iPftFlag == true | cOroFlag== true)
                {
                    scanPdfndUpload();
                }
                else {
                    upload();
                }


            }
        });

        actionBar.setText(extra);

        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FileUploadActivity.this,UploadActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
            }
        });

    }



    private void upload() {

        if(uri!=null)
        {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("Uploading...");
            progressDialog.setProgress(0);
            progressDialog.setCancelable(false);
            progressDialog.show();
            String name = System.currentTimeMillis()+"";
            storageReference = storage.getReference("uploaded files");
            storageReference.child(baNumber).child(name+" "+extractedFilename).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    storageReference.child(baNumber).child(name+" "+extractedFilename).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url =  uri.toString();
                            FileModel fileModel = new FileModel(extractedFilename,url,baNumber,new Date().toString(),fileType);
                            databaseReference.child("uploaded files").child(baNumber).child(name).setValue(fileModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toasty.success(FileUploadActivity.this,"File uploaded successfully",Toasty.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toasty.error(FileUploadActivity.this,"File not uploaded successfully",Toasty.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toasty.error(FileUploadActivity.this,"Could not upload the file.",Toasty.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    int currentProgress = (int) ((100* snapshot.getBytesTransferred())/snapshot.getTotalByteCount());
                    progressDialog.setProgress(currentProgress);
                }
            });
        }
        else {
            Toasty.warning(FileUploadActivity.this,"Select a file",Toasty.LENGTH_SHORT).show();
        }


    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if( requestCode == 10 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            selectFile();
        }
        else {
            Toasty.warning(FileUploadActivity.this, "Please provide permission...", Toasty.LENGTH_SHORT).show();
        }
    }




    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            uri = data.getData();
            Cursor returnCursor =
                    getContentResolver().query(uri, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            extractedFilename = returnCursor.getString(nameIndex);
            fileType = getContentResolver().getType(uri);
            fileName.setText(returnCursor.getString(nameIndex));
            if(extractedFilename.toLowerCase().contains("ipft"))
            {

                iPftFlag = true;
                Toasty.success(FileUploadActivity.this,"IPFT record found",Toasty.LENGTH_SHORT).show();
            }
            else if(extractedFilename.toLowerCase().contains("coro"))
            {
                cOroFlag = true;
                Toasty.success(FileUploadActivity.this,"CORO record found",Toasty.LENGTH_SHORT).show();
            }

        } else {
            Toasty.warning(FileUploadActivity.this,"Please select a file.",Toasty.LENGTH_SHORT).show();
        }
    }





    private void scanPdfndUpload() {
        try {
            String extractedText = "";

            AssetManager assetManager = getAssets();
            InputStream is = getContentResolver().openInputStream(uri);
            PdfReader reader = new PdfReader(is);

            int n = reader.getNumberOfPages();

            for (int i = 0; i < n; i++) {
                extractedText = extractedText + PdfTextExtractor.getTextFromPage(reader, i + 1).trim() + "\n";
                // to extract the PDF content from the different pages
            }

            String[] lines = extractedText.split("\n");
            String requiredLines = "";

            for(String l:lines)
            {
                if(l.matches("^[0-9].*$"))
                {
                    requiredLines += l + "\n";
                }
            }
            String[] words = requiredLines.split("\\s+");
            String requiredWords = "";

            for(String w:words)
            {
                if(w.matches("^(BA-).*[0-9]$"))
                {
                    baNumbers.add(w);
                }
            }
            databaseReferenceRead.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot snapshot1: snapshot.getChildren())
                    {
                        if(baNumbers.contains(snapshot1.child("ba").getValue().toString().trim()))
                        {
                            regTokens.add(snapshot1.child("regToken").getValue().toString().trim());
                        }

                    }

                    uploadAndCirculate();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            reader.close();
        } catch (Exception e) {
        }
    }

    private void uploadAndCirculate() {
        if(uri!=null)
        {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("Uploading...");
            progressDialog.setProgress(0);
            progressDialog.setCancelable(false);
            progressDialog.show();
            String name = System.currentTimeMillis()+"";
            if(iPftFlag==true)
            {
                storageReference = storage.getReference("ipft records");
            }
            else if(cOroFlag==true)
            {
                storageReference = storage.getReference("coro records");
            }
            storageReference.child(baNumber).child(name+" "+extractedFilename).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    storageReference.child(baNumber).child(name+" "+extractedFilename).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String url =  uri.toString();
                            progressDialog.dismiss();
                            progressDialog = new ProgressDialog(FileUploadActivity.this);
                            progressDialog.setMessage("Please wait..."); // Setting Message
                            progressDialog.setTitle("Circulating"); // Setting Title
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                            progressDialog.show(); // Display Progress Dialog
                            progressDialog.setCancelable(false);
                            Date d1 = new Date();
                            String dateStr = d1.toString();
                            DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
                            DateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
                            String formattedDate = null;
                            try {
                                formattedDate = formatter1.format(formatter.parse(dateStr));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            FileModel fileModel = new FileModel(extractedFilename,url,baNumber,dateStr,fileType);
                            if(iPftFlag==true)
                            {
                                NotificationModel notificationModel = new NotificationModel(dateStr,"ipft",baNumber,url,"IPFT Record Updated "+formattedDate);
                                for(String ba:baNumbers)
                                {
                                    databaseReference.child("ipft records").child(ba).child(name).setValue(fileModel);
                                    databaseReference.child("notifications").child(ba).child(name).setValue(notificationModel);
                                }


                                Notifier(regTokens,"IPFT Record","IPFT Record Updated "+formattedDate);




                            }
                            else if(cOroFlag==true)
                            {
                                NotificationModel notificationModel = new NotificationModel(dateStr,"coro",baNumber,url,"Coro Record Updated "+formattedDate);
                                for(String ba:baNumbers)
                                {
                                    databaseReference.child("coro records").child(ba).child(name).setValue(fileModel);
                                    databaseReference.child("notifications").child(ba).child(name).setValue(notificationModel);
                                }

                                Notifier(regTokens,"CORO Record","CORO Record Updated "+formattedDate);
                            }

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    // yourMethod();
                                    progressDialog.dismiss();
                                    Toasty.success(FileUploadActivity.this,"File uploaded and circulated successfully",Toasty.LENGTH_SHORT).show();
                                }
                            }, 2000);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toasty.error(FileUploadActivity.this,"Could not upload the file.",Toasty.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    int currentProgress = (int) ((100* snapshot.getBytesTransferred())/snapshot.getTotalByteCount());
                    progressDialog.setProgress(currentProgress);
                }
            });
        }
        else {
            Toasty.warning(FileUploadActivity.this,"Select a file",Toasty.LENGTH_SHORT).show();
        }
    }


    private void selectFile() {
        Intent intent = new Intent();
        if(extra.equals("pdf"))
        {
            intent.setType("application/pdf");
        }
        else if(extra.equals("image"))
        {
            intent.setType("image/*");
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }



    @Override
    public void onBackPressed() {
        startActivity(new Intent(FileUploadActivity.this,UploadActivity.class));
        finish();
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
    }




    private void nointernetp() {
        AlertDialog.Builder builder=new AlertDialog.Builder(FileUploadActivity.this);
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

                Intent intent = new Intent(getBaseContext(), FileUploadActivity.class);
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


    private void Notifier(ArrayList<String> regToken,String title,String body){

        JSONObject data = new JSONObject();
        try {
            JSONObject notify = new JSONObject();
            JSONArray tokens = new JSONArray(regToken);
            notify.put("title", title);
            notify.put("body", body);
//            notify.put("android_channel_id","cuet_connect_primary_notification_channel");
            data.put("notification", notify);
            data.put("registration_ids", tokens);
            Log.d("simji", "Notifier: " + data);

            String Url = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Url,
                    data,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAAKXvMyPw:APA91bG1msPzblmMlyJ26XSYH-oltn47LlP-sKtHbsHwscqZbdSKQLduYPLGQot9ncDDxdAoBDrs4Z2Bcu2a8ouAuhiCYPjvVqlqQbofIGdr3DoupczkVgeAdi9PGAC83Wy6R6T0YYJA");
                    return  header;
                }
            };
            requestQueue.add(request);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

}