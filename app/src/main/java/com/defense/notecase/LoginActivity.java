package com.defense.notecase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.ParseException;

public class LoginActivity extends AppCompatActivity {
    private Button login,reg;
    private EditText baNo,pass;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPref;
    private boolean flag;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String IS_LOGGED_IN = "isLoggedIn";
    public static final String BA_NUMBER = "baNumber";
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        baNo = findViewById(R.id.baNo);
        pass = findViewById(R.id.email);
        login = findViewById(R.id.confirm);
        reg = findViewById(R.id.register);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
        sharedPref = getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);

        flag = false;

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Token", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                       token = task.getResult();

                        Log.d("Token", token);
//                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnectivity()){
                } else {
                    nointernetp();
                    return;
                }
                if(!validateBaNumber() | !validatePass())
                {
                    return;
                }
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Please wait..."); // Setting Message
                progressDialog.setTitle("Validating"); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                        {

                            if(baNo.getText().toString().trim().equals(dataSnapshot1.getKey().toString().trim()))
                            {
                                flag = true;

                                if(pass.getText().toString().trim().equals(dataSnapshot1.child("password").getValue().toString()))
                                {

                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putBoolean(IS_LOGGED_IN, true);
                                    editor.putString(BA_NUMBER,baNo.getText().toString().trim());
                                    editor.apply();
                                    progressDialog.dismiss();
                                    databaseReference.child(baNo.getText().toString().trim()).child("regToken").setValue(token);
                                    startActivity(new Intent(LoginActivity.this,DirectoryActivity.class));
                                    finish();
                                    overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
                                }
                                else {
                                    progressDialog.dismiss();
                                    AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                                    builder.setCancelable(true);
                                    builder.setIcon(R.drawable.ic_baseline_error_outline_24);
                                    builder.setTitle("Validation Error");
                                    builder.setMessage("BA number and Password do not match.");
                                    builder.setInverseBackgroundForced(true);
                                    builder.setPositiveButton("Close",new DialogInterface.OnClickListener(){

                                        @Override
                                        public void onClick(DialogInterface dialog, int which){
                                            dialog.dismiss();
                                        }
                                    });

                                    AlertDialog alert=builder.create();
                                    alert.show();
                                }
                                break;
                            }
                        }

                        if(!flag)
                        {
                            progressDialog.dismiss();
                            AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                            builder.setCancelable(true);
                            builder.setIcon(R.drawable.ic_baseline_error_outline_24);
                            builder.setTitle("Validation Error");
                            builder.setMessage("Couldn't find the BA number you entered.");
                            builder.setInverseBackgroundForced(true);
                            builder.setPositiveButton("Close",new DialogInterface.OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog alert=builder.create();
                            alert.show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this,RegActivity.class));
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);

            }
        });

        if (checkConnectivity()){
        } else {
            nointernetp();
        }

    }

    private boolean validatePass() {
        String val = pass.getText().toString();


        if(val.isEmpty())
        {
            pass.setError("Field cannot be empty");
            return false;
        }
        else {
            pass.setError(null);
            return true;
        }
    }

    private boolean validateBaNumber() {
        String val = baNo.getText().toString();
        String baNoPattern = "^(BA-).*[0-9]$";

        if(val.isEmpty())
        {
            baNo.setError("Field cannot be empty");
            return false;
        }
        if(!val.matches(baNoPattern))
        {
            baNo.setError("Invalid BA No");
            return false;
        }
        else {
            baNo.setError(null);
            return true;
        }
    }

    @Override
    public void onBackPressed()
    {
        exitapp();
    }
    private void exitapp() {
        AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
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
        AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
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

                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
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