package com.defense.notecase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

import static com.defense.notecase.LoginActivity.BA_NUMBER;
import static com.defense.notecase.LoginActivity.SHARED_PREFS;

public class AuthenticationActivity extends AppCompatActivity {
    private String destination;
    private EditText rfId;
    private Button submit;
    private TextView hello;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPref;
    private String baNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        submit = findViewById(R.id.submit);
        rfId = findViewById(R.id.rfId);
        hello = findViewById(R.id.hello);
        destination = getIntent().getStringExtra("destination");
        sharedPref = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        baNumber = sharedPref.getString(BA_NUMBER, "123");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("profiles").child(baNumber);

        hello.setText("Hello User!!!");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hello.setText("Hello "+snapshot.child("name").getValue().toString()+"!!!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateRfId())
                {
                    return;
                }
                if(destination.equals("editProfile"))
                {
                    startActivity(new Intent(AuthenticationActivity.this,ProfileEditActivity.class));
                    finish();
                    overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
                }
                else if(destination.equals("pdf"))
                {
                    Intent intent = new Intent(AuthenticationActivity.this,FileUploadActivity.class);
                    intent.putExtra("type","pdf");
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
                }
                else if(destination.equals("image"))
                {
                    Intent intent = new Intent(AuthenticationActivity.this,FileUploadActivity.class);
                    intent.putExtra("type","image");
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
                }
            }
        });

        if (checkConnectivity()){
        } else {
            nointernetp();
        }


    }

    private boolean validateRfId() {
        String val = rfId.getText().toString().trim();
        if(val.isEmpty())
        {
            rfId.setError("Field can't be empty");
            return false;
        }
        else if(val.equals("123"))
        {
            return true;
        }
        else {
            Toasty.warning(AuthenticationActivity.this,"RfId do not match.",Toasty.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
    }
    private void nointernetp() {
        AlertDialog.Builder builder=new AlertDialog.Builder(AuthenticationActivity.this);
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

                Intent intent = new Intent(getBaseContext(), AuthenticationActivity.class);
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