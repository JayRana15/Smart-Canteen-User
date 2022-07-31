package Adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcanteen.R;

import java.util.List;
import java.util.StringJoiner;

import ResponseModel.MenuResponseModel;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.myViewHolder> {

    List<MenuResponseModel> data;
    TextView textView;



    public MenuAdapter(List<MenuResponseModel> data, TextView textView) {
        this.data = data;
        this.textView = textView;
    }



    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.itemName.setText(data.get(position).getItem_name());
        holder.itemPrice.setText(data.get(position).getItem_price()+"₹");
        holder.itemQnty.setText(String.valueOf(data.get(position).getItem_quantity()));

        holder.increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qnt = data.get(position).getItem_quantity();
                qnt++;
                data.get(position).setItem_quantity(qnt);
                notifyDataSetChanged();
                Log.d("incr","incr");
                updatePrice();
            }
        });

        holder.decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int qnt = data.get(position).getItem_quantity();
                if (qnt != 0) {
                    qnt--;
                    data.get(position).setItem_quantity(qnt);
                    Log.d("dcr","dcr");
                    notifyDataSetChanged();
                    updatePrice();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView itemName,itemPrice,itemQnty,increment,decrement;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemQnty = itemView.findViewById(R.id.itemQnty);
            increment = itemView.findViewById(R.id.increment);
            decrement = itemView.findViewById(R.id.decrement);
        }
    }

    public void updatePrice() {
        int sum = 0;
        for (int i = 0; i < data.size();i++){
            sum = sum + (Integer.parseInt(data.get(i).getItem_price()) *  data.get(i).getItem_quantity());
        }

        textView.setText("Total Amount: "+ sum + "₹");

    }

}
