package com.defense.notecase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import es.dmoral.toasty.Toasty;

public class NewPasswordActivity extends AppCompatActivity {
    private Button confirm;
    private String email,phoneNumber,baNumber,regToken;
    private EditText pass,confirmPass;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference,databaseReference1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);


        email = getIntent().getStringExtra("email");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        baNumber = getIntent().getStringExtra("ba");

        pass = findViewById(R.id.pass);
        confirmPass = findViewById(R.id.confirmPass);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
        databaseReference1 = firebaseDatabase.getReference("profiles");



//                                        Log.d("fb", "onDataChange: "+email);
//                                        Log.d("fb", "onDataChange: "+phoneNumber);
//                                        Log.d("fb", "onDataChange: "+baNumber);
        confirm = findViewById(R.id.confirm);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        // Get new FCM registration token
                        regToken = task.getResult();

                    }
                });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkConnectivity()){
                } else {
                    nointernetp();
                    return;
                }
                if(!validatePass() | !validateConfirmPass())
                {
                    return;
                }


                if(pass.getText().toString().trim().equals(confirmPass.getText().toString().trim()))
                {

                    databaseReference.child(baNumber).child("ba").setValue(baNumber);
                    databaseReference.child(baNumber).child("regToken").setValue(regToken);
                    databaseReference.child(baNumber).child("phoneNumber").setValue(phoneNumber);
                    databaseReference.child(baNumber).child("email").setValue(email);
                    databaseReference.child(baNumber).child("password").setValue(pass.getText().toString().trim());
                    databaseReference1.child(baNumber).child("accountCreated").setValue("true");


                    Toasty.success(NewPasswordActivity.this,"Registration successful.",Toasty.LENGTH_SHORT).show();

                    Intent intent = new Intent(NewPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
                }
                else {

                    AlertDialog.Builder builder=new AlertDialog.Builder(NewPasswordActivity.this);
                    builder.setCancelable(true);
                    builder.setIcon(R.drawable.ic_baseline_error_outline_24);
                    builder.setTitle("Validation Error");
                    builder.setMessage("Password do not match");
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
        });
    }

    private boolean validateConfirmPass() {
        String val = confirmPass.getText().toString();

        if(val.isEmpty())
        {
            confirmPass.setError("Field cannot be empty");
            return false;
        }
        else {
            return true;
        }
    }

    private boolean validatePass() {
        String val = pass.getText().toString().trim();

        if(val.isEmpty())
        {
            pass.setError("Field cannot be empty");
            return false;
        }
        else {
            return true;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
    }

    private void nointernetp() {
        AlertDialog.Builder builder=new AlertDialog.Builder(NewPasswordActivity.this);
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

                Intent intent = new Intent(getBaseContext(), NewPasswordActivity.class);
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