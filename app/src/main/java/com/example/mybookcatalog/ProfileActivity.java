package com.example.mybookcatalog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView textViewUserName = findViewById(R.id.textViewUserName);
        TextView textViewUserEmail = findViewById(R.id.textViewUserEmail);
        TextView menuOrderHistory = findViewById(R.id.menuOrderHistory);
        TextView menuSettings = findViewById(R.id.menuSettings);

        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            textViewUserName.setText(currentUser.getName());
            textViewUserEmail.setText(currentUser.getEmail());
        }

        menuOrderHistory.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, OrderHistoryActivity.class));
        });

        menuSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Settings feature coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        findViewById(R.id.toolbarProfile).setOnClickListener(v -> finish());
    }
}
