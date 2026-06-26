package com.example.mybookcatalog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onQuantityChanged();
        void onItemRemoved();
    }

    public CartAdapter(List<CartItem> cartItems, OnCartChangeListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        Book book = item.getBook();

        holder.textViewCartTitle.setText(book.getTitle());
        holder.textViewCartPrice.setText(String.format(Locale.getDefault(), "KES %.2f", book.getPrice()));
        holder.textViewQuantity.setText(String.valueOf(item.getQuantity()));

        holder.buttonPlus.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                CartItem currentItem = cartItems.get(currentPos);
                CartManager.updateQuantity(currentItem.getBook(), currentItem.getQuantity() + 1);
                notifyItemChanged(currentPos);
                listener.onQuantityChanged();
            }
        });

        holder.buttonMinus.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                CartItem currentItem = cartItems.get(currentPos);
                if (currentItem.getQuantity() > 1) {
                    CartManager.updateQuantity(currentItem.getBook(), currentItem.getQuantity() - 1);
                    notifyItemChanged(currentPos);
                    listener.onQuantityChanged();
                }
            }
        });

        holder.buttonRemove.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                CartItem currentItem = cartItems.get(currentPos);
                CartManager.removeBook(currentItem.getBook());
                notifyItemRemoved(currentPos);
                // No need for notifyItemRangeChanged as it can cause flickering if handled by the parent
                listener.onItemRemoved();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCartTitle, textViewCartPrice, textViewQuantity;
        MaterialButton buttonMinus, buttonPlus;
        ImageView buttonRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCartTitle = itemView.findViewById(R.id.textViewCartTitle);
            textViewCartPrice = itemView.findViewById(R.id.textViewCartPrice);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            buttonMinus = itemView.findViewById(R.id.buttonMinus);
            buttonPlus = itemView.findViewById(R.id.buttonPlus);
            buttonRemove = itemView.findViewById(R.id.buttonRemove);
        }
    }
}