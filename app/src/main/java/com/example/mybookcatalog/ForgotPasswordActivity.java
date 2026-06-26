package com.example.mybookcatalog;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ImageView buttonBack = findViewById(R.id.buttonBack);
        TextInputEditText editTextEmail = findViewById(R.id.editTextForgotEmail);
        MaterialButton buttonSendReset = findViewById(R.id.buttonSendReset);

        buttonBack.setOnClickListener(v -> finish());

        buttonSendReset.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Reset link sent to " + email, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}