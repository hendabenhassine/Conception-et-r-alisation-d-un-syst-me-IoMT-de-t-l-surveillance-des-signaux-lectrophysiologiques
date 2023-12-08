package com.example.neurosight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Random;

public class ResetPassword extends AppCompatActivity {

    private EditText mEmailEditText;
    private Button mResetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mEmailEditText = findViewById(R.id.editText_reset_email);
        mResetButton = findViewById(R.id.button_reset_password);

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailEditText.getText().toString().trim();
                if (email.isEmpty()) {
                    mEmailEditText.setError("Veuillez saisir un e-mail");
                    mEmailEditText.requestFocus();
                    return;
                }

                // Generate a random 6-digit code
                Random random = new Random();
                int code = random.nextInt(900000) + 100000; // generate a random number between 100000 and 999999

                // Send the code to the user's email
                // Code to send email goes here

                // Start the ResetPasswordCodeActivity and pass the generated code as an intent extra
                Intent intent = new Intent(ResetPassword.this, ResetPasswordCode.class);
                intent.putExtra("code", code);
                startActivity(intent);

                // Close the ResetPasswordActivity
                finish();
            }
        });}}
