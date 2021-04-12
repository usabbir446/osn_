package com.defense.notecase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import es.dmoral.toasty.Toasty;

import static com.defense.notecase.LoginActivity.BA_NUMBER;
import static com.defense.notecase.LoginActivity.IS_LOGGED_IN;
import static com.defense.notecase.LoginActivity.SHARED_PREFS;

public class ProfileEditActivity extends AppCompatActivity {

    private ImageView back,update;
    private EditText nameEdit,dobEdit,phoneNumEdit,baEdit,emailEdit,unitEdit;
    private DatePickerDialog.OnDateSetListener myDateListener;
    private Calendar calendar;
    private SharedPreferences sharedPref;
    private int year,month,day;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference,databaseReference1;
    private ProgressDialog progressDialog;
    private String baNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        back = findViewById(R.id.back);
        nameEdit = findViewById(R.id.nameEdit);
        dobEdit = findViewById(R.id.dobEdit);
        phoneNumEdit = findViewById(R.id.phoneNumEdit);
        baEdit = findViewById(R.id.baEdit);
        emailEdit = findViewById(R.id.emailEdit);
        unitEdit = findViewById(R.id.unitEdit);
        update = findViewById(R.id.update);

        baEdit.setFocusable(false);
        baEdit.setFocusableInTouchMode(false);
        baEdit.setClickable(false);
        dobEdit.setFocusable(false);
        dobEdit.setFocusableInTouchMode(false);
        dobEdit.setClickable(false);
        nameEdit.setFocusable(false);
        nameEdit.setFocusableInTouchMode(false);
        nameEdit.setClickable(false);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        sharedPref = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        baNumber = sharedPref.getString(BA_NUMBER, "123");


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("profiles/"+baNumber);
        databaseReference1 = firebaseDatabase.getReference("users/"+baNumber);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                baEdit.setText(snapshot.child("ba").getValue().toString().trim());
                unitEdit.setText(snapshot.child("unit").getValue().toString().trim());
                dobEdit.setText(snapshot.child("dob").getValue().toString().trim());
                nameEdit.setText(snapshot.child("name").getValue().toString().trim());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                emailEdit.setText(snapshot.child("email").getValue().toString().trim());
                phoneNumEdit.setText(snapshot.child("phoneNumber").getValue().toString().trim());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileEditActivity.this,UserProfileActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
            }
        });



        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnectivity()){
                } else {
                    nointernetp();
                    return;
                }

                if(!validateBaNo() | !validateEmail()  | !validatePhoneNumber() | !validateDob() | !validateName() | !validateUnit())
                {
                    return;
                }

                progressDialog = new ProgressDialog(ProfileEditActivity.this);
                progressDialog.setMessage("Please wait..."); // Setting Message
                progressDialog.setTitle("Updating"); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);

                databaseReference.child("unit").setValue(unitEdit.getText().toString().trim());
                databaseReference1.child("email").setValue(emailEdit.getText().toString().trim());
                databaseReference1.child("phoneNumber").setValue(phoneNumEdit.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toasty.success(ProfileEditActivity.this,"Successfully Updated User Info",Toasty.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toasty.error(ProfileEditActivity.this,"Could not Update User Info successfully",Toasty.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
    }


    private boolean validateName()
    {
        String val = nameEdit.getText().toString();

        if(val.isEmpty())
        {
            nameEdit.setError("Field cannot be empty");
            return false;
        }
        else
        {
            return true;
        }
    }

    private boolean validateUnit()
    {
        String val = unitEdit.getText().toString();

        if (val.isEmpty())
        {
            unitEdit.setError("Field cannot be empty");
            return false;
        }
        else {
            return true;
        }
    }

    private boolean validateDob() {
        String val = dobEdit.getText().toString();

        if(val.isEmpty())
        {
            dobEdit.setError("Field cannot be empty");
            return  false;
        }
        else {
            dobEdit.setError(null);
            return true;
        }
    }

    private boolean validatePhoneNumber() {
        String val = phoneNumEdit.getText().toString();
        String phoneNumberPattern = "^([0][1]|[+][8][8][0][1])([3-9]{1}[0-9]{8})";
        if(val.isEmpty())
        {
            phoneNumEdit.setError("Field cannot be empty");
            return false;
        }
        if(!val.matches(phoneNumberPattern))
        {
            phoneNumEdit.setError("Invalid phone number");
            return false;
        }
        else {
            phoneNumEdit.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String val = emailEdit.getText().toString();
        String emailPattern = "^(.+)@(.+)$";

        if(val.isEmpty())
        {
            emailEdit.setError("Field cannot be empty");
            return false;
        }
        if(!val.matches(emailPattern))
        {
            emailEdit.setError("Invalid email address");
            return false;
        }
        else {
            emailEdit.setError(null);
            return true;
        }

    }

    private boolean validateBaNo() {
        String val = baEdit.getText().toString();
        String baNoPattern = "^(BA-).*[0-9]$";

        if(val.isEmpty())
        {
            baEdit.setError("Field cannot be empty");
            return false;
        }
        if(!val.matches(baNoPattern))
        {
            baEdit.setError("Invalid BA No");
            return false;
        }
        else {
            baEdit.setError(null);
            return true;
        }
    }


    private void nointernetp() {
        AlertDialog.Builder builder=new AlertDialog.Builder(ProfileEditActivity.this);
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

                Intent intent = new Intent(getBaseContext(), ProfileEditActivity.class);
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