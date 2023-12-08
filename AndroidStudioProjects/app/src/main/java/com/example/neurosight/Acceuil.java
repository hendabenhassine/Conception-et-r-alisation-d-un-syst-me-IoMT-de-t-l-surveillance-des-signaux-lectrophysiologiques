package com.example.neurosight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Acceuil extends AppCompatActivity {
    Button compteButton;
    Button visualisationButton;
    Button deconnexionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceuil);

        // Référencer les vues
        compteButton = findViewById(R.id.my_account_button);
        visualisationButton = findViewById(R.id.visualisation_button);
        deconnexionButton = findViewById(R.id.logout_button);

        // Ajouter un écouteur de clic au bouton de compte
        compteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer une nouvelle activité pour afficher les informations du compte
                Intent intent = new Intent(Acceuil.this, Compte.class);
                startActivity(intent);
            }
        });

        // Ajouter un écouteur de clic au bouton de visualisation
        visualisationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer une nouvelle activité pour visualiser les signaux électrophysiologiques
                Intent intent = new Intent(Acceuil.this, Visualisation.class);
                startActivity(intent);
            }
        });

        // Ajouter un écouteur de clic au bouton de déconnexion
        deconnexionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Déconnecter l'utilisateur et retourner à la page d'authentification
                Intent intent = new Intent(Acceuil.this, Authentification.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
