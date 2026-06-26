package com.example.mybookcatalog;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

public class BookDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Book book = (Book) getIntent().getSerializableExtra("book");

        if (book != null) {
            TextView title = findViewById(R.id.textViewDetailTitle);
            TextView author = findViewById(R.id.textViewDetailAuthor);
            TextView rating = findViewById(R.id.textViewDetailRating);
            TextView genre = findViewById(R.id.textViewDetailGenre);
            TextView price = findViewById(R.id.textViewDetailPrice);
            TextView description = findViewById(R.id.textViewDetailDescription);
            ImageView cover = findViewById(R.id.imageViewDetailCover);
            Button addToCartButton = findViewById(R.id.buttonBuy);

            title.setText(book.getTitle());
            author.setText("by " + book.getAuthor());
            rating.setText(String.valueOf(book.getRating()));
            genre.setText(book.getGenre());
            price.setText(String.format(Locale.getDefault(), "Price: KES %.2f", book.getPrice()));
            description.setText(book.getDescription());

            if (book.getImageResId() != 0) {
                cover.setImageResource(book.getImageResId());
                cover.setImageTintList(null); // Remove placeholder tint
            }

            addToCartButton.setOnClickListener(v -> {
                CartManager.addBook(book);
                Toast.makeText(this, book.getTitle() + " added to cart!", Toast.LENGTH_SHORT).show();
            });
        }
    }
}