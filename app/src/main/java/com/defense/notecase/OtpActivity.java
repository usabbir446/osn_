package com.defense.notecase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class OtpActivity extends AppCompatActivity {
    private Button confirm;
    private String email,phoneNumber,baNumber,verId;
    private TextView countDown,resend;
    private FirebaseAuth auth;
    private Pinview otp;
    private ProgressDialog dial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        confirm = findViewById(R.id.confirm);
        countDown = findViewById(R.id.countDown);
        resend = findViewById(R.id.resend);
        otp = findViewById(R.id.otp);
        dial = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();

        resend.setClickable(false);
        email = getIntent().getStringExtra("email");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        baNumber = getIntent().getStringExtra("ba");
        if(phoneNumber.length()==11)phoneNumber="+88"+phoneNumber;

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                countDown.setText("Expires in 0:" + millisUntilFinished / 1000+"s");
            }

            public void onFinish() {
                countDown.setText("");
                resend.setText("Resend");
                resend.setClickable(true);
            }
        }.start();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!validateOtp())
                {
                    Toasty.error(OtpActivity.this,"Invalid Otp",Toasty.LENGTH_SHORT).show();
                    return;
                }
                if(verId == null)
                {

                    Toasty.error(OtpActivity.this,"Verification Id cannot be null",Toasty.LENGTH_SHORT).show();
                    return;
                }
                dial.setMessage("Verifying...");
                dial.show();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verId, otp.getValue().toString());
                signInWithPhoneAuthCredential(credential);

            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resend.setClickable(false);
                resend.setText("");
                new CountDownTimer(60000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        countDown.setText("Expires in 0:" + millisUntilFinished / 1000+"s");
                    }

                    public void onFinish() {
                        countDown.setText("");
                        resend.setText("Resend");
                        resend.setClickable(true);
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phoneNumber,        // Phone number to verify
                                60,                 // Timeout duration
                                TimeUnit.SECONDS,   // Unit of timeout
                                OtpActivity.this,               // Activity (for callback binding)
                                mCallbacks);        // OnVerificationStateChangedCallbacks
                    }
                }.start();
            }
        });
    }

    private boolean validateOtp() {
        String val = otp.getValue().toString();
        String pattern = "([0-9]{6})";

        if(val.isEmpty())
        {
            return false;
        }
        if(!val.matches(pattern))
        {
            return false;
        }
        else {
            return true;
        }
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
    {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {

            // Toast.makeText(Login.this,"sent",Toast.LENGTH_SHORT).show();

//            if(credential.getSmsCode()!=null) {
//                otp.setValue(String.valueOf(credential.getSmsCode()));
//                dial.setMessage("Please Wait...");
//                dial.show();
//                signInWithPhoneAuthCredential(credential);
//            }
//            else {
//                dial.setMessage("Verifying...");
//                dial.show();
//                signInWithPhoneAuthCredential(credential);
//            }


        }


        @Override
        public void onVerificationFailed(FirebaseException e) {


            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toast.makeText(OtpActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(OtpActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                // ...
            }


        }



        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            verId=verificationId;



        }
    };

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            dial.dismiss();
//                            Toast.makeText(OtpActivity.this, "Successful", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(OtpActivity.this,NewPasswordActivity.class);
                            intent.putExtra("email",email);
                            intent.putExtra("ba",baNumber);
                            intent.putExtra("phoneNumber",phoneNumber);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                dial.dismiss();
                                AlertDialog.Builder builder=new AlertDialog.Builder(OtpActivity.this);
                                builder.setCancelable(true);
                                builder.setIcon(R.drawable.ic_baseline_error_outline_24);
                                builder.setTitle("Validation Error");
                                builder.setMessage("Otp do not match");
                                builder.setInverseBackgroundForced(true);
                                builder.setPositiveButton("Close",new DialogInterface.OnClickListener(){

                                    @Override
                                    public void onClick(DialogInterface dialog, int which){
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alert=builder.create();
                                alert.show();
                                // The verification code entered was invalid
//                                Toast.makeText(OtpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
    }
}