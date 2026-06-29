package com.example.mybookcatalog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private final List<Book> bannerBooks;

    public BannerAdapter(List<Book> bannerBooks) {
        this.bannerBooks = bannerBooks;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_item, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Book book = bannerBooks.get(position);
        holder.textViewBannerTitle.setText(book.getTitle());
        if (book.getImageResId() != 0) {
            holder.imageViewBanner.setImageResource(book.getImageResId());
        }
    }

    @Override
    public int getItemCount() {
        return bannerBooks.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewBanner;
        TextView textViewBannerTitle;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewBanner = itemView.findViewById(R.id.imageViewBanner);
            textViewBannerTitle = itemView.findViewById(R.id.textViewBannerTitle);
        }
    }
}