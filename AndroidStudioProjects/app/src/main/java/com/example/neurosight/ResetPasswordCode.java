package com.example.neurosight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ResetPasswordCode extends AppCompatActivity {
    private EditText mCodeEditText;
    private EditText mNewPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private Button mResetButton;

    private int mGeneratedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_code);

        mCodeEditText = findViewById(R.id.editText_code);
        mNewPasswordEditText = findViewById(R.id.editText_new_password);
        mConfirmPasswordEditText = findViewById(R.id.editText_confirm_password);
        mResetButton = findViewById(R.id.button_reset_password);

        // Get the generated code from the intent extra
        mGeneratedCode = getIntent().getIntExtra("code", 0);

        // Display the generated code in a TextView or EditText so the user can enter it
        mCodeEditText.setText(String.valueOf(mGeneratedCode));

        // Get the email from the intent extra
        String email = getIntent().getStringExtra("email");

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codeEntered = mCodeEditText.getText().toString().trim();
                String newPassword = mNewPasswordEditText.getText().toString().trim();
                String confirmPassword = mConfirmPasswordEditText.getText().toString().trim();

                if (codeEntered.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    // Show an error message if any of the fields are empty
                    Toast.makeText(ResetPasswordCode.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the code entered by the user matches the generated code
                if (Integer.parseInt(codeEntered) != mGeneratedCode) {
                    Toast.makeText(ResetPasswordCode.this, "Le code entré est incorrect", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the new password and confirm password match
                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(ResetPasswordCode.this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update the password in the database
                DatabaseHelper dbHelper = new DatabaseHelper(ResetPasswordCode.this);
                dbHelper.updateUserPassword(email, newPassword);

                // Show a success message and return to the login screen
                Toast.makeText(ResetPasswordCode.this, "Le mot de passe a été réinitialisé avec succès", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ResetPasswordCode.this,Authentification.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}
