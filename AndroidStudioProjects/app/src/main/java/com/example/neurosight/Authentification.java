package com.example.neurosight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Authentification extends AppCompatActivity {


    // Déclaration des vues
    EditText emailEditText;
    EditText passwordEditText;
    Button loginButton;
    Button createAccountButton;
    Button forgotPasswordButton;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentification);

        // Référencer les vues
        emailEditText = findViewById(R.id.email_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        loginButton = findViewById(R.id.login_button);
        createAccountButton = findViewById(R.id.register_button);
        forgotPasswordButton = findViewById(R.id.forgot_password_button);
        databaseHelper = new DatabaseHelper(this);
        ToggleButton passwordToggleButton = findViewById(R.id.password_toggle_button);

// Référencer la vue pour le bouton Afficher/Cacher


// Ajouter un écouteur de clic pour le bouton Afficher/Cacher


        // Ajouter un écouteur de clic au bouton de connexion
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupérer les valeurs des champs de texte
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Vérifier si les champs sont vides
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Authentification.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                } else {
                    // Vérifier si l'adresse e-mail et le mot de passe sont valides
                    if (isValidEmail(email) && isValidPassword(password)) {
                        // Vérifier si les informations d'identification sont présentes dans la base de données
                        if (databaseHelper.checkUser( getApplicationContext(), email, password)) {
                            // Authentification réussie, passer à l'interface d'accueil
                            Intent intent = new Intent(Authentification.this,Acceuil.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Authentification.this, "Adresse e-mail ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Authentification.this, "Adresse e-mail ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        passwordToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Si le bouton est coché, afficher le mot de passe
                if (isChecked) {
                    passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                // Sinon, masquer le mot de passe
                else {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        // Ajouter un écouteur de clic au bouton de création de compte
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer une nouvelle activité pour créer un compte
                Intent intent = new Intent(Authentification.this, CreateAccount.class);
                startActivity(intent);
            }
        });

        // Ajouter un écouteur de clic au bouton de réinitialisation de mot de passe
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer une nouvelle activité pour réinitialiser le mot de passe
                Intent intent = new Intent(Authentification.this, ResetPassword.class);
                startActivity(intent);
            }
        });
    }

    // Vérifier si l'adresse e-mail est valide en utilisant la classe Patterns de Android
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Vérifier si le mot de passe est valide (au moins 6 caractères)
    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

}