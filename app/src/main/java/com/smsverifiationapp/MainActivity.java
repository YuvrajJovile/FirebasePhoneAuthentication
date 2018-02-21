package com.smsverifiationapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText etPhoneNumber, etOtp;
    private Button btSendOtp, btVerify, btResend, btSignOut, btLoginStatus;
    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String verificationCode = null;
    private String numberString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etPhoneNumber = (EditText) findViewById(R.id.et_ph_number);
        etOtp = (EditText) findViewById(R.id.et_otp);
        btSendOtp = (Button) findViewById(R.id.bt_send_otp);
        btVerify = (Button) findViewById(R.id.bt_verify_otp);
        btResend = (Button) findViewById(R.id.bt_resend);
        btSignOut = (Button) findViewById(R.id.bt_signout);
        btLoginStatus = (Button) findViewById(R.id.bt_status);

        btSendOtp.setOnClickListener(this);
        btVerify.setOnClickListener(this);
        btResend.setOnClickListener(this);
        btSignOut.setOnClickListener(this);
        btLoginStatus.setOnClickListener(this);

        init();
    }

    private void init() {

        auth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(MainActivity.this, "Code Sent..", Toast.LENGTH_SHORT).show();
            }
        };

        auth.addAuthStateListener(authStateListener);

    }

    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            if (firebaseAuth.getCurrentUser() != null) {
                Toast.makeText(MainActivity.this, "Logged in", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();

            }
        }
    };


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_verify_otp:
                verifyPhoneNumberFunc();
                break;
            case R.id.bt_send_otp:
                getOtpFunc();
                break;
            case R.id.bt_resend:
                getOtpFunc();
                break;
            case R.id.bt_signout:
                signOutFunc();
                break;
            case R.id.bt_status:
                loginStatusFunc();
                break;
        }
    }

    private void loginStatusFunc() {
        if(auth.getCurrentUser()!=null){
            Toast.makeText(this, "User is active", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "User is Inactive", Toast.LENGTH_SHORT).show();

        }
    }

    private void signOutFunc() {
        FirebaseAuth.getInstance().signOut();
    }

    private void verifyPhoneNumberFunc() {
        String inputCode = etOtp.getText().toString().trim();
        if (inputCode != null && verificationCode != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, inputCode);
            signInWithPhoneNumberFunc(credential);
        }
    }

    private void getOtpFunc() {

        numberString = etPhoneNumber.getText().toString().trim();
        if (numberString != null) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + numberString, 120, TimeUnit.SECONDS, this, mCallbacks);
        }
    }


    private void signInWithPhoneNumberFunc(PhoneAuthCredential credentials) {
        auth.signInWithCredential(credentials).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (task.isSuccessful()) {
                            //success
                            Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        } else {
                            //failure
                            Toast.makeText(MainActivity.this, "Login Failure", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        );
    }


}
