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
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import Controller.Controller;
import ResponseModel.ResponseModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class logInActivity extends AppCompatActivity {

    EditText usernameET,numberET;
    TextInputLayout til0username,til1phoneNumber;
    Button OTPButton;
    String username = "",phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        usernameET = findViewById(R.id.username);
        til0username = findViewById(R.id.til0);

        numberET = findViewById(R.id.phoneNumber);
        til1phoneNumber = findViewById(R.id.til1);

        OTPButton = findViewById(R.id.OTPButton);

        OTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameET.getText().toString();
                phoneNumber = numberET.getText().toString();
                boolean unValidate = false,pnValidate = false;

                if (!username.equals("")){
                    unValidate = true;
                    til0username.setErrorEnabled(false);

                } else {
                    til0username.setError("Enter Username");
                }

                if (phoneNumber.length() == 10){
                    til1phoneNumber.setErrorEnabled(false);
                    pnValidate = true;
                } else {
                    til1phoneNumber.setError("Enter 10 digit number");
                }

                Log.d("username",username);
                Log.d("number",phoneNumber);

                if (unValidate && pnValidate){
//                    Intent intent = new Intent(logInActivity.this,otpActivity.class);
//                    intent.putExtra("username",username);
//                    intent.putExtra("number",phoneNumber);
//                    startActivity(intent);
//                    finish();
                    getData(phoneNumber);
                }

            }
        });

    }

    private void getData(String phoneNumber) {
        Call<ResponseModel> call = Controller.getInstance().getAPI().checkUser(phoneNumber);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, @NonNull Response<ResponseModel> response) {
                ResponseModel obj = response.body();
                int temp = obj.getUser_id();
                if (temp != 100) {

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    sp.edit().putInt("user_id",temp).apply();

                    Log.d("dashBoardActivity","start");

                    startActivity(new Intent(logInActivity.this,dashBoardActivity.class));
                    finish();
                } else {

                    Log.d("OtpActivity","start");

                    Intent intent = new Intent(logInActivity.this,otpActivity.class);
                    intent.putExtra("username",username);
                    intent.putExtra("number",phoneNumber);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.e("checkUserError",t.getMessage());
                Toast.makeText(logInActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}