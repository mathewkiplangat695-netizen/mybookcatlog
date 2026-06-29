package com.example.mybookcatalog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookList;
    private OnBookClickListener listener;
    private boolean showAddToCart = true;

    public interface OnBookClickListener {
        void onBookClick(Book book);
        void onAddToCartClick(Book book);
    }

    public BookAdapter(List<Book> bookList, OnBookClickListener listener) {
        this.bookList = bookList;
        this.listener = listener;
    }

    public void setShowAddToCart(boolean show) {
        this.showAddToCart = show;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.textViewBookTitle.setText(book.getTitle());
        holder.textViewBookAuthor.setText(book.getAuthor());
        holder.textViewBookGenre.setText(book.getGenre());
        holder.textViewBookRating.setText(String.valueOf(book.getRating()));
        
        // Price removed from view
        holder.textViewBookPrice.setVisibility(View.GONE);
        
        if (book.getImageResId() != 0) {
            holder.imageViewBookCover.setImageResource(book.getImageResId());
            holder.imageViewBookCover.setImageTintList(null); // Remove placeholder tint
        } else {
            holder.imageViewBookCover.setImageResource(android.R.drawable.ic_menu_agenda);
        }
        
        holder.itemView.setOnClickListener(v -> listener.onBookClick(book));
        
        if (showAddToCart) {
            holder.buttonAddToCart.setVisibility(View.VISIBLE);
            holder.buttonAddToCart.setOnClickListener(v -> listener.onAddToCartClick(book));
        } else {
            holder.buttonAddToCart.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void updateList(List<Book> newList) {
        this.bookList = newList;
        notifyDataSetChanged();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBookTitle, textViewBookAuthor, textViewBookGenre, textViewBookPrice, textViewBookRating;
        ImageView imageViewBookCover;
        Button buttonAddToCart;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBookTitle = itemView.findViewById(R.id.textViewBookTitle);
            textViewBookAuthor = itemView.findViewById(R.id.textViewBookAuthor);
            textViewBookGenre = itemView.findViewById(R.id.textViewBookGenre);
            textViewBookPrice = itemView.findViewById(R.id.textViewBookPrice);
            textViewBookRating = itemView.findViewById(R.id.textViewBookRating);
            imageViewBookCover = itemView.findViewById(R.id.imageViewBookCover);
            buttonAddToCart = itemView.findViewById(R.id.buttonAddToCart);
        }
    }
}