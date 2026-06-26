package com.example.mybookcatalog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextInputEditText editTextEmail = findViewById(R.id.editTextEmail);
        TextInputEditText editTextPassword = findViewById(R.id.editTextPassword);
        MaterialButton buttonLogin = findViewById(R.id.buttonLogin);
        TextView textViewSignUp = findViewById(R.id.textViewSignUp);
        TextView textViewForgotPassword = findViewById(R.id.textViewForgotPassword);

        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                if (SessionManager.login(email, password)) {
                    User user = SessionManager.getCurrentUser();
                    Toast.makeText(this, "Welcome back, " + user.getName(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });

        textViewForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });
    }
}