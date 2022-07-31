package com.example.smartcanteen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import Controller.Controller;
import ResponseModel.ResponseModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class changeNumberActivity extends AppCompatActivity {

    EditText changeNumberET,otpEt;
    Button cnBtn;
    ProgressBar pgbar;
    String newNumber = "",OTPbySystem;
    TextInputLayout til0,til1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_number);

        changeNumberET = findViewById(R.id.changeNumberET);
        otpEt = findViewById(R.id.otpET);
        cnBtn = findViewById(R.id.cnBtn);
        pgbar = findViewById(R.id.pgbar1);
        til0 = findViewById(R.id.til4);
        til1 = findViewById(R.id.til5);

        cnBtn.setTag(0);
        changeNumberET.setTag(0);

        pgbar.setVisibility(View.INVISIBLE);


        cnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp = (Integer) cnBtn.getTag();

                if(temp == 0) {
                    senOTP();
                    pgbar.setVisibility(View.VISIBLE);
                } else {
                    String code = otpEt.getText().toString();
                    if (code.isEmpty() || code.length() < 6 ){
                        otpEt.setError("wrong OTP");
                        return;
                    } else {
                        verifyCode(code);
                    }
                }
            }
        });

    }

    void senOTP(){
        newNumber = changeNumberET.getText().toString();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String oldnum = sp.getString("number","");
        //&& !newNumber.equals(oldnum)
        if (!newNumber.equals("") && !newNumber.equals(oldnum) ){
            sendVerificationCode(newNumber);
        }
    }

    void sendVerificationCode(String phone_number) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + phone_number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            OTPbySystem = s;
            til0.setVisibility(View.INVISIBLE);
            til1.setVisibility(View.VISIBLE);
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {

                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(changeNumberActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("error",e.getMessage());
        }
    };

    void verifyCode(String verificationCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(OTPbySystem,verificationCode);
        signInUser(credential);
    }

    void signInUser(PhoneAuthCredential credential){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    int uid = sp.getInt("user_id",100);

                    updateNumber(uid,newNumber);

                } else {
                    Log.d("error2",task.getException().getMessage());
                }
            }
        });
    }

    void updateNumber(int uid,String new_number) {

        Call<ResponseModel> call = Controller.getInstance().getAPI().updateNumber(uid, new_number);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                ResponseModel obj = response.body();
                String temp = obj.getNewNumber();
                if (temp.equals("updated")){
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    sp.edit().putString("number",new_number).apply();
                    Toast.makeText(changeNumberActivity.this, "Number Updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(changeNumberActivity.this,dashBoardActivity.class));
                } else {
                    Log.d("newNumber","not updated");
                    Toast.makeText(changeNumberActivity.this, "Number not updated", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d("error3",t.getMessage());
            }
        });

    }

}