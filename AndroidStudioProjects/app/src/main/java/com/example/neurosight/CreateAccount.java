package com.example.neurosight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class CreateAccount extends AppCompatActivity {

    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText emailEditText;
    EditText passwordEditText;
    EditText confirmPasswordEditText;
    Button createAccountButton;
    Button loginButton ;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Référencer les vues
        firstNameEditText = findViewById(R.id.first_name_edittext);
        lastNameEditText = findViewById(R.id.last_name_edittext);
        emailEditText = findViewById(R.id.email_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edittext);
        createAccountButton = findViewById(R.id.create_account_button);
        ToggleButton passwordToggleButton1 = findViewById(R.id.password_toggle_button1);
        ToggleButton confirmPasswordToggleButton = findViewById(R.id.confirm_password_toggle_button);
        View loginButton = findViewById(R.id.login_button);

        // Créer l'objet DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Ajouter un écouteur de clic au bouton de création de compte
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupérer les valeurs des champs de texte
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                // Vérifier si les champs sont vides
                if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(CreateAccount.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                } else {
                    // Vérifier si l'adresse e-mail et le mot de passe sont valides
                    if (isValidEmail(email) && isValidPassword(password) && passwordsMatch(password, confirmPassword)) {
                        // Enregistrer les coordonnées de l'utilisateur dans la base de données
                        SQLiteDatabase db = databaseHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(DatabaseContract.User.COLUMN_NAME_FIRST_NAME, firstName);
                        values.put(DatabaseContract.User.COLUMN_NAME_LAST_NAME, lastName);
                        values.put(DatabaseContract.User.COLUMN_NAME_EMAIL, email);
                        values.put(DatabaseContract.User.COLUMN_NAME_PASSWORD, password);
                        long newRowId = db.insert(DatabaseContract.User.TABLE_NAME, null, values);

                        Toast.makeText(CreateAccount.this, "Compte créé avec succès", Toast.LENGTH_SHORT).show();
                        // Fin de l'activité de création de compte
                        finish();
                    } else {
                        Toast.makeText(CreateAccount.this, "Veuillez entrer des informations valides", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer une nouvelle activité pour visualiser les signaux électrophysiologiques
                Intent intent = new Intent(CreateAccount.this, Authentification.class);
                startActivity(intent);
            }
        });

        passwordToggleButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        confirmPasswordToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Si le bouton est coché, afficher le mot de passe
                if (isChecked) {
                    confirmPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                // Sinon, masquer le mot de passe
                else {
                    confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    // Vérifier si l'adresse e-mail est valide en utilisant la classe Patterns de Android
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Vérifier si le mot de passe est valide (par exemple, s'il contient au moins 6 caractères)
    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    // Vérifier si le mot de passe et la confirmation correspondent
    private boolean passwordsMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

}
