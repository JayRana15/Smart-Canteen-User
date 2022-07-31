package com.example.smartcanteen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.paytm.pg.merchant.PaytmChecksum;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Random;

import Adapter.MenuAdapter;
import Controller.Controller;
import Controller.ServiceWrapper;
import ResponseModel.MenuResponseModel;
import ResponseModel.ResponseModel;
import ResponseModel.TokenRes.Token_Res;
import okhttp3.Interceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity  { //implements PaymentResultListener

    RecyclerView recyclerView;
    TextView totalAmount,tv1;
    Button orderButton;
    ProgressBar pgbar;
    List<MenuResponseModel> obj;

    String lastOrderId ="",txnAmountString = "",order_string = "";


//    static final String midString = "KZNzcm86869687469112";
//    private Integer ActivityRequestCode = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = findViewById(R.id.toolbar4);
        toolbar.setNavigationIcon(getDrawable(R.drawable.ic_baseline_arrow_back_24));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        totalAmount = findViewById(R.id.totalAmountTV);
        tv1 = findViewById(R.id.tv1);
        pgbar = findViewById(R.id.pgbar2);
        orderButton = findViewById(R.id.newOrderButton);


        displayData();

        SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayData();
                refreshLayout.setRefreshing(false);
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (createOrderString()){
//                    lastOrderId();
//                    razorPayment();
//                    try {
//                        getToken();
//                    } catch (MalformedURLException e) {
//                        Log.d("getTokenError",e.getMessage());
//                    }

                    txnAmountString = totalAmount.getText().toString();
                    txnAmountString = txnAmountString.replaceAll("[^0-9]","");
                    int amount = Integer.parseInt(txnAmountString);
                    Log.d("userid",String.valueOf(amount));

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MenuActivity.this);
                    int uid = sp.getInt("user_id",00);
                    Log.d("userid",String.valueOf(uid));

                    Random rnd = new Random();
                    int number = rnd.nextInt(999999);

                    String temp =  String.format("%06d", number);

                    Log.d("txnid",temp);

                    createOrder(uid,order_string,amount,temp);

                }
            }
        });
    }

    private void createOrder(int uid, String orderString, int amount, String txn_id) {
        Call<ResponseModel> call = Controller.getInstance().getAPI().newOrder(uid, orderString, amount, txn_id);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                ResponseModel obj = response.body();
                String res = obj.getOrder();

                if (res.equals("successful")) {
                    Toast.makeText(MenuActivity.this, "Order successful", Toast.LENGTH_SHORT).show();
                    Log.d("order","successful");
                    startActivity(new Intent(MenuActivity.this,dashBoardActivity.class));
                } else {
                    Toast.makeText(MenuActivity.this, "Order unsuccessful", Toast.LENGTH_SHORT).show();
                    Log.d("order","unsuccessful");
                    startActivity(new Intent(MenuActivity.this,dashBoardActivity.class));
                }

            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.e("newOrderError",t.getMessage());
            }
        });
    }

    private void razorPayment() {
        txnAmountString = totalAmount.getText().toString();
        txnAmountString = txnAmountString.replaceAll("[^0-9]","");
        int amount = Math.round(Float.valueOf(txnAmountString) * 100);
        Log.d("txnAmountString",String.valueOf(amount));

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_BgAYrLSIvGZOrd");

        JSONObject object = new JSONObject();
        try {
            object.put("amount",amount);
            object.put("currency","INR");
//            object.put()
            checkout.open(MenuActivity.this,object);
        } catch (Exception e){
            Log.d("razorError",e.getMessage());
        }


    }

    public void displayData() {
        Call<List<MenuResponseModel>> call = Controller.getInstance().getAPI().getData();

        pgbar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<List<MenuResponseModel>>() {
            @Override
            public void onResponse(Call<List<MenuResponseModel>> call, Response<List<MenuResponseModel>> response) {
                obj = response.body();
                pgbar.setVisibility(View.INVISIBLE);
                MenuAdapter adapter = new MenuAdapter(obj,totalAmount);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<MenuResponseModel>> call, Throwable t) {

//                Toast.makeText(MenuActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

                pgbar.setVisibility(View.INVISIBLE);
                tv1.setVisibility(View.VISIBLE);
                Log.d("error",t.getMessage());
            }
        });
    }

    public boolean createOrderString() {
        String temp = totalAmount.getText().toString();
        int tempInt2 = -1;
        boolean temp5b = false;

        if (!temp.equals("Total Amount: 0₹")) {
            String temp1="null55";

            for (int i = 0; i < obj.size();i++){
                if (obj.get(i).getItem_quantity() != 0){
                    String temp3 = String.valueOf(obj.get(i).getItem_quantity());
                    String temp2 = obj.get(i).getItem_name();
                    temp1 = String.join("x ",temp3,temp2); //qntx name
                    Log.d("first",temp1);
                    tempInt2 = i;
                    temp5b = true;
                    break;
                }
            }

            if (!temp1.equals("null55")){
                for (int i = 0; i < obj.size();i++){
                    if (tempInt2 != i ){
                        if (obj.get(i).getItem_quantity() != 0) {

                            String temp5 = String.valueOf(obj.get(i).getItem_quantity());
                            String temp6 = obj.get(i).getItem_name();
                            String temp4 = String.join("x ", temp5, temp6);// qntx name

                            temp1 = String.join(", ", temp1, temp4); // (1(qntx name),2(qntx name))
                            temp5b = true;
                        }

                    } else {
                        temp5b = true;
                        continue;
                    }
                }
            }

            order_string = temp1;
            Log.d("orderString",temp1);
            return temp5b;
        } else
            return temp5b;

    }

    public void lastOrderId(){
        Call<ResponseModel> call = Controller.getInstance().getAPI().getLastOrderID();
        Log.d("method","start");
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                ResponseModel obj2 = response.body();
                Log.d("obj2","here");
                lastOrderId = String.valueOf(obj2.getLastOrderId() + 1);
                Log.d("last_order_id",lastOrderId);
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d("orderIDError",t.getMessage());
            }
        });
    }

//    @Override
//    public void onPaymentSuccess(String s) {
//        Toast.makeText(this, "Payment successful", Toast.LENGTH_SHORT).show();
//        Log.d("payment",s);
//        Log.d("payment","successful");
//        startActivity(new Intent(MenuActivity.this,dashBoardActivity.class));
//        finish();
//    }
//
//    @Override
//    public void onPaymentError(int i, String s) {
//        Toast.makeText(this, "Payment unsuccessful", Toast.LENGTH_SHORT).show();
//        Log.d("payment",s);
//        Log.d("int",String.valueOf(i));
//        Log.d("payment","unsuccessful");
//    }


    public void getToken() throws MalformedURLException {
        Log.d("getToken", " get token start");

        txnAmountString = totalAmount.getText().toString();
        txnAmountString = txnAmountString.replaceAll("[^0-9]","");
        Log.d("txnAmountString",txnAmountString);

//        Interceptor interceptor = new Interceptor() {
//            @NonNull
//            @Override
//            public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
//                return null;
//            }
//        };


//        ServiceWrapper serviceWrapper = new ServiceWrapper(null);
//        Call<Token_Res> call = serviceWrapper.getTokenCall("12345", midString, lastOrderId, txnAmountString);
//        call.enqueue(new Callback<Token_Res>() {
//            @Override
//            public void onResponse(Call<Token_Res> call, Response<Token_Res> response) {
//                Log.d("temp", " respo "+ response.isSuccessful() );
//                try {
//                    if (response.isSuccessful() && response.body()!=null){
//                        if (response.body().getBody().getTxnToken()!="") {
//                            Log.d( " transaction token : ",response.body().getBody().getTxnToken());
//                            startPaytmPayment(response.body().getBody().getTxnToken());
//                        }else {
//                            Log.d("error3", " Token status false");
//                        }
//                    }
//                } catch (Exception e){
//                    Log.d("error2", " error in Token Res "+e.getMessage());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Token_Res> call, Throwable t) {
//                Log.d("error1", " response error "+t.getMessage());
//            }
//        });

        JSONObject paytmParams = new JSONObject();

        JSONObject body = new JSONObject();
        try {

            body.put("requestType", "Payment");
            body.put("mid", "KZNzcm86869687469112");
            body.put("websiteName", "WEBSTAGING");
            body.put("orderId", lastOrderId);
            body.put("callbackUrl", "https://<callback URL to be used by merchant>");

        } catch (Exception e) {
            Log.d("jsonBodyError",e.getMessage());
        }

        JSONObject txnAmount = new JSONObject();
        try {
            txnAmount.put("value", "1.00");
            txnAmount.put("currency", "INR");
        } catch (Exception e){
            Log.d("txnAmountError",e.getMessage());
        }

        JSONObject userInfo = new JSONObject();
        try {
            userInfo.put("custId", "CUST_001");

            body.put("txnAmount", txnAmount);
            body.put("userInfo", userInfo);
        } catch (JSONException e) {
            Log.d("userInfoError",e.getMessage());
        }


        /*
         * Generate checksum by parameters we have in body
         * You can get Checksum JAR from https://developer.paytm.com/docs/checksum/
         * Find your Merchant Key in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys
         */

        String checksum = null;
        try {
            checksum = PaytmChecksum.generateSignature(body.toString(), "YOUR_MERCHANT_KEY");
        } catch (Exception e) {
            Log.d("checksumError",e.getMessage());
        }

        JSONObject head = new JSONObject();
        try {
            head.put("signature", checksum);

            paytmParams.put("body", body);
            paytmParams.put("head", head);

        } catch (Exception e){
            Log.d("headError",e.getMessage());
        }

        String post_data = paytmParams.toString();

        /* for Staging */
        URL url = new URL("https://securegw-stage.paytm.in/theia/api/v1/initiateTransaction?mid=YOUR_MID_HERE&orderId=ORDERID_98765");

        /* for Production */
// URL url = new URL("https://securegw.paytm.in/theia/api/v1/initiateTransaction?mid=YOUR_MID_HERE&orderId=ORDERID_98765");

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
            requestWriter.writeBytes(post_data);
            requestWriter.close();
            String responseData = "";
            InputStream is = connection.getInputStream();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
            if ((responseData = responseReader.readLine()) != null) {
                System.out.append("Response: " + responseData);
            }
            responseReader.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }


    }

    private void startPaytmPayment(String txnToken) {
    }

}