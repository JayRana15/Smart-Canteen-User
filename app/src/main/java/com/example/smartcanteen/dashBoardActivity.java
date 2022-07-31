package com.example.smartcanteen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import Controller.Controller;
import ResponseModel.ResponseModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class dashBoardActivity extends AppCompatActivity {

    TextView total_spent,orders,changeNumbers,menu;
    public static final String CHANNEL_ID = "7554";
    String token = "123";
    boolean hasProcessed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        total_spent = findViewById(R.id.total_spent);
        orders = findViewById(R.id.ordersButton);
        changeNumbers = findViewById(R.id.changeNumber);
        menu = findViewById(R.id.menu);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int uid = sp.getInt("user_id",99);
        Log.d("uid",String.valueOf(uid));

//        String token = sp.getString("token","123");
//        Log.d("tokenDashboard",token);

        createNotificationChannel();

        displayTotalSpent(uid);

        if (!hasProcessed) {
            getToken();
        }

        saveTokenToDB();

        menu.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 startActivity(new Intent(dashBoardActivity.this,MenuActivity.class));
             }
         });

        orders.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 startActivity(new Intent(dashBoardActivity.this,pOrderActivity.class));
             }
         });

        changeNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(dashBoardActivity.this,changeNumberActivity.class));
            }
        });

    }

    void getToken(){

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()){
                            token = task.getResult();
                            Log.d("token", token);

                            hasProcessed = true;

                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            sp.edit().putString("token1",token).apply();
                        }
                    }
                });
    }

    private void saveTokenToDB() {

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            int id = sp.getInt("user_id",99);
            String token1 = sp.getString("token1","1234");
            Log.d("Token",token1);

            if (id != 99 && !token1.equals("1234")) {
                Call<ResponseModel> call = Controller.getInstance().getAPI().updateToken(id,token1);

                call.enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        ResponseModel obj = response.body();
                        String temp = obj.getToken();
                        if (temp.equals("updated")){
                            Log.d("token","updated");
                        } else {
                            Log.d("token","notupdated");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        Log.e("TokenResError",t.getMessage());
                        Toast.makeText(dashBoardActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }


    }

    void displayTotalSpent(int user_id){
        Call<ResponseModel> call = Controller.getInstance().getAPI().totalSpent(user_id);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                ResponseModel obj = response.body();
                String amount = obj.getAmountSpent();
                if (amount == null){
                    total_spent.setText("0₹");
                } else {
                    total_spent.setText(amount+"₹");
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d("error",t.getMessage());
                Toast.makeText(dashBoardActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

//    @Override
//    public void onBackPressed() {
//        Intent a = new Intent(Intent.ACTION_MAIN);
//        a.addCategory(Intent.CATEGORY_HOME);
//        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(a);
//    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "OrderStatus";
            String description = "This channel is for to notify you when your order is ready";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    
}