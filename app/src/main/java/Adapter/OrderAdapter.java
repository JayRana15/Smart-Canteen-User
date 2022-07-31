package Adapter;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcanteen.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import ResponseModel.OrderResponseModel;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.orderViewHolder> {

    List<OrderResponseModel> data;

    public OrderAdapter(List<OrderResponseModel> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public orderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_row,parent,false);
        return new orderViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull orderViewHolder holder, int position) {
        holder.orderDetails.setText(data.get(position).getItems_name());
        holder.transactionID.setText("Transaction ID: "+data.get(position).getTxn_id());
        holder.amountTV.setText(data.get(position).getAmount()+"₹");
        holder.date.setText(formatter(data.get(position).getDate()));
        holder.orderId.setText( "Order ID: "+ data.get(position).getOrder_id());

        if (data.get(position).getIsReady().equals("0")){
            holder.status.setText("Order is being cooked");
        } else {
            holder.status.setText("Order is ready to pick");
        }

        if (!data.get(position).getPicked().equals("0") && !data.get(position).getIsReady().equals("0")){
            holder.status.setText("Order is picked");
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    String formatter(String date){
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yy hh:mm");
        String newDate = "";

        try {
            newDate = myFormat.format(fromUser.parse(date));
        } catch (ParseException e) {
            Log.e("dateError",e.getMessage());
        }

        return newDate;
    }


    class orderViewHolder extends RecyclerView.ViewHolder {
        TextView orderDetails,transactionID,amountTV,date,status,orderId;

        public orderViewHolder(@NonNull View itemView) {
            super(itemView);

            orderDetails = itemView.findViewById(R.id.orderDetails);
            transactionID = itemView.findViewById(R.id.transactionID);
            amountTV = itemView.findViewById(R.id.amountTV);
            date = itemView.findViewById(R.id.date);
            status = itemView.findViewById(R.id.status);
            orderId = itemView.findViewById(R.id.orderIdTV);

        }
    }

}
