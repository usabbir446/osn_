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
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class RegActivity extends AppCompatActivity {
    private Button signUp;
    private EditText baNo,email,dob,phoneNumber;
    private DatePickerDialog.OnDateSetListener myDateListener;
    private Calendar calendar;
    private int year,month,day;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private SimpleDateFormat localDateFormat,serverDateFormat;
    private Date localDate,serverDate;
    private ProgressDialog progressDialog;
    private boolean flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        signUp =findViewById(R.id.confirm);
        baNo = findViewById(R.id.baNo);
        email = findViewById(R.id.email);
        dob = findViewById(R.id.dob);
        phoneNumber = findViewById(R.id.phoneNumber);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        firebaseDatabase = FirebaseDatabase.getInstance();
        serverDateFormat = new SimpleDateFormat("dd-mm-yyyy");
        localDateFormat = new SimpleDateFormat("dd/mm/yyyy");
        flag=false;


        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegActivity.this,
                        myDateListener, year, month, day).show();

            }
        });
        myDateListener = new
                DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0,
                                          int arg1, int arg2, int arg3) {
                        // TODO Auto-generated method stub
                        // arg1 = year
                        // arg2 = month
                        // arg3 = day
                        showDate(arg1, arg2+1, arg3);
                    }
                };
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkConnectivity()){
                } else {
                    nointernetp();
                    return;
                }

                if(!validateBaNo() | !validateEmail()  | !validatePhoneNumber() | !validateDob())
                {
                    return;
                }




                progressDialog = new ProgressDialog(RegActivity.this);
                progressDialog.setMessage("Please wait..."); // Setting Message
                progressDialog.setTitle("Validating"); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);
                databaseReference = firebaseDatabase.getReference("profiles");

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                        {

                            if(baNo.getText().toString().trim().equals(dataSnapshot1.getKey().toString().trim()))
                            {
                                flag=true;

                                if(dataSnapshot1.child("accountCreated").getValue().toString().equals("false"))
                                {
                                    try {
                                        localDate = localDateFormat.parse(dob.getText().toString().trim());
                                        serverDate = serverDateFormat.parse(dataSnapshot1.child("dob").getValue().toString());
                                        if(localDate.equals(serverDate))
                                        {
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(RegActivity.this,OtpActivity.class);
                                            intent.putExtra("email",email.getText().toString().trim());
                                            intent.putExtra("ba",baNo.getText().toString().trim());
                                            intent.putExtra("phoneNumber",phoneNumber.getText().toString().trim());
                                            startActivity(intent);
                                            finish();
                                            overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
//                                        Log.d("fb", "onDataChange: "+dataSnapshot1.getKey().toString());
//                                        Log.d("fb", "onDataChange: "+dob.getText().toString());
//                                        Log.d("fb", "onDataChange: "+dataSnapshot1.child("dob").getValue().toString());
//                                        Log.d("fb", "onDataChange: "+dataSnapshot1.child("name").getValue().toString());

                                        }
                                        else{
                                            progressDialog.dismiss();
                                            AlertDialog.Builder builder=new AlertDialog.Builder(RegActivity.this);
                                            builder.setCancelable(true);
                                            builder.setIcon(R.drawable.ic_baseline_error_outline_24);
                                            builder.setTitle("Validation Error");
                                            builder.setMessage("BA number and DOB do not match.");
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
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else {
                                    progressDialog.dismiss();
                                    AlertDialog.Builder builder=new AlertDialog.Builder(RegActivity.this);
                                    builder.setCancelable(true);
                                    builder.setIcon(R.drawable.ic_baseline_error_outline_24);
                                    builder.setTitle("Registration Error");
                                    builder.setMessage("An account with this BA number already exists.");
                                    builder.setInverseBackgroundForced(true);
                                    builder.setPositiveButton("Close",new DialogInterface.OnClickListener(){

                                        @Override
                                        public void onClick(DialogInterface dialog, int which){
                                            dialog.dismiss();
                                        }
                                    });

                                    AlertDialog alert=builder.create();
                                    if(hasWindowFocus())
                                    {
                                        alert.show();
                                    }


                                }
                                break;
                            }
                        }
                        if(!flag)
                        {
                            progressDialog.dismiss();
                            AlertDialog.Builder builder=new AlertDialog.Builder(RegActivity.this);
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

        if (checkConnectivity()){
        } else {
            nointernetp();
        }
    }

    private boolean validateDob() {
        String val = dob.getText().toString();

        if(val.isEmpty())
        {
            dob.setError("Field cannot be empty");
            return  false;
        }
        else {
            dob.setError(null);
            return true;
        }
    }

    private boolean validatePhoneNumber() {
        String val = phoneNumber.getText().toString();
        String phoneNumberPattern = "^([0][1]|[+][8][8][0][1])([3-9]{1}[0-9]{8})";
        if(val.isEmpty())
        {
            phoneNumber.setError("Field cannot be empty");
            return false;
        }
        if(!val.matches(phoneNumberPattern))
        {
            phoneNumber.setError("Invalid phone number");
            return false;
        }
        else {
            phoneNumber.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String val = email.getText().toString();
        String emailPattern = "^(.+)@(.+)$";

        if(val.isEmpty())
        {
            email.setError("Field cannot be empty");
            return false;
        }
        if(!val.matches(emailPattern))
        {
            email.setError("Invalid email address");
            return false;
        }
        else {
            email.setError(null);
            return true;
        }

    }

    private boolean validateBaNo() {
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




    private void showDate(int year, int month, int day) {
        dob.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
    }

    private void nointernetp() {
        AlertDialog.Builder builder=new AlertDialog.Builder(RegActivity.this);
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

                Intent intent = new Intent(getBaseContext(), RegActivity.class);
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