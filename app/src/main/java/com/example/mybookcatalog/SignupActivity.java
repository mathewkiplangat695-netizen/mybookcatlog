package com.example.mybookcatalog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        TextInputEditText editTextName = findViewById(R.id.editTextName);
        TextInputEditText editTextEmail = findViewById(R.id.editTextEmailSignup);
        // Password field is removed from layout in next step
        MaterialButton buttonSignup = findViewById(R.id.buttonSignup);
        TextView textViewLoginLink = findViewById(R.id.textViewLoginLink);

        buttonSignup.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                if (SessionManager.register(name, email)) {
                    Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to login
                } else {
                    Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewLoginLink.setOnClickListener(v -> {
            finish(); // Go back to login
        });
    }
}