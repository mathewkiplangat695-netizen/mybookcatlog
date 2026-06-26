package com.example.mybookcatalog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.textViewOrderId.setText("Order #" + order.getOrderId().substring(Math.max(0, order.getOrderId().length() - 8)));
        holder.textViewOrderDate.setText(order.getDate());
        holder.textViewOrderTotal.setText(String.format(Locale.getDefault(), "KES %.2f", order.getTotalAmount()));

        StringBuilder items = new StringBuilder();
        for (int i = 0; i < order.getItems().size(); i++) {
            items.append(order.getItems().get(i).getBook().getTitle());
            if (i < order.getItems().size() - 1) items.append(", ");
        }
        holder.textViewOrderItems.setText(items.toString());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewOrderId, textViewOrderDate, textViewOrderItems, textViewOrderTotal;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOrderId = itemView.findViewById(R.id.textViewOrderId);
            textViewOrderDate = itemView.findViewById(R.id.textViewOrderDate);
            textViewOrderItems = itemView.findViewById(R.id.textViewOrderItems);
            textViewOrderTotal = itemView.findViewById(R.id.textViewOrderTotal);
        }
    }
}