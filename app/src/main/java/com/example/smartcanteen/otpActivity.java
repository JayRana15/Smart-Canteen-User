package com.example.smartcanteen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.TimeUnit;

import Controller.Controller;
import ResponseModel.ResponseModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class otpActivity extends AppCompatActivity {

    Button verify;
    EditText otpEt;
    ProgressBar pgBar;
    String OTPbySystem,username,number,token;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        username = getIntent().getStringExtra("username");
        number = getIntent().getStringExtra("number");

        Log.d("name",username);
        Log.d("number",number);

        verify = findViewById(R.id.verifyBTN);
        otpEt = findViewById(R.id.OTPbyUser);
        pgBar = findViewById(R.id.pgbar);
        pgBar.setVisibility(View.INVISIBLE);
        toolbar = findViewById(R.id.toolbar1);

        sendVerificationCode(number);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = otpEt.getText().toString();
                if (code.isEmpty() || code.length() < 6 ){
                    otpEt.setError("wrong OTP");
                    return;
                }

                pgBar.setVisibility(View.VISIBLE);
                verifyCode(code);

            }
        });

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
            toolbar.setSubtitle("OTP sent");
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                pgBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(otpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
//                    String token1 = getToken();
//                    createUser(username,number,token1);
                    newUser(username,number);

                } else {
                    Log.e("error2",task.getException().getMessage());
                }
            }
        });
    }

    private void newUser(String username, String number) {
        Call<ResponseModel> call = Controller.getInstance().getAPI().newUser(number,username);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                ResponseModel obj = response.body();
                int temp = obj.getUser_id();

                if(temp != 100) {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    sp.edit().putInt("user_id",temp).apply();

                    startActivity(new Intent(otpActivity.this, dashBoardActivity.class));
                    finish();
                } else {
                    Log.e("newError",String.valueOf(temp));
                    Toast.makeText(otpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.e("NewResError",t.getMessage());
                Toast.makeText(otpActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private String getToken() {
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (task.isSuccessful()){
//                            token = task.getResult();
//                            Log.d("token",token);
//                        }
//                    }
//                });
//
//        return token;
//    }

    void createUser(String user_name,String phone_number,String firebase_token){
        Call<ResponseModel>call = Controller.getInstance().getAPI().createUser(user_name,phone_number,firebase_token);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                ResponseModel obj = response.body();
                int temp = obj.getUser_id();
//                int temp2 = Integer.parseInt(temp);

                if (temp == 100 ){

                    Log.d("error3","user not created");
                    Toast.makeText(otpActivity.this, "user not created", Toast.LENGTH_SHORT).show();

                } else if (temp == 99){

                    Log.d("TokenUpdateError","token not updated in database");
                    Toast.makeText(otpActivity.this, "token not updated in database", Toast.LENGTH_SHORT).show();

                } else {

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    sp.edit().putString("username",user_name)
                            .putString("number",phone_number)
                            .putString("user_id",String.valueOf(temp))
//                            .putString("token",firebase_token)
                            .apply();

                    startActivity(new Intent(otpActivity.this, dashBoardActivity.class));
                    finish();

                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.e("error4",t.getMessage());
                Toast.makeText(otpActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}