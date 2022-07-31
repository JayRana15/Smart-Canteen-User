package com.example.smartcanteen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.prefs.Preferences;

import Adapter.OrderAdapter;
import Controller.Controller;
import ResponseModel.OrderResponseModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class pOrderActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView textView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_porder);

        Toolbar toolbar = findViewById(R.id.toolbar3);
        toolbar.setNavigationIcon(getDrawable(R.drawable.ic_baseline_arrow_back_24));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        textView = findViewById(R.id.tv2);
        progressBar = findViewById(R.id.pgbar3);

        recyclerView = findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int uid = sp.getInt("user_id",100);


        displayOrderData(uid);

        SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshLayout1);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayOrderData(uid);
                refreshLayout.setRefreshing(false);
            }
        });

    }

    public void displayOrderData(int user_id) {

        Call<List<OrderResponseModel>> call = Controller.getInstance().getAPI().getPreviousOrder(user_id);

        progressBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<List<OrderResponseModel>>() {
            @Override
            public void onResponse(Call<List<OrderResponseModel>> call, Response<List<OrderResponseModel>> response) {
                List<OrderResponseModel> obj = response.body();
                progressBar.setVisibility(View.INVISIBLE);
                OrderAdapter adapter = new OrderAdapter(obj);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<OrderResponseModel>> call, Throwable t) {
//                Toast.makeText(pOrderActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.VISIBLE);
                Log.d("error",t.getMessage());
            }
        });
    }


}